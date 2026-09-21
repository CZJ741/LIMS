package com.lims.common.process.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lims.common.constant.LimsConstants;
import com.lims.common.exception.BizException;
import com.lims.common.model.PageReq;
import com.lims.common.model.PageResp;
import com.lims.common.process.entity.ProcessBusinessRelation;
import com.lims.common.process.entity.ProcessRejectRecord;
import com.lims.common.process.mapper.ProcessBusinessRelationMapper;
import com.lims.common.process.mapper.ProcessRejectRecordMapper;
import com.lims.common.process.model.ContractTransactionDTO;
import com.lims.common.process.model.HistoricActivityDTO;
import com.lims.common.process.model.ProcessInstanceDTO;
import com.lims.common.process.model.TaskDTO;
import com.lims.common.process.model.TraceTimelineDTO;
import com.lims.common.process.service.ProcessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.FlowNode;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessServiceImpl implements ProcessService {

    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final HistoryService historyService;
    private final RepositoryService repositoryService;
    private final ProcessBusinessRelationMapper relationMapper;
    private final ProcessRejectRecordMapper rejectRecordMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String startProcess(String processKey, String businessKey, Map<String, Object> variables) {
        if (StrUtil.isBlank(processKey) || StrUtil.isBlank(businessKey)) {
            throw new BizException("流程启动失败：流程定义Key或业务Key不能为空");
        }
        if (variables == null) {
            variables = new HashMap<>();
        }

        String currentUserId = getSafeCurrentUserId();
        variables.put("initiator", currentUserId);

        // 启动 Flowable 实例
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processKey, businessKey, variables);
        String processInstanceId = processInstance.getId();

        // 提取阶段标识
        String stage = deduceStageFromProcessKey(processKey);

        // 写入业务-流程关联记录
        ProcessBusinessRelation relation = ProcessBusinessRelation.builder()
                .businessKey(businessKey)
                .stage(stage)
                .processKey(processKey)
                .processInstanceId(processInstanceId)
                .status("RUNNING")
                .createBy(currentUserId)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .isDeleted(0)
                .build();
        relationMapper.insert(relation);

        log.info("成功启动流程实例, processKey={}, businessKey={}, processInstanceId={}",
                processKey, businessKey, processInstanceId);
        return processInstanceId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeTask(String taskId, Map<String, Object> variables) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new BizException("任务不存在或已被处理: " + taskId);
        }

        if (variables == null) {
            variables = new HashMap<>();
        }
        taskService.complete(taskId, variables);

        // 判断流程实例是否结束，若结束更新业务关联状态
        ProcessInstance instance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(task.getProcessInstanceId())
                .singleResult();
        if (instance == null) {
            updateRelationStatus(task.getProcessInstanceId(), "COMPLETED");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rejectToPrevious(String taskId, String reason) {
        validateReason(reason);

        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new BizException("待驳回任务不存在或已处理: " + taskId);
        }

        String processDefinitionId = task.getProcessDefinitionId();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        FlowNode currentFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(task.getTaskDefinitionKey());

        // 寻找上一节点 (优先找上游的 UserTask)
        FlowNode targetNode = findPreviousUserTask(currentFlowNode);
        if (targetNode == null) {
            throw new BizException("未找到可驳回的上游任务节点");
        }

        executeChangeActivityState(task, targetNode.getId(), reason, "REJECT");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rollbackTo(String taskId, String targetActivityId, String reason) {
        validateReason(reason);

        if (StrUtil.isBlank(targetActivityId)) {
            throw new BizException("回退目标节点不能为空");
        }

        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new BizException("待回退任务不存在或已处理: " + taskId);
        }

        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        FlowElement targetElement = bpmnModel.getMainProcess().getFlowElement(targetActivityId);
        if (targetElement == null) {
            throw new BizException("目标节点在当前流程定义中不存在: " + targetActivityId);
        }

        executeChangeActivityState(task, targetActivityId, reason, "ROLLBACK");
    }

    private void executeChangeActivityState(Task task, String targetActivityId, String reason, String type) {
        String processInstanceId = task.getProcessInstanceId();
        String currentTaskId = task.getId();
        String currentActivityId = task.getTaskDefinitionKey();
        String currentActivityName = task.getName();

        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        FlowElement targetElement = bpmnModel.getMainProcess().getFlowElement(targetActivityId);
        String targetActivityName = targetElement != null ? targetElement.getName() : targetActivityId;

        // Flowable 7.x 运行时动态跳转/回退
        runtimeService.createChangeActivityStateBuilder()
                .processInstanceId(processInstanceId)
                .moveActivityIdTo(currentActivityId, targetActivityId)
                .changeState();

        // 记录驳回轨迹审计
        String currentUserId = getSafeCurrentUserId();
        ProcessRejectRecord record = ProcessRejectRecord.builder()
                .businessKey(getBusinessKeyByProcessInstanceId(processInstanceId))
                .processKey(task.getProcessDefinitionId().split(":")[0])
                .processInstanceId(processInstanceId)
                .currentTaskId(currentTaskId)
                .currentTaskName(currentActivityName)
                .targetActivityId(targetActivityId)
                .targetActivityName(targetActivityName)
                .rejectType(type)
                .reason(reason)
                .rejectedBy(currentUserId)
                .rejectedByName(currentUserId)
                .rejectedByPosition(task.getCategory())
                .operatorIp("127.0.0.1")
                .previousStatus("AUDITING")
                .currentStatus("REJECTED")
                .rejectedAt(LocalDateTime.now())
                .isDeleted(0)
                .build();
        rejectRecordMapper.insert(record);

        // 添加评论留痕
        taskService.addComment(currentTaskId, processInstanceId, type + ": " + reason);

        log.info("成功执行流程节点变更: taskId={}, type={}, from={} to={}, reason={}",
                currentTaskId, type, currentActivityId, targetActivityId, reason);
    }

    @Override
    public List<HistoricActivityDTO> getHistoricActivities(String processInstanceId) {
        List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                .orderByHistoricActivityInstanceStartTime().asc()
                .list();

        return list.stream().map(h -> HistoricActivityDTO.builder()
                .activityId(h.getActivityId())
                .activityName(h.getActivityName())
                .activityType(h.getActivityType())
                .assignee(h.getAssignee())
                .startTime(h.getStartTime())
                .endTime(h.getEndTime())
                .durationInMillis(h.getDurationInMillis())
                .build()
        ).collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> getActiveTasksByUser(String userId) {
        List<String> userPositions = getUserPositions(userId);

        TaskQuery query = taskService.createTaskQuery().active();
        if (CollUtil.isNotEmpty(userPositions)) {
            query.or().taskCandidateOrAssigned(userId).taskCandidateGroupIn(userPositions).endOr();
        } else {
            query.taskCandidateOrAssigned(userId);
        }

        List<Task> tasks = query.orderByTaskCreateTime().desc().list();
        return tasks.stream().map(this::convertTaskToDTO).collect(Collectors.toList());
    }

    @Override
    public TraceTimelineDTO getTraceTimeline(String businessKey) {
        if (StrUtil.isBlank(businessKey)) {
            throw new BizException("业务唯一标识不能为空");
        }

        LambdaQueryWrapper<ProcessBusinessRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProcessBusinessRelation::getBusinessKey, businessKey)
                .orderByAsc(ProcessBusinessRelation::getId);
        List<ProcessBusinessRelation> relations = relationMapper.selectList(wrapper);

        List<TraceTimelineDTO.StageTimeline> stageTimelines = new ArrayList<>();

        for (ProcessBusinessRelation rel : relations) {
            String procInstId = rel.getProcessInstanceId();

            List<HistoricActivityDTO> activities = getHistoricActivities(procInstId);
            List<ProcessRejectRecord> rejects = rejectRecordMapper.selectList(
                    new LambdaQueryWrapper<ProcessRejectRecord>()
                            .eq(ProcessRejectRecord::getProcessInstanceId, procInstId)
                            .orderByAsc(ProcessRejectRecord::getRejectedAt)
            );

            List<TraceTimelineDTO.NodeAuditLog> nodeLogs = new ArrayList<>();
            for (HistoricActivityDTO act : activities) {
                if ("userTask".equals(act.getActivityType())) {
                    nodeLogs.add(TraceTimelineDTO.NodeAuditLog.builder()
                            .nodeName(act.getActivityName())
                            .operator(act.getAssignee())
                            .operatorName(act.getAssignee())
                            .operatorPosition(act.getCandidateGroup())
                            .actionType(act.getEndTime() != null ? "COMPLETE" : "WAITING")
                            .commentOrReason(act.getComment())
                            .operateTime(act.getEndTime() != null ? act.getEndTime() : act.getStartTime())
                            .build());
                }
            }

            for (ProcessRejectRecord rej : rejects) {
                nodeLogs.add(TraceTimelineDTO.NodeAuditLog.builder()
                        .nodeName(rej.getCurrentTaskName())
                        .operator(rej.getRejectedBy())
                        .operatorName(rej.getRejectedByName())
                        .operatorPosition(rej.getRejectedByPosition())
                        .actionType(rej.getRejectType())
                        .commentOrReason(rej.getReason())
                        .operateTime(java.sql.Timestamp.valueOf(rej.getRejectedAt()))
                        .operatorIp(rej.getOperatorIp())
                        .build());
            }

            stageTimelines.add(TraceTimelineDTO.StageTimeline.builder()
                    .stage(rel.getStage())
                    .processKey(rel.getProcessKey())
                    .processInstanceId(procInstId)
                    .status(rel.getStatus())
                    .nodes(nodeLogs)
                    .build());
        }

        return TraceTimelineDTO.builder()
                .businessKey(businessKey)
                .stages(stageTimelines)
                .build();
    }

    @Override
    public PageResp<ProcessInstanceDTO> getMyApplications(String userId, PageReq pageReq) {
        String safeUser = StrUtil.isNotBlank(userId) ? userId : getSafeCurrentUserId();

        HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery()
                .startedBy(safeUser)
                .orderByProcessInstanceStartTime().desc();

        long total = query.count();
        int offset = (pageReq.getCurrent() - 1) * pageReq.getSize();
        List<HistoricProcessInstance> list = query.listPage(offset, pageReq.getSize());

        List<ProcessInstanceDTO> records = list.stream().map(h -> ProcessInstanceDTO.builder()
                .processInstanceId(h.getId())
                .businessKey(h.getBusinessKey())
                .processDefinitionKey(h.getProcessDefinitionKey())
                .processDefinitionName(h.getProcessDefinitionName())
                .startUserId(h.getStartUserId())
                .startTime(h.getStartTime())
                .endTime(h.getEndTime())
                .ended(h.getEndTime() != null)
                .build()
        ).collect(Collectors.toList());

        long pages = (total + pageReq.getSize() - 1) / pageReq.getSize();
        return new PageResp<>(pageReq.getCurrent(), pageReq.getSize(), total, pages, records);
    }

    @Override
    public PageResp<TaskDTO> getMyPendingTasks(String userId, PageReq pageReq) {
        String safeUser = StrUtil.isNotBlank(userId) ? userId : getSafeCurrentUserId();
        List<String> userPositions = getUserPositions(safeUser);

        TaskQuery query = taskService.createTaskQuery().active();
        if (CollUtil.isNotEmpty(userPositions)) {
            query.or().taskCandidateOrAssigned(safeUser).taskCandidateGroupIn(userPositions).endOr();
        } else {
            query.taskCandidateOrAssigned(safeUser);
        }
        query.orderByTaskCreateTime().desc();

        long total = query.count();
        int offset = (pageReq.getCurrent() - 1) * pageReq.getSize();
        List<Task> tasks = query.listPage(offset, pageReq.getSize());

        List<TaskDTO> records = tasks.stream().map(this::convertTaskToDTO).collect(Collectors.toList());
        long pages = (total + pageReq.getSize() - 1) / pageReq.getSize();
        return new PageResp<>(pageReq.getCurrent(), pageReq.getSize(), total, pages, records);
    }

    @Override
    public PageResp<ContractTransactionDTO> getMyTransactions(PageReq pageReq) {
        // 聚合合同维度业务
        LambdaQueryWrapper<ProcessBusinessRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(ProcessBusinessRelation::getBusinessKey)
                .groupBy(ProcessBusinessRelation::getBusinessKey)
                .orderByDesc(ProcessBusinessRelation::getId);

        List<ProcessBusinessRelation> distinctKeys = relationMapper.selectList(wrapper);
        long total = distinctKeys.size();
        int offset = (pageReq.getCurrent() - 1) * pageReq.getSize();
        List<ProcessBusinessRelation> paged = distinctKeys.stream()
                .skip(offset)
                .limit(pageReq.getSize())
                .collect(Collectors.toList());

        List<ContractTransactionDTO> resultList = new ArrayList<>();
        for (ProcessBusinessRelation r : paged) {
            String bKey = r.getBusinessKey();
            List<ProcessBusinessRelation> allRels = relationMapper.selectList(
                    new LambdaQueryWrapper<ProcessBusinessRelation>().eq(ProcessBusinessRelation::getBusinessKey, bKey)
            );

            List<ContractTransactionDTO.StageSummary> summaries = allRels.stream().map(rel ->
                    ContractTransactionDTO.StageSummary.builder()
                            .stage(rel.getStage())
                            .stageName(rel.getStage())
                            .processInstanceId(rel.getProcessInstanceId())
                            .status(rel.getStatus())
                            .build()
            ).collect(Collectors.toList());

            // 查找该业务当前待办任务
            List<String> instanceIds = allRels.stream()
                    .map(ProcessBusinessRelation::getProcessInstanceId)
                    .collect(Collectors.toList());
            List<TaskDTO> pendingTasks = new ArrayList<>();
            if (CollUtil.isNotEmpty(instanceIds)) {
                List<Task> taskList = taskService.createTaskQuery()
                        .processInstanceIdIn(instanceIds)
                        .active()
                        .list();
                pendingTasks = taskList.stream().map(this::convertTaskToDTO).collect(Collectors.toList());
            }

            resultList.add(ContractTransactionDTO.builder()
                    .contractNo(bKey)
                    .currentStage(allRels.get(allRels.size() - 1).getStage())
                    .overallStatus(pendingTasks.isEmpty() ? "已完成" : "流转中")
                    .pendingTasks(pendingTasks)
                    .stageSummaries(summaries)
                    .build());
        }

        long pages = (total + pageReq.getSize() - 1) / pageReq.getSize();
        return new PageResp<>(pageReq.getCurrent(), pageReq.getSize(), total, pages, resultList);
    }

    private void validateReason(String reason) {
        if (StrUtil.isBlank(reason) || reason.trim().length() < 10) {
            throw new BizException("操作失败：驳回或回退必须填写详细原因，且不少于 10 个字符！");
        }
    }

    private void updateRelationStatus(String processInstanceId, String status) {
        ProcessBusinessRelation entity = relationMapper.selectOne(
                new LambdaQueryWrapper<ProcessBusinessRelation>()
                        .eq(ProcessBusinessRelation::getProcessInstanceId, processInstanceId)
        );
        if (entity != null) {
            entity.setStatus(status);
            entity.setUpdateTime(LocalDateTime.now());
            relationMapper.updateById(entity);
        }
    }

    private String getBusinessKeyByProcessInstanceId(String processInstanceId) {
        ProcessInstance pi = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        if (pi != null && pi.getBusinessKey() != null) {
            return pi.getBusinessKey();
        }
        HistoricProcessInstance hpi = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        return hpi != null ? hpi.getBusinessKey() : "";
    }

    private FlowNode findPreviousUserTask(FlowNode current) {
        List<SequenceFlow> incoming = current.getIncomingFlows();
        if (CollUtil.isEmpty(incoming)) {
            return null;
        }
        for (SequenceFlow sf : incoming) {
            FlowElement source = sf.getSourceFlowElement();
            if (source instanceof UserTask) {
                return (FlowNode) source;
            } else if (source instanceof FlowNode) {
                FlowNode prev = findPreviousUserTask((FlowNode) source);
                if (prev != null) {
                    return prev;
                }
            }
        }
        return null;
    }

    private TaskDTO convertTaskToDTO(Task t) {
        String bKey = "";
        ProcessInstance pi = runtimeService.createProcessInstanceQuery()
                .processInstanceId(t.getProcessInstanceId())
                .singleResult();
        if (pi != null) {
            bKey = pi.getBusinessKey();
        }
        return TaskDTO.builder()
                .taskId(t.getId())
                .taskName(t.getName())
                .taskDefinitionKey(t.getTaskDefinitionKey())
                .processInstanceId(t.getProcessInstanceId())
                .processDefinitionKey(t.getProcessDefinitionId() != null ? t.getProcessDefinitionId().split(":")[0] : "")
                .category(t.getCategory())
                .businessKey(bKey)
                .assignee(t.getAssignee())
                .createTime(t.getCreateTime())
                .description(t.getDescription())
                .processVariables(taskService.getVariables(t.getId()))
                .build();
    }

    private String getSafeCurrentUserId() {
        try {
            if (StpUtil.isLogin()) {
                return StpUtil.getLoginIdAsString();
            }
        } catch (Exception ignored) {
        }
        return "admin";
    }

    private List<String> getUserPositions(String userId) {
        try {
            if (StpUtil.isLogin()) {
                return StpUtil.getRoleList();
            }
        } catch (Exception ignored) {
        }
        return Collections.singletonList(LimsConstants.Position.FINANCE_SPECIALIST);
    }

    private String deduceStageFromProcessKey(String processKey) {
        if (processKey.contains("contract")) return LimsConstants.Stage.CONTRACT;
        if (processKey.contains("entrust")) return LimsConstants.Stage.ENTRUST;
        if (processKey.contains("sampling")) return LimsConstants.Stage.SAMPLING;
        if (processKey.contains("detection")) return LimsConstants.Stage.DETECTION;
        if (processKey.contains("report")) return LimsConstants.Stage.REPORT;
        if (processKey.contains("settle")) return LimsConstants.Stage.SETTLEMENT;
        return "UNKNOWN";
    }
}

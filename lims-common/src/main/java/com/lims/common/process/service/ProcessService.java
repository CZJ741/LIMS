package com.lims.common.process.service;

import com.lims.common.model.PageReq;
import com.lims.common.model.PageResp;
import com.lims.common.process.model.ContractTransactionDTO;
import com.lims.common.process.model.HistoricActivityDTO;
import com.lims.common.process.model.ProcessInstanceDTO;
import com.lims.common.process.model.TaskDTO;
import com.lims.common.process.model.TraceTimelineDTO;

import java.util.List;
import java.util.Map;

/**
 * 核心流程引擎服务接口
 */
public interface ProcessService {

    /**
     * 启动流程实例并绑定业务关联
     *
     * @param processKey  流程定义Key (如 contract-audit)
     * @param businessKey 业务单据号 (如 HT-2026-0001)
     * @param variables   初始流程变量
     * @return 流程实例ID
     */
    String startProcess(String processKey, String businessKey, Map<String, Object> variables);

    /**
     * 完成待办审批任务 (通过)
     *
     * @param taskId    任务ID
     * @param variables 附带流程变量 (如 auditPass=true)
     */
    void completeTask(String taskId, Map<String, Object> variables);

    /**
     * 驳回到上一节点 (强制原因 >= 10 字)
     *
     * @param taskId 任务ID
     * @param reason 驳回原因
     */
    void rejectToPrevious(String taskId, String reason);

    /**
     * 动态自由回退到任意上游节点 (强制原因 >= 10 字)
     *
     * @param taskId           当前任务ID
     * @param targetActivityId 目标活动ActivityId
     * @param reason           回退原因
     */
    void rollbackTo(String taskId, String targetActivityId, String reason);

    /**
     * 查询指定流程实例的全部活动历史留痕
     *
     * @param processInstanceId 流程实例ID
     * @return 历史活动列表
     */
    List<HistoricActivityDTO> getHistoricActivities(String processInstanceId);

    /**
     * 根据用户与角色查询待办审批任务
     *
     * @param userId 用户账号/ID
     * @return 待办任务集合
     */
    List<TaskDTO> getActiveTasksByUser(String userId);

    /**
     * 全链路业务追溯：按业务单据号聚合所有流程阶段留痕与驳回历史
     *
     * @param businessKey 业务唯一编号
     * @return 全流程追溯数据
     */
    TraceTimelineDTO getTraceTimeline(String businessKey);

    /**
     * 分页查询当前用户发起的流程实例
     *
     * @param userId  发起人账号/ID
     * @param pageReq 分页请求
     * @return 分页结果
     */
    PageResp<ProcessInstanceDTO> getMyApplications(String userId, PageReq pageReq);

    /**
     * 分页查询当前待我处理的 UserTask
     *
     * @param userId  用户账号/ID
     * @param pageReq 分页请求
     * @return 分页结果
     */
    PageResp<TaskDTO> getMyPendingTasks(String userId, PageReq pageReq);

    /**
     * 跨阶段聚合：按合同维度聚合待办、进行中、已完成事务
     *
     * @param pageReq 分页请求
     * @return 分页结果
     */
    PageResp<ContractTransactionDTO> getMyTransactions(PageReq pageReq);
}

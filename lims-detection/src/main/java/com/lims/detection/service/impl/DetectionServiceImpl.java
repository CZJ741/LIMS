package com.lims.detection.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lims.common.enums.DetectionStatus;
import com.lims.common.exception.BizException;
import com.lims.common.model.IdResp;
import com.lims.common.model.PageReq;
import com.lims.common.model.PageResp;
import com.lims.common.process.service.ProcessService;
import com.lims.common.result.LimsBizErrorCode;
import com.lims.common.util.BusinessNumberGenerator;
import com.lims.detection.entity.DetectionTask;
import com.lims.detection.mapper.DetectionTaskMapper;
import com.lims.detection.model.DetectionTaskCreateReq;
import com.lims.detection.model.DetectionTaskVO;
import com.lims.detection.service.DetectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DetectionServiceImpl implements DetectionService {

    private final DetectionTaskMapper detectionTaskMapper;
    private final ProcessService processService;
    private final BusinessNumberGenerator businessNumberGenerator;
    private static final String PROCESS_KEY = "detection-process";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IdResp createDetectionTask(DetectionTaskCreateReq req) {
        String code = businessNumberGenerator.generate(BusinessNumberGenerator.BusinessType.DETECTION);
        Long userId = getSafeUserId();

        DetectionTask task = DetectionTask.builder()
                .taskCode(code)
                .entrustId(req.getEntrustId())
                .labHeadId(userId)
                .assignTime(LocalDateTime.now())
                .deadline(req.getDeadline())
                .status("PENDING")
                .isDeleted(0)
                .createBy(userId)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        detectionTaskMapper.insert(task);
        log.info("创建检测任务成功: id={}, code={}", task.getId(), code);
        return IdResp.of(task.getId(), code);
    }

    @Override
    public PageResp<DetectionTaskVO> pageDetectionTasks(PageReq req) {
        Page<DetectionTask> page = new Page<>(req.getCurrent(), req.getSize());
        LambdaQueryWrapper<DetectionTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DetectionTask::getIsDeleted, 0);

        if (StrUtil.isNotBlank(req.getKeyword())) {
            wrapper.like(DetectionTask::getTaskCode, req.getKeyword());
        }
        wrapper.orderByDesc(DetectionTask::getId);

        Page<DetectionTask> res = detectionTaskMapper.selectPage(page, wrapper);
        List<DetectionTaskVO> vos = res.getRecords().stream().map(this::convertToVO).collect(Collectors.toList());
        return PageResp.of(res.getCurrent(), res.getSize(), res.getTotal(), vos);
    }

    @Override
    public DetectionTaskVO getDetectionTaskById(Long id) {
        DetectionTask t = detectionTaskMapper.selectById(id);
        if (t == null || t.getIsDeleted() == 1) {
            throw new BizException(LimsBizErrorCode.DETECTION_TASK_NOT_FOUND);
        }
        return convertToVO(t);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String receiveSample(String detectionNo, Map<String, Object> variables) {
        if (variables == null) {
            variables = new HashMap<>();
        }
        variables.put("detectionStatus", DetectionStatus.RECEIVED.getCode());
        log.info("实验室接收样品启动流程, detectionNo={}", detectionNo);
        return processService.startProcess(PROCESS_KEY, detectionNo, variables);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignTask(String taskId, String analystId) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("analystId", analystId);
        vars.put("detectionStatus", DetectionStatus.TESTING.getCode());
        processService.completeTask(taskId, vars);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitResult(String taskId, Map<String, Object> variables) {
        if (variables == null) {
            variables = new HashMap<>();
        }
        variables.put("detectionStatus", DetectionStatus.PENDING_RECHECK.getCode());
        processService.completeTask(taskId, variables);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recheckResult(String taskId, String comment) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("recheckPass", true);
        vars.put("comment", comment);
        vars.put("detectionStatus", DetectionStatus.COMPLETED.getCode());
        processService.completeTask(taskId, vars);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rejectResult(String taskId, String reason) {
        processService.rejectToPrevious(taskId, reason);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rollback(String taskId, String targetActivityId, String reason) {
        processService.rollbackTo(taskId, targetActivityId, reason);
    }

    @Override
    public DetectionStatus getStatus(String detectionNo) {
        return DetectionStatus.PENDING_RECEIVE;
    }

    private DetectionTaskVO convertToVO(DetectionTask t) {
        return DetectionTaskVO.builder()
                .id(t.getId())
                .taskCode(t.getTaskCode())
                .entrustId(t.getEntrustId())
                .labHeadName("实验室主任")
                .assignTime(t.getAssignTime())
                .deadline(t.getDeadline())
                .status(t.getStatus())
                .statusName("待检测")
                .build();
    }

    private Long getSafeUserId() {
        try {
            if (StpUtil.isLogin()) {
                return StpUtil.getLoginIdAsLong();
            }
        } catch (Exception ignored) {
        }
        return 1L;
    }
}

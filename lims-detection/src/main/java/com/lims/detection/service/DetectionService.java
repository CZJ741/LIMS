package com.lims.detection.service;

import com.lims.common.enums.DetectionStatus;
import com.lims.common.model.IdResp;
import com.lims.common.model.PageReq;
import com.lims.common.model.PageResp;
import com.lims.detection.model.DetectionTaskCreateReq;
import com.lims.detection.model.DetectionTaskVO;

import java.util.Map;

/**
 * 阶段四：检测分析业务状态机驱动服务与CRUD
 */
public interface DetectionService {

    /**
     * 创建并下发检测任务
     */
    IdResp createDetectionTask(DetectionTaskCreateReq req);

    /**
     * 分页查询检测任务
     */
    PageResp<DetectionTaskVO> pageDetectionTasks(PageReq req);

    /**
     * 任务详情
     */
    DetectionTaskVO getDetectionTaskById(Long id);

    /**
     * 接收样品并启动检测流程
     */
    String receiveSample(String detectionNo, Map<String, Object> variables);

    /**
     * 任务分发认领
     */
    void assignTask(String taskId, String analystId);

    /**
     * 录入结果并提交复审
     */
    void submitResult(String taskId, Map<String, Object> variables);

    /**
     * 实验数据复审通过
     */
    void recheckResult(String taskId, String comment);

    /**
     * 实验数据复审驳回重测
     */
    void rejectResult(String taskId, String reason);

    /**
     * 回退
     */
    void rollback(String taskId, String targetActivityId, String reason);

    DetectionStatus getStatus(String detectionNo);
}

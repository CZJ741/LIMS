package com.lims.sampling.service;

import com.lims.common.enums.SamplingStatus;
import com.lims.common.model.IdResp;
import com.lims.common.model.PageReq;
import com.lims.common.model.PageResp;
import com.lims.sampling.model.SamplingTaskCreateReq;
import com.lims.sampling.model.SamplingTaskVO;

import java.util.Map;

/**
 * 阶段三：采样业务状态机驱动服务与CRUD
 */
public interface SamplingService {

    /**
     * 创建采样任务
     */
    IdResp createSamplingTask(SamplingTaskCreateReq req);

    /**
     * 分页查询采样任务
     */
    PageResp<SamplingTaskVO> pageSamplingTasks(PageReq req);

    /**
     * 查询采样任务详情
     */
    SamplingTaskVO getSamplingTaskById(Long id);

    /**
     * 采样任务准备阶段
     */
    String startPrepare(String samplingNo, Map<String, Object> variables);

    /**
     * 物料仪器准备完毕
     */
    void finishPrepare(String taskId, Map<String, Object> variables);

    /**
     * 现场采样执行完成并提交审核
     */
    void collectSample(String taskId, Map<String, Object> variables);

    /**
     * 采样记录审核通过
     */
    void auditSample(String taskId, String comment);

    /**
     * 采样记录驳回重采
     */
    void rejectSample(String taskId, String reason);

    /**
     * 回退到指定节点
     */
    void rollback(String taskId, String targetActivityId, String reason);

    SamplingStatus getStatus(String samplingNo);
}

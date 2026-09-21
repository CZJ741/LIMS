package com.lims.entrust.service;

import com.lims.common.enums.EntrustStatus;
import com.lims.common.model.IdResp;
import com.lims.common.model.PageReq;
import com.lims.common.model.PageResp;
import com.lims.entrust.model.EntrustCreateReq;
import com.lims.entrust.model.EntrustVO;

import java.util.Map;

/**
 * 阶段二：委托下单业务状态机驱动服务与CRUD
 */
public interface EntrustService {

    /**
     * 新增委托单
     */
    IdResp createEntrust(EntrustCreateReq req);

    /**
     * 分页查询委托单
     */
    PageResp<EntrustVO> pageEntrusts(PageReq req);

    /**
     * 详情
     */
    EntrustVO getEntrustById(Long id);

    /**
     * 基于合同创建委托单并启动流程
     */
    String createOrder(String entrustNo, Map<String, Object> variables);

    /**
     * 取消委托
     */
    void cancel(String taskId, String reason);

    /**
     * 完成委托任务
     */
    void complete(String taskId, Map<String, Object> variables);

    /**
     * 驳回
     */
    void reject(String taskId, String reason);

    /**
     * 回退
     */
    void rollback(String taskId, String targetActivityId, String reason);

    EntrustStatus getStatus(String entrustNo);
}

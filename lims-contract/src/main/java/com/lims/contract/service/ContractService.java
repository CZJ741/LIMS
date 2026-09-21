package com.lims.contract.service;

import com.lims.common.enums.ContractStatus;
import com.lims.common.model.IdResp;
import com.lims.common.model.PageReq;
import com.lims.common.model.PageResp;
import com.lims.contract.model.ContractCreateReq;
import com.lims.contract.model.ContractUpdateReq;
import com.lims.contract.model.ContractVO;

import java.util.Map;

/**
 * 阶段一：合同业务状态机驱动与CRUD服务
 */
public interface ContractService {

    /**
     * 新增合同草稿并生成业务编号
     */
    IdResp createContract(ContractCreateReq req);

    /**
     * 更新合同信息
     */
    void updateContract(Long id, ContractUpdateReq req);

    /**
     * 逻辑删除合同
     */
    void deleteContract(Long id);

    /**
     * 查询合同详情
     */
    ContractVO getContractById(Long id);

    /**
     * 分页多条件查询合同列表
     */
    PageResp<ContractVO> pageContracts(PageReq req);

    /**
     * 我的历史合同列表
     */
    PageResp<ContractVO> getMyHistoricalContracts(PageReq req);

    /**
     * 提交审核并启动流程
     */
    String submitAudit(Long id);

    /**
     * 财务审核通过
     */
    void approve(String taskId, String comment);

    /**
     * 财务审核驳回 (原因需 >= 10 字)
     */
    void reject(String taskId, String reason);

    /**
     * 回退到指定历史节点
     */
    void rollback(String taskId, String targetActivityId, String reason);

    /**
     * 获取合同当前状态
     */
    ContractStatus getStatus(String contractId);
}

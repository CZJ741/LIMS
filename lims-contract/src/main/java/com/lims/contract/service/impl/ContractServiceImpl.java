package com.lims.contract.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lims.common.enums.ContractStatus;
import com.lims.common.exception.BizException;
import com.lims.common.model.IdResp;
import com.lims.common.model.PageReq;
import com.lims.common.model.PageResp;
import com.lims.common.process.service.ProcessService;
import com.lims.common.result.LimsBizErrorCode;
import com.lims.common.util.BusinessNumberGenerator;
import com.lims.contract.entity.Contract;
import com.lims.contract.mapper.ContractMapper;
import com.lims.contract.model.ContractCreateReq;
import com.lims.contract.model.ContractUpdateReq;
import com.lims.contract.model.ContractVO;
import com.lims.contract.service.ContractService;
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
public class ContractServiceImpl implements ContractService {

    private final ContractMapper contractMapper;
    private final ProcessService processService;
    private final BusinessNumberGenerator businessNumberGenerator;
    private static final String PROCESS_KEY = "contract-audit";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IdResp createContract(ContractCreateReq req) {
        String contractCode = businessNumberGenerator.generate(BusinessNumberGenerator.BusinessType.CONTRACT);
        Long currentUserId = getSafeUserId();

        Contract contract = Contract.builder()
                .contractCode(contractCode)
                .contractName(req.getContractName())
                .clientCompany(req.getClientCompany())
                .clientContact(req.getClientContact())
                .clientPhone(req.getClientPhone())
                .totalAmount(req.getTotalAmount())
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .salesUserId(currentUserId)
                .status("DRAFT")
                .attachmentId(req.getAttachmentId())
                .remark(req.getRemark())
                .isDeleted(0)
                .createBy(currentUserId)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        contractMapper.insert(contract);
        log.info("新建合同成功, id={}, code={}", contract.getId(), contractCode);
        return IdResp.of(contract.getId(), contractCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateContract(Long id, ContractUpdateReq req) {
        Contract contract = getEntityById(id);
        if (!"DRAFT".equalsIgnoreCase(contract.getStatus()) && !"REJECTED".equalsIgnoreCase(contract.getStatus())) {
            throw new BizException(LimsBizErrorCode.CONTRACT_CANNOT_EDIT);
        }

        if (StrUtil.isNotBlank(req.getContractName())) contract.setContractName(req.getContractName());
        if (StrUtil.isNotBlank(req.getClientCompany())) contract.setClientCompany(req.getClientCompany());
        if (StrUtil.isNotBlank(req.getClientContact())) contract.setClientContact(req.getClientContact());
        if (StrUtil.isNotBlank(req.getClientPhone())) contract.setClientPhone(req.getClientPhone());
        if (req.getTotalAmount() != null) contract.setTotalAmount(req.getTotalAmount());
        if (req.getStartDate() != null) contract.setStartDate(req.getStartDate());
        if (req.getEndDate() != null) contract.setEndDate(req.getEndDate());
        if (req.getAttachmentId() != null) contract.setAttachmentId(req.getAttachmentId());
        if (req.getRemark() != null) contract.setRemark(req.getRemark());
        contract.setUpdateTime(LocalDateTime.now());
        contract.setUpdateBy(getSafeUserId());

        contractMapper.updateById(contract);
        log.info("修改合同成功, id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteContract(Long id) {
        Contract contract = getEntityById(id);
        contract.setIsDeleted(1);
        contract.setUpdateTime(LocalDateTime.now());
        contract.setUpdateBy(getSafeUserId());
        contractMapper.updateById(contract);
        log.info("逻辑删除合同成功, id={}", id);
    }

    @Override
    public ContractVO getContractById(Long id) {
        Contract contract = getEntityById(id);
        return convertToVO(contract);
    }

    @Override
    public PageResp<ContractVO> pageContracts(PageReq req) {
        Page<Contract> page = new Page<>(req.getCurrent(), req.getSize());
        LambdaQueryWrapper<Contract> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Contract::getIsDeleted, 0);

        if (StrUtil.isNotBlank(req.getKeyword())) {
            wrapper.and(w -> w.like(Contract::getContractName, req.getKeyword())
                    .or().like(Contract::getContractCode, req.getKeyword())
                    .or().like(Contract::getClientCompany, req.getKeyword()));
        }
        if (req.getStatus() != null) {
            ContractStatus st = ContractStatus.fromCode(req.getStatus());
            if (st != null) {
                wrapper.eq(Contract::getStatus, st.name());
            }
        }
        wrapper.orderByDesc(Contract::getId);

        Page<Contract> result = contractMapper.selectPage(page, wrapper);
        List<ContractVO> vos = result.getRecords().stream().map(this::convertToVO).collect(Collectors.toList());
        return PageResp.of(result.getCurrent(), result.getSize(), result.getTotal(), vos);
    }

    @Override
    public PageResp<ContractVO> getMyHistoricalContracts(PageReq req) {
        Long userId = getSafeUserId();
        Page<Contract> page = new Page<>(req.getCurrent(), req.getSize());
        LambdaQueryWrapper<Contract> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Contract::getIsDeleted, 0)
                .eq(Contract::getSalesUserId, userId)
                .orderByDesc(Contract::getId);

        Page<Contract> result = contractMapper.selectPage(page, wrapper);
        List<ContractVO> vos = result.getRecords().stream().map(this::convertToVO).collect(Collectors.toList());
        return PageResp.of(result.getCurrent(), result.getSize(), result.getTotal(), vos);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String submitAudit(Long id) {
        Contract contract = getEntityById(id);
        if (!"DRAFT".equalsIgnoreCase(contract.getStatus()) && !"REJECTED".equalsIgnoreCase(contract.getStatus())) {
            throw new BizException(LimsBizErrorCode.CONTRACT_STATUS_INVALID);
        }

        Map<String, Object> vars = new HashMap<>();
        vars.put("contractId", contract.getId());
        vars.put("contractCode", contract.getContractCode());
        vars.put("totalAmount", contract.getTotalAmount());
        vars.put("salesUserId", contract.getSalesUserId());

        String procInstId = processService.startProcess(PROCESS_KEY, contract.getContractCode(), vars);
        contract.setStatus("PENDING");
        contract.setUpdateTime(LocalDateTime.now());
        contractMapper.updateById(contract);

        log.info("合同提交审核并启动流程, contractCode={}, procInstId={}", contract.getContractCode(), procInstId);
        return procInstId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(String taskId, String comment) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("auditPass", true);
        vars.put("comment", comment);
        processService.completeTask(taskId, vars);
        log.info("合同任务审批通过, taskId={}", taskId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reject(String taskId, String reason) {
        processService.rejectToPrevious(taskId, reason);
        log.info("合同任务审批驳回, taskId={}, reason={}", taskId, reason);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rollback(String taskId, String targetActivityId, String reason) {
        processService.rollbackTo(taskId, targetActivityId, reason);
        log.info("合同任务审批回退, taskId={}, targetActivityId={}", taskId, targetActivityId);
    }

    @Override
    public ContractStatus getStatus(String contractId) {
        Contract c = contractMapper.selectOne(new LambdaQueryWrapper<Contract>().eq(Contract::getContractCode, contractId));
        return c != null ? ContractStatus.valueOf(c.getStatus()) : ContractStatus.PENDING;
    }

    private Contract getEntityById(Long id) {
        Contract c = contractMapper.selectById(id);
        if (c == null || c.getIsDeleted() == 1) {
            throw new BizException(LimsBizErrorCode.CONTRACT_NOT_FOUND);
        }
        return c;
    }

    private ContractVO convertToVO(Contract c) {
        String sName = "草稿";
        if ("PENDING".equalsIgnoreCase(c.getStatus())) sName = "待审核";
        else if ("APPROVED".equalsIgnoreCase(c.getStatus())) sName = "已审核";
        else if ("REJECTED".equalsIgnoreCase(c.getStatus())) sName = "已驳回";
        else if ("ARCHIVED".equalsIgnoreCase(c.getStatus())) sName = "已归档";

        return ContractVO.builder()
                .id(c.getId())
                .contractCode(c.getContractCode())
                .contractName(c.getContractName())
                .clientCompany(c.getClientCompany())
                .clientContact(c.getClientContact())
                .clientPhone(c.getClientPhone())
                .totalAmount(c.getTotalAmount())
                .startDate(c.getStartDate())
                .endDate(c.getEndDate())
                .status(c.getStatus())
                .statusName(sName)
                .signDate(c.getSignDate())
                .attachmentId(c.getAttachmentId())
                .salesUserId(c.getSalesUserId())
                .salesUserName("市场专员")
                .createTime(c.getCreateTime())
                .updateTime(c.getUpdateTime())
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

package com.lims.entrust.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lims.common.enums.EntrustStatus;
import com.lims.common.exception.BizException;
import com.lims.common.model.IdResp;
import com.lims.common.model.PageReq;
import com.lims.common.model.PageResp;
import com.lims.common.process.service.ProcessService;
import com.lims.common.result.LimsBizErrorCode;
import com.lims.common.util.BusinessNumberGenerator;
import com.lims.entrust.entity.EntrustOrder;
import com.lims.entrust.mapper.EntrustOrderMapper;
import com.lims.entrust.model.EntrustCreateReq;
import com.lims.entrust.model.EntrustVO;
import com.lims.entrust.service.EntrustService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EntrustServiceImpl implements EntrustService {

    private final EntrustOrderMapper entrustOrderMapper;
    private final ProcessService processService;
    private final BusinessNumberGenerator businessNumberGenerator;
    private static final String PROCESS_KEY = "entrust-order";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IdResp createEntrust(EntrustCreateReq req) {
        String code = businessNumberGenerator.generate(BusinessNumberGenerator.BusinessType.ENTRUST);
        Long userId = getSafeUserId();

        EntrustOrder order = EntrustOrder.builder()
                .entrustCode(code)
                .contractId(req.getContractId())
                .clientCompany(req.getClientCompany())
                .clientContact(req.getClientContact())
                .clientPhone(req.getClientPhone())
                .entrustDate(req.getEntrustDate() != null ? req.getEntrustDate() : LocalDate.now())
                .sampleSource(req.getSampleSource())
                .urgencyLevel(StrUtil.isNotBlank(req.getUrgencyLevel()) ? req.getUrgencyLevel() : "NORMAL")
                .status("WAITING")
                .clerkId(userId)
                .remark(req.getRemark())
                .isDeleted(0)
                .createBy(userId)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        entrustOrderMapper.insert(order);
        log.info("开立委托单成功: id={}, code={}", order.getId(), code);
        return IdResp.of(order.getId(), code);
    }

    @Override
    public PageResp<EntrustVO> pageEntrusts(PageReq req) {
        Page<EntrustOrder> page = new Page<>(req.getCurrent(), req.getSize());
        LambdaQueryWrapper<EntrustOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EntrustOrder::getIsDeleted, 0);

        if (StrUtil.isNotBlank(req.getKeyword())) {
            wrapper.and(w -> w.like(EntrustOrder::getEntrustCode, req.getKeyword())
                    .or().like(EntrustOrder::getClientCompany, req.getKeyword())
                    .or().like(EntrustOrder::getClientContact, req.getKeyword()));
        }
        wrapper.orderByDesc(EntrustOrder::getId);

        Page<EntrustOrder> res = entrustOrderMapper.selectPage(page, wrapper);
        List<EntrustVO> vos = res.getRecords().stream().map(this::convertToVO).collect(Collectors.toList());
        return PageResp.of(res.getCurrent(), res.getSize(), res.getTotal(), vos);
    }

    @Override
    public EntrustVO getEntrustById(Long id) {
        EntrustOrder o = entrustOrderMapper.selectById(id);
        if (o == null || o.getIsDeleted() == 1) {
            throw new BizException(LimsBizErrorCode.ENTRUST_NOT_FOUND);
        }
        return convertToVO(o);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createOrder(String entrustNo, Map<String, Object> variables) {
        if (variables == null) {
            variables = new HashMap<>();
        }
        variables.put("entrustStatus", EntrustStatus.ORDERED.getCode());
        log.info("开具委托单并启动流程, entrustNo={}", entrustNo);
        return processService.startProcess(PROCESS_KEY, entrustNo, variables);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(String taskId, String reason) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("orderAction", "CANCEL");
        vars.put("reason", reason);
        vars.put("entrustStatus", EntrustStatus.CANCELLED.getCode());
        processService.completeTask(taskId, vars);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void complete(String taskId, Map<String, Object> variables) {
        if (variables == null) {
            variables = new HashMap<>();
        }
        variables.put("orderAction", "SUBMIT");
        variables.put("entrustStatus", EntrustStatus.COMPLETED.getCode());
        processService.completeTask(taskId, variables);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reject(String taskId, String reason) {
        processService.rejectToPrevious(taskId, reason);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rollback(String taskId, String targetActivityId, String reason) {
        processService.rollbackTo(taskId, targetActivityId, reason);
    }

    @Override
    public EntrustStatus getStatus(String entrustNo) {
        return EntrustStatus.ORDERED;
    }

    private EntrustVO convertToVO(EntrustOrder o) {
        return EntrustVO.builder()
                .id(o.getId())
                .entrustCode(o.getEntrustCode())
                .contractId(o.getContractId())
                .clientCompany(o.getClientCompany())
                .clientContact(o.getClientContact())
                .clientPhone(o.getClientPhone())
                .entrustDate(o.getEntrustDate())
                .sampleSource(o.getSampleSource())
                .sampleSourceName("SAMPLING".equalsIgnoreCase(o.getSampleSource()) ? "现场采样" : "送样")
                .urgencyLevel(o.getUrgencyLevel())
                .status(o.getStatus())
                .statusName("已下单")
                .clerkName("下单专员")
                .createTime(o.getCreateTime())
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

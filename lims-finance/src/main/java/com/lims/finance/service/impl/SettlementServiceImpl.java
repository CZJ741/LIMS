package com.lims.finance.service.impl;

import com.lims.common.enums.SettlementStatus;
import com.lims.common.process.service.ProcessService;
import com.lims.finance.service.SettlementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SettlementServiceImpl implements SettlementService {

    private final ProcessService processService;
    private static final String PROCESS_KEY = "settlement-process";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String create(String settlementNo, Map<String, Object> variables) {
        if (variables == null) {
            variables = new HashMap<>();
        }
        variables.put("settlementStatus", SettlementStatus.PROCESSING.getCode());
        log.info("生成财务结算单流程, settlementNo={}", settlementNo);
        return processService.startProcess(PROCESS_KEY, settlementNo, variables);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirm(String taskId, String comment) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("settlePass", true);
        vars.put("comment", comment);
        vars.put("settlementStatus", SettlementStatus.COMPLETED.getCode());
        processService.completeTask(taskId, vars);
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
    public SettlementStatus getStatus(String settlementNo) {
        return SettlementStatus.PENDING;
    }
}

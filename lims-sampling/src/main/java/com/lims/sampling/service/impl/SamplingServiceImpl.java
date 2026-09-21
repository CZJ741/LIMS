package com.lims.sampling.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lims.common.enums.SamplingStatus;
import com.lims.common.exception.BizException;
import com.lims.common.model.IdResp;
import com.lims.common.model.PageReq;
import com.lims.common.model.PageResp;
import com.lims.common.process.service.ProcessService;
import com.lims.common.result.LimsBizErrorCode;
import com.lims.common.util.BusinessNumberGenerator;
import com.lims.sampling.entity.SamplingTask;
import com.lims.sampling.mapper.SamplingTaskMapper;
import com.lims.sampling.model.SamplingTaskCreateReq;
import com.lims.sampling.model.SamplingTaskVO;
import com.lims.sampling.service.SamplingService;
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
public class SamplingServiceImpl implements SamplingService {

    private final SamplingTaskMapper samplingTaskMapper;
    private final ProcessService processService;
    private final BusinessNumberGenerator businessNumberGenerator;
    private static final String PROCESS_KEY = "sampling-process";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IdResp createSamplingTask(SamplingTaskCreateReq req) {
        String code = businessNumberGenerator.generate(BusinessNumberGenerator.BusinessType.SAMPLING);
        Long userId = getSafeUserId();

        SamplingTask task = SamplingTask.builder()
                .taskCode(code)
                .entrustId(req.getEntrustId())
                .leaderId(userId)
                .samplingSite(req.getSamplingSite())
                .planStartTime(req.getPlanStartTime())
                .planEndTime(req.getPlanEndTime())
                .status("PENDING")
                .remark(req.getRemark())
                .isDeleted(0)
                .createBy(userId)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        samplingTaskMapper.insert(task);
        log.info("创建采样任务成功: id={}, code={}", task.getId(), code);
        return IdResp.of(task.getId(), code);
    }

    @Override
    public PageResp<SamplingTaskVO> pageSamplingTasks(PageReq req) {
        Page<SamplingTask> page = new Page<>(req.getCurrent(), req.getSize());
        LambdaQueryWrapper<SamplingTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SamplingTask::getIsDeleted, 0);

        if (StrUtil.isNotBlank(req.getKeyword())) {
            wrapper.and(w -> w.like(SamplingTask::getTaskCode, req.getKeyword())
                    .or().like(SamplingTask::getSamplingSite, req.getKeyword()));
        }
        wrapper.orderByDesc(SamplingTask::getId);

        Page<SamplingTask> res = samplingTaskMapper.selectPage(page, wrapper);
        List<SamplingTaskVO> vos = res.getRecords().stream().map(this::convertToVO).collect(Collectors.toList());
        return PageResp.of(res.getCurrent(), res.getSize(), res.getTotal(), vos);
    }

    @Override
    public SamplingTaskVO getSamplingTaskById(Long id) {
        SamplingTask t = samplingTaskMapper.selectById(id);
        if (t == null || t.getIsDeleted() == 1) {
            throw new BizException(LimsBizErrorCode.SAMPLING_TASK_NOT_FOUND);
        }
        return convertToVO(t);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String startPrepare(String samplingNo, Map<String, Object> variables) {
        if (variables == null) {
            variables = new HashMap<>();
        }
        variables.put("samplingStatus", SamplingStatus.PREPARING.getCode());
        log.info("采样任务进入准备流程, samplingNo={}", samplingNo);
        return processService.startProcess(PROCESS_KEY, samplingNo, variables);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void finishPrepare(String taskId, Map<String, Object> variables) {
        if (variables == null) {
            variables = new HashMap<>();
        }
        variables.put("samplingStatus", SamplingStatus.READY.getCode());
        processService.completeTask(taskId, variables);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void collectSample(String taskId, Map<String, Object> variables) {
        if (variables == null) {
            variables = new HashMap<>();
        }
        variables.put("samplingStatus", SamplingStatus.PENDING_AUDIT.getCode());
        processService.completeTask(taskId, variables);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditSample(String taskId, String comment) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("auditPass", true);
        vars.put("comment", comment);
        vars.put("samplingStatus", SamplingStatus.AUDITED.getCode());
        processService.completeTask(taskId, vars);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rejectSample(String taskId, String reason) {
        processService.rejectToPrevious(taskId, reason);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rollback(String taskId, String targetActivityId, String reason) {
        processService.rollbackTo(taskId, targetActivityId, reason);
    }

    @Override
    public SamplingStatus getStatus(String samplingNo) {
        return SamplingStatus.PENDING;
    }

    private SamplingTaskVO convertToVO(SamplingTask t) {
        return SamplingTaskVO.builder()
                .id(t.getId())
                .taskCode(t.getTaskCode())
                .entrustId(t.getEntrustId())
                .leaderName("采样组长")
                .samplingSite(t.getSamplingSite())
                .planStartTime(t.getPlanStartTime())
                .planEndTime(t.getPlanEndTime())
                .actualStartTime(t.getActualStartTime())
                .status(t.getStatus())
                .statusName("待采样")
                .remark(t.getRemark())
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

package com.lims.common.process.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.lims.common.model.PageReq;
import com.lims.common.model.PageResp;
import com.lims.common.process.model.ContractTransactionDTO;
import com.lims.common.process.model.ProcessInstanceDTO;
import com.lims.common.process.model.TaskDTO;
import com.lims.common.process.model.TraceTimelineDTO;
import com.lims.common.process.service.ProcessService;
import com.lims.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 流程任务留痕与聚合查询控制器
 */
@Tag(name = "流程任务留痕与跨阶段聚合查询", description = "全链路业务留痕追踪与三类任务聚合查询")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProcessTraceController {

    private final ProcessService processService;

    @Operation(summary = "全链路业务追溯留痕", description = "根据业务主单号跨阶段聚合所有流程流转节点、审批意见及驳回/回退轨迹")
    @GetMapping("/trace/{businessKey}")
    public Result<TraceTimelineDTO> getTraceTimeline(@PathVariable String businessKey) {
        return Result.ok(processService.getTraceTimeline(businessKey));
    }

    @Operation(summary = "我发起的流程实例列表", description = "查询当前登录用户作为发起人的所有流程实例（包含流转中和已归档）")
    @GetMapping("/task/my-applications")
    public Result<PageResp<ProcessInstanceDTO>> getMyApplications(@Valid PageReq pageReq) {
        String userId = getSafeUserId();
        return Result.ok(processService.getMyApplications(userId, pageReq));
    }

    @Operation(summary = "当前待我处理的待办任务", description = "基于当前登录用户的ID或其所属职位候选组查询待办审批任务")
    @GetMapping("/task/my-pending")
    public Result<PageResp<TaskDTO>> getMyPendingTasks(@Valid PageReq pageReq) {
        String userId = getSafeUserId();
        return Result.ok(processService.getMyPendingTasks(userId, pageReq));
    }

    @Operation(summary = "跨阶段合同事务聚合视图", description = "按合同主维度聚合所有关联的委托、采样、检测、报告、结算阶段进展与待办")
    @GetMapping("/task/my-transactions")
    public Result<PageResp<ContractTransactionDTO>> getMyTransactions(@Valid PageReq pageReq) {
        return Result.ok(processService.getMyTransactions(pageReq));
    }

    private String getSafeUserId() {
        try {
            if (StpUtil.isLogin()) {
                return StpUtil.getLoginIdAsString();
            }
        } catch (Exception ignored) {
        }
        return "admin";
    }
}

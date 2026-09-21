package com.lims.common.enums;

import com.lims.common.constant.LimsConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 阶段五：报告编制与审核状态机枚举
 */
@Getter
@AllArgsConstructor
public enum ReportStatus {

    PENDING_INIT(10, "待发起", "检测已归档，报告员待发起报告编制", LimsConstants.Position.REPORT_SPECIALIST),
    GENERATING(20, "生成中", "选择模板并由插件提取数据预渲染", LimsConstants.Position.REPORT_SPECIALIST),
    PENDING_EDIT(30, "待编制", "已分配编制员，等待编制员编写校对", LimsConstants.Position.REPORT_WRITER),
    EDITING(40, "编制中", "编制员正在排版、编写评价结论", LimsConstants.Position.REPORT_WRITER),
    PENDING_AUDIT(50, "待审核", "报告编制完成，提交审核员审核", LimsConstants.Position.REPORT_AUDITOR),
    AUDITING(60, "审核中", "报告审核员技术核对与签字", LimsConstants.Position.REPORT_AUDITOR),
    PENDING_FINANCE(70, "待财务审核", "报告审核通过，转入财务确认结算资格", LimsConstants.Position.FINANCE_SPECIALIST),
    PUBLISHED(80, "已发布", "盖电子印章并完成正式发布发放", LimsConstants.Position.REPORT_SPECIALIST),
    REJECTED(90, "已驳回", "报告审核或财务审核不通过，退回修改", LimsConstants.Position.REPORT_WRITER);

    private final int code;
    private final String name;
    private final String description;
    private final String operatorPosition;

    public static ReportStatus fromCode(int code) {
        for (ReportStatus status : values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return null;
    }
}

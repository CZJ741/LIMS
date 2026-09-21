package com.lims.common.enums;

import com.lims.common.constant.LimsConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 阶段六：财务结算状态机枚举
 */
@Getter
@AllArgsConstructor
public enum SettlementStatus {

    PENDING(10, "待结算", "报告已发布，自动或手动生成财务结算单", LimsConstants.Position.FINANCE_SPECIALIST),
    PROCESSING(20, "结算中", "开具发票、催款、等待到账中", LimsConstants.Position.FINANCE_SPECIALIST),
    COMPLETED(30, "已结算", "全额款项到账，财务核销完毕", LimsConstants.Position.FINANCE_SPECIALIST),
    REJECTED(40, "已驳回", "发票信息错误或结算金额异议退回重新核对", LimsConstants.Position.FINANCE_SPECIALIST);

    private final int code;
    private final String name;
    private final String description;
    private final String operatorPosition;

    public static SettlementStatus fromCode(int code) {
        for (SettlementStatus status : values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return null;
    }
}

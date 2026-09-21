package com.lims.common.enums;

import com.lims.common.constant.LimsConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 阶段一：合同状态机枚举
 */
@Getter
@AllArgsConstructor
public enum ContractStatus {

    DRAFT(10, "草稿", "市场员新创建草稿合同", LimsConstants.Position.SALES_REP),
    PENDING(20, "待审核", "已提交待财务人员审核", LimsConstants.Position.FINANCE_SPECIALIST),
    APPROVED(30, "已审核", "财务审核通过，进入待委托下单", LimsConstants.Position.FINANCE_SPECIALIST),
    REJECTED(40, "已驳回", "财务审核不通过，退回市场员修改", LimsConstants.Position.SALES_REP),
    ARCHIVED(50, "已归档", "流程完毕后归档", LimsConstants.Position.QUALITY_DIRECTOR);

    private final int code;
    private final String name;
    private final String description;
    private final String operatorPosition;

    public static ContractStatus fromCode(int code) {
        for (ContractStatus status : values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return null;
    }
}

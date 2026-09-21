package com.lims.common.enums;

import com.lims.common.constant.LimsConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 阶段二：委托下单状态机枚举
 */
@Getter
@AllArgsConstructor
public enum EntrustStatus {

    WAITING(10, "待下单", "合同已通过，待下单专员开立委托单", LimsConstants.Position.ORDER_SPECIALIST),
    ORDERED(20, "已下单", "委托单建立完成，生成检测要求", LimsConstants.Position.ORDER_SPECIALIST),
    IN_PROGRESS(30, "进行中", "已进入采样或实验室检测环节", LimsConstants.Position.SAMPLING_DIRECTOR),
    COMPLETED(40, "已完成", "检测完成并完成交付", LimsConstants.Position.ORDER_SPECIALIST),
    CANCELLED(50, "已取消", "委托单在执行前被主动取消", LimsConstants.Position.ORDER_SPECIALIST);

    private final int code;
    private final String name;
    private final String description;
    private final String operatorPosition;

    public static EntrustStatus fromCode(int code) {
        for (EntrustStatus status : values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return null;
    }
}

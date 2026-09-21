package com.lims.common.enums;

import com.lims.common.constant.LimsConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 阶段三：采样管理状态机枚举
 */
@Getter
@AllArgsConstructor
public enum SamplingStatus {

    PENDING(10, "待准备", "下发采样任务，待采样主任准备规划", LimsConstants.Position.SAMPLING_DIRECTOR),
    PREPARING(20, "准备中", "采样队长准备仪器设备与耗材试剂", LimsConstants.Position.SAMPLING_CAPTAIN),
    READY(30, "待采样", "耗材方案确认就绪，准备出发现场", LimsConstants.Position.SAMPLING_OFFICER),
    COLLECTING(40, "采样中", "现场采集并记录环境气象与样品信息", LimsConstants.Position.SAMPLING_OFFICER),
    PENDING_AUDIT(50, "待审核", "现场采样记录与样品已送达，待审核", LimsConstants.Position.SAMPLING_AUDITOR),
    AUDITED(60, "已审核", "采样记录审核通过，样品入库流转至实验室", LimsConstants.Position.SAMPLING_AUDITOR),
    REJECTED(70, "已驳回", "采样记录不合规或样品损坏，驳回重采", LimsConstants.Position.SAMPLING_AUDITOR);

    private final int code;
    private final String name;
    private final String description;
    private final String operatorPosition;

    public static SamplingStatus fromCode(int code) {
        for (SamplingStatus status : values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return null;
    }
}

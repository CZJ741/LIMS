package com.lims.common.enums;

import com.lims.common.constant.LimsConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 阶段四：检测分析状态机枚举
 */
@Getter
@AllArgsConstructor
public enum DetectionStatus {

    PENDING_RECEIVE(10, "待接收", "实验室主任/样品管理员待签收样品", LimsConstants.Position.SAMPLE_ADMIN),
    RECEIVED(20, "已接收", "样品已登记完好入库，待提交分析任务", LimsConstants.Position.LAB_DIRECTOR),
    PENDING_ASSIGN(30, "待分发", "任务待分配给专业分析员或待分析员认领", LimsConstants.Position.LAB_DIRECTOR),
    TESTING(40, "检测中", "分析员实验前处理、上机测定、录入结果", LimsConstants.Position.ANALYST),
    PENDING_RECHECK(50, "待复审", "原始记录已提交，待数据复审人员核验", LimsConstants.Position.DATA_REVIEWER),
    RECHECKING(60, "复审中", "数据复审人员正在审查质控谱图与计算公式", LimsConstants.Position.DATA_REVIEWER),
    COMPLETED(70, "已完成", "数据复审通过，检测数据归档生效", LimsConstants.Position.DATA_REVIEWER),
    REJECTED(80, "已驳回", "数据存在异常或质控不达标，驳回重新前处理分析", LimsConstants.Position.DATA_REVIEWER);

    private final int code;
    private final String name;
    private final String description;
    private final String operatorPosition;

    public static DetectionStatus fromCode(int code) {
        for (DetectionStatus status : values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return null;
    }
}

package com.lims.common.result;

import lombok.Getter;

/**
 * 14个业务模块分段业务错误码
 * 公共: 0xxx | 合同: 1xxx | 委托: 2xxx | 采样: 3xxx | 检测: 4xxx | 报告: 5xxx
 * 财务: 6xxx | 设备/基础: 7xxx | 文件: 8xxx | 系统/客商/分包: 9xxx
 */
@Getter
public enum LimsBizErrorCode implements BizErrorCode {

    // 0xxx: 通用与基础设施
    IDEMPOTENCY_VIOLATION(4001, "请求正在处理中，请勿重复提交"),
    DATA_SCOPE_DENIED(4003, "超出您的数据权限范围，无法查看或操作该记录"),
    BUSINESS_NO_GEN_ERROR(4005, "业务编号生成失败"),

    // 1xxx: 合同/委托协议模块
    CONTRACT_NOT_FOUND(1001, "合同不存在或已删除"),
    CONTRACT_STATUS_INVALID(1002, "当前合同状态不允许执行该操作"),
    CONTRACT_CANNOT_EDIT(1003, "非草稿或驳回状态的合同不允许修改"),
    CONTRACT_PACKAGE_NOT_FOUND(1004, "检测包维护项目不存在"),

    // 2xxx: 委托单管理模块
    ENTRUST_NOT_FOUND(2001, "委托单不存在"),
    ENTRUST_STATUS_INVALID(2002, "当前委托单状态不允许执行下单或取消"),
    ENTRUST_QUOTATION_REQUIRED(2003, "有报价任务必须填写单价与折扣信息"),

    // 3xxx: 采样管理模块
    SAMPLING_TASK_NOT_FOUND(3001, "采样任务不存在"),
    SAMPLING_STATUS_INVALID(3002, "采样任务当前状态不允许流转"),
    SAMPLING_AUDIT_REASON_REQUIRED(3003, "采样审核驳回必须填写不少于10字的原因"),

    // 4xxx: 样品流转与检测管理模块
    SAMPLE_NOT_FOUND(4001, "样品信息不存在"),
    SAMPLE_STATUS_INVALID(4002, "样品当前流转状态不允许进行该操作"),
    DETECTION_TASK_NOT_FOUND(4003, "检测任务不存在"),
    DETECTION_CURVE_INVALID(4004, "标准曲线相关系数不合规"),
    DETECTION_RECHECK_REJECT(4005, "数据复审驳回必须录入偏差说明"),

    // 5xxx: 报告管理模块
    REPORT_NOT_FOUND(5001, "检测报告不存在"),
    REPORT_TEMPLATE_NOT_FOUND(5002, "指定的报告模板不存在"),
    REPORT_STATUS_INVALID(5003, "报告当前状态不允许签发或发放"),

    // 6xxx: 财务管理模块
    SETTLEMENT_NOT_FOUND(6001, "结算单不存在"),
    SETTLEMENT_STATUS_INVALID(6002, "结算单状态异常"),
    SETTLEMENT_AMOUNT_MISMATCH(6003, "核销金额与到账流水金额不一致"),

    // 7xxx: 业务基础管理与仪器设备模块
    DEVICE_NOT_FOUND(7001, "仪器设备不存在"),
    DEVICE_IN_USE(7002, "仪器设备当前处于占用或检定状态"),
    TEST_ITEM_NOT_FOUND(7003, "检测项目不存在"),
    STANDARD_NOT_FOUND(7004, "判定标准或检测标准未启用"),

    // 8xxx: 文件管理模块
    FILE_NOT_FOUND(8001, "文件资源不存在"),
    FILE_ARCHIVE_FAILED(8002, "文件归档异常"),

    // 9xxx: 客商/分包/系统管理模块
    CUSTOMER_NOT_FOUND(9001, "客户信息不存在"),
    SUPPLIER_NOT_FOUND(9002, "供应商信息不存在"),
    SUBCONTRACT_NOT_FOUND(9003, "分包协议不存在"),
    USER_NOT_FOUND(9004, "用户信息不存在"),
    ROLE_NOT_FOUND(9005, "角色不存在");

    private final int code;
    private final String message;

    LimsBizErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}

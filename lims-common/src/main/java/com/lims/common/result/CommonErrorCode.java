package com.lims.common.result;

import lombok.Getter;

/**
 * 通用业务错误码枚举
 */
@Getter
public enum CommonErrorCode implements BizErrorCode {

    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "账号未登录或登录态已失效"),
    FORBIDDEN(403, "无操作权限"),
    NOT_FOUND(404, "请求资源未找到"),
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),
    CONFLICT(409, "数据冲突或已存在"),
    PARAM_VALID_ERROR(422, "参数校验不通过"),
    TOO_MANY_REQUESTS(429, "请求过于频繁，请稍后重试"),
    SYSTEM_ERROR(500, "系统内部繁忙，请联系管理员"),
    SERVICE_UNAVAILABLE(503, "服务暂不可用"),

    // 业务通用错误码
    DATA_NOT_EXIST(1001, "所查数据不存在"),
    DATA_ALREADY_EXISTS(1002, "数据已存在，不可重复新增"),
    STATUS_ERROR(1003, "当前状态不允许执行该操作"),
    FLOW_NOT_FOUND(1004, "工作流定义或实例不存在"),
    FILE_UPLOAD_ERROR(1005, "文件上传失败"),
    FILE_DOWNLOAD_ERROR(1006, "文件下载失败");

    private final int code;
    private final String message;

    CommonErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}

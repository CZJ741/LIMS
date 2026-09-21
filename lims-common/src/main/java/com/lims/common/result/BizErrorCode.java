package com.lims.common.result;

/**
 * 业务错误码规范接口
 */
public interface BizErrorCode {

    /**
     * 获取状态码
     */
    int getCode();

    /**
     * 获取提示信息
     */
    String getMessage();
}

package com.lims.common.exception;

import com.lims.common.result.BizErrorCode;
import com.lims.common.result.CommonErrorCode;
import lombok.Getter;

/**
 * 业务全局自定义异常
 */
@Getter
public class BizException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final int code;
    private final String message;

    public BizException(String message) {
        super(message);
        this.code = CommonErrorCode.SYSTEM_ERROR.getCode();
        this.message = message;
    }

    public BizException(BizErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    public BizException(BizErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.code = errorCode.getCode();
        this.message = customMessage;
    }

    public BizException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
}

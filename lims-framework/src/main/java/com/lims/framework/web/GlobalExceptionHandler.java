package com.lims.framework.web;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.exception.SaTokenException;
import com.lims.common.exception.BizException;
import com.lims.common.result.CommonErrorCode;
import com.lims.common.result.Result;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.stream.Collectors;

/**
 * 全局统一异常处理器（支持 Sa-Token 鉴权异常转换）
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 捕获业务自定义异常
     */
    @ExceptionHandler(BizException.class)
    public Result<Void> handleBizException(BizException e) {
        log.warn("业务异常 [code={}]: {}", e.getCode(), e.getMessage());
        return Result.fail(e.getCode(), e.getMessage());
    }

    /**
     * 捕获 Sa-Token 未登录异常
     */
    @ExceptionHandler(NotLoginException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<Void> handleNotLoginException(NotLoginException e) {
        log.warn("认证失败: {}", e.getMessage());
        return Result.fail(CommonErrorCode.UNAUTHORIZED.getCode(), "账号未登录或登录态已失效");
    }

    /**
     * 捕获 Sa-Token 无角色异常
     */
    @ExceptionHandler(NotRoleException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<Void> handleNotRoleException(NotRoleException e) {
        log.warn("角色校验不通过: {}", e.getMessage());
        return Result.fail(CommonErrorCode.FORBIDDEN.getCode(), "当前角色无权访问该功能");
    }

    /**
     * 捕获 Sa-Token 无权限异常
     */
    @ExceptionHandler(NotPermissionException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<Void> handleNotPermissionException(NotPermissionException e) {
        log.warn("权限校验不通过: {}", e.getMessage());
        return Result.fail(CommonErrorCode.FORBIDDEN.getCode(), "您没有该操作权限: " + e.getCode());
    }

    /**
     * 捕获 Sa-Token 其他通用异常并转换为业务异常响应
     */
    @ExceptionHandler(SaTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<Void> handleSaTokenException(SaTokenException e) {
        log.warn("Sa-Token 安全拦截异常: {}", e.getMessage());
        return Result.fail(CommonErrorCode.UNAUTHORIZED.getCode(), e.getMessage());
    }

    /**
     * 捕获 @Valid / @Validated 请求体参数校验失败异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("参数校验异常: {}", message);
        return Result.fail(CommonErrorCode.PARAM_VALID_ERROR.getCode(), message);
    }

    /**
     * 捕获表单绑定异常
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("表单参数绑定异常: {}", message);
        return Result.fail(CommonErrorCode.PARAM_VALID_ERROR.getCode(), message);
    }

    /**
     * 捕获单个参数校验异常 (@RequestParam / @PathVariable)
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleConstraintViolationException(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));
        log.warn("字段校验异常: {}", message);
        return Result.fail(CommonErrorCode.PARAM_VALID_ERROR.getCode(), message);
    }

    /**
     * 捕获不支持的 HTTP 方法
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public Result<Void> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.warn("不支持的请求方式: {}", e.getMessage());
        return Result.fail(CommonErrorCode.METHOD_NOT_ALLOWED.getCode(), "不支持当前请求方法: " + e.getMethod());
    }

    /**
     * 捕获 404 资源未找到
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result<Void> handleNoHandlerFoundException(NoHandlerFoundException e) {
        log.warn("路径不存在: {}", e.getRequestURL());
        return Result.fail(CommonErrorCode.NOT_FOUND.getCode(), "请求路径不存在: " + e.getRequestURL());
    }

    /**
     * 捕获未处理的兜底系统异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleException(Exception e) {
        log.error("系统未知内部异常", e);
        return Result.fail(CommonErrorCode.SYSTEM_ERROR.getCode(), "系统处理异常，请联系系统管理员");
    }
}

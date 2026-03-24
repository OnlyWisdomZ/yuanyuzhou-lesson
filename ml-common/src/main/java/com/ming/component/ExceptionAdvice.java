package com.ming.component;

import com.ming.exception.IllegalParamException;
import com.ming.exception.RepeatRecordException;
import com.ming.exception.ServerErrorException;
import com.ming.result.Result;
import com.ming.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** @author Ming */
@Slf4j
@RestControllerAdvice(basePackages = {"com.ming"})
public class ExceptionAdvice {

    @ExceptionHandler(ServerErrorException.class)
    public Object serverErrorException(ServerErrorException e) {
        String coderMessage = e.getMessage();
        log.error("业务异常 - 服务器异常: " + coderMessage);
        return new Result<>(ResultCode.SERVER_ERROR, coderMessage);
    }

    @ExceptionHandler(IllegalParamException.class)
    public Object illegalParamException(IllegalParamException e) {
        String coderMessage = e.getMessage();
        log.error("业务异常 - 非法参数: " + coderMessage);
        return new Result<>(ResultCode.ILLEGAL_PARAM, coderMessage);
    }

    @ExceptionHandler(RepeatRecordException.class)
    public Object repeatRecordException(RepeatRecordException e) {
        String coderMessage = e.getMessage();
        log.error("业务异常 - 重复记录: " + coderMessage);
        return new Result<>(ResultCode.REPEAT_RECORD, coderMessage);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public Object hibernateValidatorException(BindException e) {
        // 获取BindingResult
        BindingResult bindingResult = e.getBindingResult();
        // 获取BindingResult中所有属性错误信息集合中的第一个属性错误
        FieldError firstFieldError = bindingResult.getFieldErrors().get(0);
        // 异常信息 : "xxx实例的xxx属性校验失败: xxx异常信息"
        String coderMessage = String.format("%s实例的%s属性校验失败: %s",
                firstFieldError.getObjectName(),
                firstFieldError.getField(),
                firstFieldError.getDefaultMessage());
        // 记录日志
        log.error(coderMessage);
        // 响应
        return new Result<>(ResultCode.ILLEGAL_PARAM, coderMessage);
    }

    @ExceptionHandler(Exception.class)
    public Object exception(Exception e) {
        String coderMessage = e.getMessage();
        log.error("其他异常: " + coderMessage);
        return new Result<>(ResultCode.SERVER_ERROR, coderMessage);
    }
}
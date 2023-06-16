package com.yuan.handle;

import com.alibaba.fastjson.JSON;
import com.yuan.exception.MyLoginException;
import com.yuan.exception.MyRuntimeException;
import com.yuan.myEnum.CodeMsg;
import com.yuan.utils.DataCacheUtil;
import com.yuan.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/2/27 0:25
 * @Description 统一异常处理
 */
@ControllerAdvice
@Slf4j
public class MyExceptionHandler {
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public R handlerException(Exception ex) {
        log.error("请求URL-----------------" + DataCacheUtil.getRequest().getRequestURL());
        log.error("出错啦-----------------" + ex);
        DataCacheUtil.getResponse().setStatus(500);
        if (ex instanceof MyRuntimeException) {
            MyRuntimeException e = (MyRuntimeException) ex;
            return R.fail(e.getMessage());
        }
        if (ex instanceof MyLoginException) {
            MyLoginException e = (MyLoginException) ex;
            return R.fail(300, e.getMessage());
        }
//            方法参数不可用
        if (ex instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException e = (MethodArgumentNotValidException) ex;
            Map<String, String> collect = e.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            return R.fail(JSON.toJSONString(collect));
        }
//        参数异常
        if (ex instanceof MissingServletRequestParameterException) {
            return R.fail(CodeMsg.PARAMETER_ERROR);
        }
        return R.fail(CodeMsg.FAIL);
    }
}

package com.yuan.exception;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/2/27 0:23
 * @Description 自定义运行异常
 */
public class MyRuntimeException extends RuntimeException {

    private String msg;

    public MyRuntimeException() {
        super();
    }

    public MyRuntimeException(String msg) {
        super(msg);
        this.msg = msg;
    }


    public MyRuntimeException(Throwable cause) {
        super(cause);
        this.msg = cause.getMessage();
    }

    public MyRuntimeException(String msg, Throwable cause) {
        super(msg, cause);
        this.msg = msg;
    }


    public String getMsg() {
        return msg;
    }
}

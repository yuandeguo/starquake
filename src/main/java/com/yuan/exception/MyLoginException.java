package com.yuan.exception;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/2/27 0:24
 * @Description 未登录异常
 */
public class MyLoginException extends RuntimeException {

    private String msg;

    public MyLoginException() {
        super();
    }

    public MyLoginException(String msg) {
        super(msg);
        this.msg = msg;
    }


    public MyLoginException(Throwable cause) {
        super(cause);
        this.msg = cause.getMessage();
    }

    public MyLoginException(String msg, Throwable cause) {
        super(msg, cause);
        this.msg = msg;
    }


    public String getMsg() {
        return msg;
    }
}


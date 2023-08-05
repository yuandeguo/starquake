package com.yuan.utils;

import com.yuan.myEnum.CodeMsg;
import lombok.Data;

import java.io.Serializable;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/2/27 0:20
 * @Description null
 */
@Data
public class R <T> implements Serializable {
    private static final long serialVersionUI = 1L;

    private int code;
    private String message;
    private T data;
    private long currentTimeMillis = System.currentTimeMillis();

    public R() {
        this.code = 200;
    }

    public R(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public R(T data) {
        this.code = 200;
        this.data = data;
    }

    public R(String message) {
        this.code = 500;
        this.message = message;
    }

    public static <T> R<T> fail(String message) {
        return new R(message);
    }
    public static <T> R<T> JWT_EXPIRE(String message) {
       R r= new R(5001,message);
        return r;
    }
    public static <T> R<T> fail(CodeMsg codeMsg) {
        return new R(codeMsg.getCode(), codeMsg.getMsg());
    }

    public static <T> R<T> fail(Integer code, String message) {
        return new R(code, message);
    }

    public static <T> R<T> success(T data) {
        return new R(data);
    }

    public static <T> R<T> success() {
        return new R();
    }
    
}

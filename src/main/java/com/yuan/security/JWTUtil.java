package com.yuan.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;

import java.util.Date;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/6/4 21:47
 * @Description null
 */
public class JWTUtil {

    //定义token返回头部
    public static final String AUTH_HEADER_KEY = "Authorization";

    //token前缀
    public static final String TOKEN_PREFIX = "Bearer ";

    //签名密钥
    public static final String KEY = "yuan18318387647!@#$%^&";

    //有效期默认为 2填
    public static final Long EXPIRATION_TIME = 1000L*60*60*24*2;


    /**
     * 创建TOKEN
     * @param content
     * @return
     */
    public static String createToken(String content){
        return TOKEN_PREFIX + JWT.create()
                .withSubject(content)
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(KEY));
    }

    /**
     * 验证token
     * @param token
     */
    public static String verifyToken(String token) throws Exception {
        try {
            return JWT.require(Algorithm.HMAC512(KEY))
                    .build()
                    .verify(token.replace(TOKEN_PREFIX, ""))
                    .getSubject();
        } catch (TokenExpiredException e){
            throw new Exception("token已失效，请重新登录",e);
        } catch (JWTVerificationException e) {
            throw new Exception("token验证失败！",e);
        }
    }


}
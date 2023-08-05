package com.yuan.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

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
    public static final String JWT_SECRET = "yuan18318387647!@#$%^&";

    //有效期默认为 2天
    public static final Long EXPIRATION_TIME = 1000L * 60 * 60 * 24 * 2;
    //有效期默认为 2天+30分钟
    public static final Long REFRESH_EXPIRATION_TIME = 1000L * 60 * 60 * 24 * 2+ 1000L * 60 * 30;

    //用户信息
    public static final String USER_INFO = "userInfo";
    public static final String JWT_REALM = "JWTRealm";
    /**
     * @param map        令牌payload数据
     * @param secret     指定令牌生成时的密钥
     * @param expireTime 过期时间 单位:秒
     * @return
     */
    public static String createToken(Map<String, String> map, String secret, Long expireTime) {
        Date date = Date.from(LocalDateTime.now().plusSeconds(expireTime).atZone(ZoneId.systemDefault()).toInstant());
        Algorithm algorithm;
        algorithm = Algorithm.HMAC256(secret);
        JWTCreator.Builder builder = JWT.create();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            builder.withClaim(entry.getKey(), entry.getValue());
        }
        return builder
                //到期时间
                .withExpiresAt(date)
                //创建一个新的JWT，并使用给定的算法进行标记
                .sign(algorithm);
    }

    /**
     * @param content 令牌payload数据
     * @return
     */
    public static String createToken(String content) {
        Date date = Date.from(LocalDateTime.now().plusSeconds(EXPIRATION_TIME).atZone(ZoneId.systemDefault()).toInstant());
        Algorithm algorithm;
        algorithm = Algorithm.HMAC256(JWT_SECRET);
        JWTCreator.Builder builder = JWT.create();
        builder.withClaim("content", content);
        return builder
                //到期时间
                .withExpiresAt(date)
                //创建一个新的JWT，并使用给定的算法进行标记
                .sign(algorithm);
    }

    /**
     * 指定密钥校验 token 是否正确
     *
     * @param token 令牌
     */
    public static boolean verifySpecifiedSecret(String token, String secret) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        //在token中附带了username信息
        JWTVerifier verifier = JWT.require(algorithm).build();
        //验证 token
        DecodedJWT verify = verifier.verify(token);
        return true;
    }

    /**
     * 指定密钥校验 token 是否正确
     *
     * @param token 令牌
     */
    public static boolean verifySpecifiedSecret(String token) {
        Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET);
        //在token中附带了username信息
        JWTVerifier verifier = JWT.require(algorithm).build();
        //验证 token
        verifier.verify(token);
        return true;
    }
    /**
     * 获得token中的信息，无需secret解密也能获得
     *
     * @return token中包含的用户名
     */
    public static String getClaim(String token,String claim) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim(claim).asString();
        } catch (JWTDecodeException e) {
            return null;
        }
    }
}

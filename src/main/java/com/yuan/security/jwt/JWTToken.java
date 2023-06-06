package com.yuan.security.jwt;

import lombok.Data;
import org.apache.shiro.authc.AuthenticationToken;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/6/6 23:25
 * @Description null
 */
@Data
public class JWTToken implements AuthenticationToken {
    private String token;
    public JWTToken(String token) {
        this.token = token;
    }
    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }

}

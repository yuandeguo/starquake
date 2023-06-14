package com.yuan.security.jwt;

import com.alibaba.fastjson2.JSON;
import com.yuan.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import java.util.HashSet;
import java.util.Set;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/6/6 23:12
 * @Description null
 */
@Slf4j
public class JWTRealm extends AuthorizingRealm {


    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JWTToken;
    }

    /**
     * 权限校验
     * @param principals
     * @return
     */
    @Override
    public AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        if (principals.isEmpty()) {
            return null;
        }
        // 获取token对应的realm，如果不是本realm处理的token，则直接返回null
        String realmName = principals.getRealmNames().stream().findFirst().orElse("");
        if (!JWTUtil.JWT_REALM.equals(realmName)) {
            return null;
        }
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        Set<String> roleSet = new HashSet<>();
        Set<String> permissionSet = new HashSet<>();
        roleSet.add("role");
        permissionSet.add("permission");
        //设置该用户拥有的角色和权限
        simpleAuthorizationInfo.setRoles(roleSet);
        simpleAuthorizationInfo.setStringPermissions(permissionSet);
        return simpleAuthorizationInfo;
    }

    /**
     * 身份认证
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        JWTToken jwtToken = (JWTToken) authenticationToken;
        String token = (String) jwtToken.getCredentials();
        // 解密获得username，用于和数据库进行对比
        if (!JWTUtil.verifySpecifiedSecret(token, JWTUtil.JWT_SECRET)) {
            log.info("***JWTReaml.doGetAuthenticationInfo业务结束，token认证失败,token={}", token);
                       throw new AuthenticationException("您没有登录，请先登录或注册再进行此操作!");
        }
        User user = JSON.parseObject(JWTUtil.getClaim(token, JWTUtil.USER_INFO), User.class);
        return new SimpleAuthenticationInfo(user, token, "JWTRealm");
    }
}

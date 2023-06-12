package com.yuan.security.jwt;

import com.alibaba.fastjson2.JSON;
import com.yuan.vo.UserJWTVo;
import com.yuan.vo.UserVO;
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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/6/6 23:12
 * @Description null
 */
@Slf4j
public class JWTReaml extends AuthorizingRealm {


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
        System.out.println(1);
//        if (principals.isEmpty()) {
//            return null;
//        }
//        // 获取token对应的realm，如果不是本realm处理的token，则直接返回null
//        String realmName = principals.getRealmNames().stream().findFirst().orElse("");
//        if (!ShiroConstant.JWT_REALM.equals(realmName)) {
//            return null;
//        }
//        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
//        UserSimpleInfoVO userSimpleInfoVO = (UserSimpleInfoVO) principals.getPrimaryPrincipal();
//        String account = userSimpleInfoVO.getAccount();
//        Set<String> roleSet = new HashSet<>();
//        Set<String> permissionSet = new HashSet<>();
//        AccountTypeEnum accountTypeEnum = userSimpleInfoVO.getAccountTypeEnum();
//        switch (accountTypeEnum) {
//            case ADMIN: {
//                ModelResult<AdminUser> adminUserResult = adminUserService.queryByAccount(account);
//                if (adminUserResult.isSuccess() && adminUserResult.getModel() != null) {
//                    AdminUser adminUser = adminUserResult.getModel();
//                    //获得该用户角色
//                    ModelResult<List<UserRole>> roleResult = userRoleService.queryByUserId(adminUser.getId());
//                    if (roleResult.isSuccess() && roleResult.getModel() != null) {
//                        List<String> roleNameList = roleResult.getModel().stream().map(UserRole::getRoleNameEn).collect(Collectors.toList());
//                        List<Long> roleIdList = roleResult.getModel().stream().map(UserRole::getRoleId).collect(Collectors.toList());
//                        roleSet.addAll(roleNameList);
//
//                        //获得该用户的权限
//                        ModelResult<List<Long>> permissionIdResult = rolePermissionService.queryRolePermissionByRoleIds(roleIdList);
//                        if (permissionIdResult.isSuccess() && permissionIdResult.getModel() != null) {
//                            ModelResult<List<Permission>> permissionListResult = permissionService.queryPermissionByIds(permissionIdResult.getModel());
//                            if (permissionListResult.isSuccess() && permissionListResult.getModel() != null) {
//                                List<Permission> permissionList = permissionListResult.getModel();
//                                List<String> permisstionNameList = permissionList.stream().map(Permission::getNameEn).collect(Collectors.toList());
//                                permissionSet.addAll(permisstionNameList);
//                            }
//                        }
//                    }
//                }
//                break;
//            }
//            case CANTEEN_SCHEME_DEMO_ACCOUNT: {
//                ModelResult<CanteenSchemeDemoAccount> demoAccountModelResult = canteenSchemeDemoAccountService.queryByAccount(account);
//                if (!demoAccountModelResult.isSuccess() || demoAccountModelResult.getModel() == null) {
//                    return null;
//                }
//                permissionSet.addAll(DemoAccountConstant.DEMO_ACCOUNT_PERMISSION_SET);
//                break;
//            }
//        }
//
//        //设置该用户拥有的角色和权限
//        simpleAuthorizationInfo.setRoles(roleSet);
//        simpleAuthorizationInfo.setStringPermissions(permissionSet);
//        return simpleAuthorizationInfo;
        return null;
    }

    /**
     * 身份认证
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        System.out.println(1);
        JWTToken jwtToken = (JWTToken) authenticationToken;
        String token = (String) jwtToken.getCredentials();
        // 解密获得username，用于和数据库进行对比
        if (!JWTUtil.verifySpecifiedSecret(token, JWTUtil.JWT_SECRET)) {

            log.info("***JWTReaml.doGetAuthenticationInfo业务结束，token认证失败,token={}", token);
                       throw new AuthenticationException("您没有登录，请先登录或注册再进行此操作!");
        }
        UserJWTVo userJWTVo = JSON.parseObject(JWTUtil.getClaim(token, JWTUtil.USER_INFO), UserJWTVo.class);
        return new SimpleAuthenticationInfo(userJWTVo, token, "JWTReaml");
    }
}

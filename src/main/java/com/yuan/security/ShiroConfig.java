package com.yuan.security;

import com.yuan.security.jwt.JWTFilter;
import com.yuan.security.jwt.JWTReaml;
import com.yuan.security.jwt.JWTUtil;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/6/6 23:25
 * @Description null
 */
@Configuration
public class ShiroConfig {
    //ShiroFilter过滤所有请求
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(DefaultWebSecurityManager securityManager, JWTFilter jwtFilter) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        //给ShiroFilter配置安全管理器
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        //配置系统受限资源
        //配置系统公共资源
        Map<String, String> map = new HashMap<String, String>();
        map.put("/api/test/**", "jwtFilter");//表示这个为公共资源 一定是在受限资源上面
        shiroFilterFactoryBean.setFilterChainDefinitionMap(map);
        Map<String, Filter> filterMap=new HashMap<>();
        filterMap.put("jwtFilter",jwtFilter);
        shiroFilterFactoryBean.setFilters(filterMap);
        return shiroFilterFactoryBean;
    }

    //创建安全管理器
    @Bean
    public DefaultWebSecurityManager getDefaultWebSecurityManager(Realm realm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(realm);
        return securityManager;
    }

    //创建自定义Realm
    @Bean
    public Realm getRealm() {
        JWTReaml realm = new JWTReaml();
        return realm;
    }
}

package com.yuan.config;

import com.yuan.filter.LimitFlowFilter;
import com.yuan.filter.UrlFilter;
import com.yuan.security.FirstExceptionStrategy;
import com.yuan.security.jwt.JWTCacheManager;
import com.yuan.security.jwt.JWTFilter;
import com.yuan.security.jwt.JWTRealm;
import com.yuan.service.RedisService;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.authz.ModularRealmAuthorizer;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/6/7 23:52
 * @Description null
 */
@Configuration
public class ShiroConfig {
    @Resource
    private RedisService redisService;

    /**
     * 限流filter
     * @return
     */
    @Bean
    public FilterRegistrationBean<LimitFlowFilter> limitFlowFilter() {
        FilterRegistrationBean<LimitFlowFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new LimitFlowFilter(redisService,0));
        registrationBean.addUrlPatterns("/*");
        registrationBean.setName("limitFlowFilter");
        return registrationBean;
    }
    @Bean
    public FilterRegistrationBean<UrlFilter> urlFilterFilterRegistrationBean() {
        FilterRegistrationBean<UrlFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new UrlFilter(redisService));
        registrationBean.addUrlPatterns("/*");
        registrationBean.setName("urlFilter");
        registrationBean.setOrder(0);
        return registrationBean;
    }



    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(DefaultWebSecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        //给ShiroFilter配置安全管理器
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        //配置系统受限资源
        //配置系统公共资源
        Map<String, String> map = new HashMap<String, String>();
      //  map.put("/test/test2", "jwtFilter");//表示这个为公共资源 一定是在受限资源上面



        map.put("/article/admin/**","jwtFilter");
        map.put("/comment/admin/**","jwtFilter");
        map.put("/label/admin/**","jwtFilter");
        map.put("/resource/admin/**","jwtFilter");
        map.put("/resourcePath/admin/**","jwtFilter");
        map.put("/sort/admin/**","jwtFilter");
        map.put("/treeHole/admin/**","jwtFilter");
        map.put("/user/admin/**","jwtFilter");
        map.put("/webInfo/admin/**","jwtFilter");
        map.put("/weiYan/**","jwtFilter");
        map.put("/comment/saveComment","jwtFilter");
        map.put("/qiniu/getUpToken","jwtFilter");
        map.put("/resource/saveResource","jwtFilter");
        map.put("/resourcePath/saveFriend","jwtFilter");
        map.put("/user/logout","jwtFilter");
        map.put("/user/updateUserInfo","jwtFilter");
        map.put("/user/getCodeForBind","jwtFilter");
        map.put("/user/updateSecretInfo","jwtFilter");
        map.put("/weiYan/deleteWeiYan","jwtFilter");
        map.put("/weiYan/saveWeiYan","jwtFilter");
        map.put("/doc/**","jwtFilter");
        //map.put("/**", "anon");//表示这个为公共资源 一定是在受限资源上面





        shiroFilterFactoryBean.setFilterChainDefinitionMap(map);
        Map<String, Filter> filterMap=new HashMap<>();
        JWTFilter jwtFilter=new JWTFilter();
        filterMap.put("jwtFilter",jwtFilter);

        shiroFilterFactoryBean.setFilters(filterMap);
        return shiroFilterFactoryBean;
    }

    //创建安全管理器
    @Bean
    public DefaultWebSecurityManager getDefaultWebSecurityManager(JWTCacheManager jwtCacheManager,Realm realm,ModularRealmAuthorizer modularRealmAuthorizer,ModularRealmAuthenticator modularRealmAuthenticator) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(realm);
        securityManager.setAuthenticator(modularRealmAuthenticator);
        securityManager.setAuthorizer(modularRealmAuthorizer);
        securityManager.setCacheManager(jwtCacheManager);
        return securityManager;
    }

    //创建自定义Realm
    @Bean
    public Realm realm() {
        JWTRealm realm = new JWTRealm();
        return realm;
    }

    @Bean
    public JWTCacheManager jwtCacheManager()
    {
        JWTCacheManager jwtCacheManager=new JWTCacheManager("jwtCache",28800);
        return jwtCacheManager;
    }




    @Bean
    public ModularRealmAuthorizer modularRealmAuthorizer(Realm realm){
        ModularRealmAuthorizer modularRealmAuthorizer=new ModularRealmAuthorizer();
        List<Realm> list=new ArrayList<>();
        list.add(realm);
        modularRealmAuthorizer.setRealms(list);
        return modularRealmAuthorizer;

    }
    @Bean
    public ModularRealmAuthenticator modularRealmAuthenticator(Realm realm, FirstExceptionStrategy firstExceptionStrategy)
    {
        ModularRealmAuthenticator modularRealmAuthenticator=new ModularRealmAuthenticator();
        List<Realm> list=new ArrayList<>();
        list.add(realm);
        modularRealmAuthenticator.setRealms(list);
        modularRealmAuthenticator.setAuthenticationStrategy(firstExceptionStrategy);
        return modularRealmAuthenticator;
    }
    @Bean
    public FirstExceptionStrategy firstExceptionStrategy(){
        return new FirstExceptionStrategy();

    }

}

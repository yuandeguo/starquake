package com.yuan.config;

import com.yuan.filter.LimitFlowFilter;
import com.yuan.filter.UrlFilter;
import com.yuan.interceptor.AuthorityVerifyInterceptor;
import com.yuan.interceptor.WebInfoHandlerInterceptor;
import com.yuan.service.RedisService;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/2/27 0:32
 * @Description 增加拦截器
 */
@Configuration
public class AddInterceptorConfig implements WebMvcConfigurer {
    @Resource
    private RedisService redisService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new WebInfoHandlerInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/user/login",
                        "/webInfo/getWebInfo",
                        "/article/admin/**",
                        "/comment/admin/**",
                        "/label/admin/**",
                        "/resource/admin/**",
                        "/resourcePath/admin/**",
                        "/sort/admin/**",
                        "/treeHole/admin/**",
                        "/user/admin/**",
                        "/webInfo/admin/**",
                        "/weiYan/**",
                        "sort/redis/**"
                        );
        registry.addInterceptor(new AuthorityVerifyInterceptor(redisService))
                .addPathPatterns(
                "/article/admin/**",
                "/comment/admin/**",
                "/label/admin/**",
                "/resource/admin/**",
                "/resourcePath/admin/**",
                "/sort/admin/**",
                "/treeHole/admin/**",
                "/user/admin/**",
                "/webInfo/admin/**",
                "/weiYan/**"

            ).excludePathPatterns(
                        "/weiYan/listWeiYan"
                )

        ;
    }
    @Bean
    public FilterRegistrationBean<LimitFlowFilter> limitFlowFilter() {
        FilterRegistrationBean<LimitFlowFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new LimitFlowFilter(redisService));
        registrationBean.addUrlPatterns("/*");
        registrationBean.setName("limitFlowFilter");
        registrationBean.setOrder(1);
        return registrationBean;
    }
    @Bean
    public FilterRegistrationBean<UrlFilter> logFilter() {
        FilterRegistrationBean<UrlFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new UrlFilter(redisService));
        registrationBean.addUrlPatterns("/*");
        registrationBean.setName("LogFilter");
        registrationBean.setOrder(2);
        return registrationBean;
    }



}

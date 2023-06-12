package com.yuan.filter;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/3/8 22:46
 * @Description 路径过滤  已经弃用
 */

import com.yuan.service.RedisService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;

@AllArgsConstructor
@Slf4j
public class UrlFilter implements Filter {

    private RedisService redisService;

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("路径过滤");
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        //String requestURI = httpRequest.getRequestURI();
        String ipAddress = getIpAdrress(httpRequest);
        redisService.setVisitIp(ipAddress);
        redisService.setVisitUrl();
        chain.doFilter(request,response);

    }

    public String getIpAdrress(HttpServletRequest request) {
        String ip = null;
        //X-Forwarded-For：Squid 服务代理
        String ipAddresses = request.getHeader("X-Forwarded-For");
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            //打印所有头信息
            String s = headerNames.nextElement();
            String header = request.getHeader(s);
        }
        String unknown = "unknown";
        if (ipAddresses == null || ipAddresses.length() == 0 || unknown.equalsIgnoreCase(ipAddresses)) {
            //Proxy-Client-IP：apache 服务代理
            ipAddresses = request.getHeader("Proxy-Client-IP");
        }

        if (ipAddresses == null || ipAddresses.length() == 0 || unknown.equalsIgnoreCase(ipAddresses)) {
            //WL-Proxy-Client-IP：weblogic 服务代理
            ipAddresses = request.getHeader("WL-Proxy-Client-IP");
        }

        if (ipAddresses == null || ipAddresses.length() == 0 || unknown.equalsIgnoreCase(ipAddresses)) {
            //HTTP_CLIENT_IP：有些代理服务器
            ipAddresses = request.getHeader("HTTP_CLIENT_IP");
        }

        if (ipAddresses == null || ipAddresses.length() == 0 || unknown.equalsIgnoreCase(ipAddresses)) {
            //X-Real-IP：nginx服务代理
            ipAddresses = request.getHeader("X-Real-IP");
        }

        //有些网络通过多层代理，那么获取到的ip就会有多个，一般都是通过逗号（,）分割开来，并且第一个ip为客户端的真实IP
        if (ipAddresses != null && ipAddresses.length() != 0) {
            ip = ipAddresses.split(",")[0];
        }

        //还是不能获取到，最后再通过request.getRemoteAddr();获取
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ipAddresses)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }


}
package com.yuan.security.jwt;

import com.alibaba.fastjson2.JSON;
import com.yuan.utils.R;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/6/6 23:36
 * @Description null
 */
public class JWTFilter extends BasicHttpAuthenticationFilter {
    public  static  final ThreadLocal<Boolean> threadLocal =new ThreadLocal<>();

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        HttpServletRequest req = (HttpServletRequest) request;
        String authorization = req.getHeader(JWTUtil.AUTH_HEADER_KEY);
        if (StringUtils.isBlank(authorization) || !authorization.startsWith(JWTUtil.TOKEN_PREFIX)) {
            writeJsonResponse(response,  R.fail("身份校验失败"), HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        String token = authorization.replace(JWTUtil.TOKEN_PREFIX, "");
        JWTToken jwtToken = new JWTToken(token);
        try {
            Subject subject = getSubject(request, response);
            subject.login(jwtToken);
            return onLoginSuccess(jwtToken, subject, request, response);
        } catch (AuthenticationException e) {
            return onLoginFailure(jwtToken, e, request, response);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        writeJsonResponse(response, R.fail("身份校验失败"), HttpServletResponse.SC_UNAUTHORIZED);
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletResponse res = (HttpServletResponse) response;
        res.setStatus(HttpServletResponse.SC_FORBIDDEN);
        writeJsonResponse(response,R.fail("身份校验失败"), HttpServletResponse.SC_UNAUTHORIZED);
        return false;
    }

    /**
     * 对跨域提供支持
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
        // 跨域时会首先发送一个option请求，这里我们给option请求直接返回正常状态
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return false;
        }
        return super.preHandle(request, response);
    }

    /**
     * 只写入一次，所有使用 threadLocal
     * @param response
     * @param responseEntity
     * @param statusCode
     */
    public void writeJsonResponse(ServletResponse response, R responseEntity, int statusCode) {
        if(threadLocal.get()==null)
        {
            threadLocal.set(true);
        }
        else{
            return;
        }
        try {
            HttpServletResponse res = (HttpServletResponse) response;
            res.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            res.setStatus(statusCode);
            res.setCharacterEncoding("UTF-8");
            PrintWriter writer = res.getWriter();
            writer.write(JSON.toJSONString(responseEntity));
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}

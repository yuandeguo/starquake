package com.yuan.aspect;


import com.yuan.annotations.OperationLogAnnotation;
import com.yuan.pojo.OperationLog;
import com.yuan.pojo.User;
import com.yuan.service.RedisService;
import com.yuan.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Enumeration;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/3/30 13:28
 * @Description 操作日志切面处理类
 */
@Aspect
@Component
@Slf4j
public class OperationLogAspect {
    @Resource
    private RedisService redisService;


    /**
     * 设置操作日志切入点   在注解的位置切入代码
     */
    @Pointcut("@annotation(com.yuan.annotations.OperationLogAnnotation)")
    public void operLogPointCut() {
    }


    @AfterReturning(returning  /**
     * 记录操作日志
     * @param joinPoint 方法的执行点
     * @param result  方法返回值
     * @throws Throwable
     */ = "result", value = "operLogPointCut()")
    public void saveOperLog(JoinPoint joinPoint, Object result) {
        /**
         * RequestContextHolder是一个用于存储和获取当前请求线程的HttpServletRequest和HttpServletResponse对象的持有者
         * 获取request对象
         * @return
         */
        HttpServletRequest request= ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        try {
            //将返回值转换成map集合
          R result1 = (R) result;
            OperationLog operationLog = new OperationLog();
            // 从切面织入点处通过反射机制获取织入点处的方法
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            //获取切入点所在的方法
            Method method = signature.getMethod();
            //获取操作
            OperationLogAnnotation annotation = method.getAnnotation(OperationLogAnnotation.class);
            if (annotation != null) {
                operationLog.setModel(annotation.operModul());
                operationLog.setType(annotation.operType());
                operationLog.setDescription(annotation.operDesc());
            }
            //操作时间
            operationLog.setOperationTime(LocalDateTime.now());
            String authorization=request.getHeader("Authorization");
            //操作用户
            operationLog.setUserId(redisService.get(authorization, User.class).getId());

            //操作IP
            operationLog.setIp(getIpAdrress(request));
            //返回值信息
            operationLog.setResult(String.valueOf(result1.getCode()));
            //保存日志
            log.info("***OperationLogAspect.saveOperLog业务结束，结果:{}", operationLog);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  String getIpAdrress(HttpServletRequest request) {
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


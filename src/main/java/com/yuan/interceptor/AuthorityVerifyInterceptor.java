package com.yuan.interceptor;

import com.alibaba.fastjson.JSON;
import com.yuan.myEnum.CodeMsg;
import com.yuan.pojo.User;
import com.yuan.utils.DataCacheUtil;
import com.yuan.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/3/7 10:30
 * @Description null
 */
@Slf4j
public class AuthorityVerifyInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
     String auth=  request.getHeader("Authorization");
     if(auth==null) {
         response.setContentType("application/json;charset=UTF-8");
         response.getWriter().write(JSON.toJSONString(R.fail(CodeMsg.NOT_LOGIN.getCode(), CodeMsg.NOT_LOGIN.getMsg())));
         return false;
     }
    User user= (User) DataCacheUtil.get(auth);
        log.info("***AuthorityVerifyInterceptor.preHandle业务结束，结果:{}",user );
     if(user==null)
     {
         response.setContentType("application/json;charset=UTF-8");
         response.getWriter().write(JSON.toJSONString(R.fail(CodeMsg.NOT_LOGIN.getCode(), "登录已过期，请重新登录")));
         return false;
     }
     if(user.getUserType()!=0&&user.getUserType()!=1)
     {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(JSON.toJSONString(R.fail("权限不足，请更换管理员账号")));
            return false;
     }

        return true;
    }
}

package com.yuan.interceptor;

import com.alibaba.fastjson.JSON;
import com.yuan.myEnum.CodeMsg;
import com.yuan.myEnum.CommonConst;
import com.yuan.pojo.WebInfo;
import com.yuan.utils.DataCacheUtil;
import com.yuan.utils.R;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/2/27 0:33
 * @Description 拦截网址信息，当CommonConst.WEB_INFO是0，或者没有设置为1，则设置无法访问
 */
public class WebInfoHandlerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        WebInfo webInfo = (WebInfo) DataCacheUtil.get(CommonConst.WEB_INFO);
        if (webInfo == null || !webInfo.getStatus()) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(JSON.toJSONString(R.fail(CodeMsg.SYSTEM_REPAIR.getCode(), CodeMsg.SYSTEM_REPAIR.getMsg())));
            return false;
        } else {
            return true;
        }
    }

}

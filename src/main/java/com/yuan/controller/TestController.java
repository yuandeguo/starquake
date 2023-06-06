package com.yuan.controller;

import com.alibaba.fastjson.JSON;
import com.yuan.security.jwt.JWTUtil;
import com.yuan.utils.R;
import com.yuan.vo.UserJWTVo;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/6/4 21:50
 * @Description 测试
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @ResponseBody
    @RequestMapping("test1")
    public R getToken()
    {
        UserJWTVo userJWTVo=new UserJWTVo(1,"2","3","4","5",6,"7","8",true);
        Map<String,String> map=new HashMap<>();
        map.put(JWTUtil.USER_INFO, JSON.toJSONString(userJWTVo));
        String token = JWTUtil.createToken(map, JWTUtil.JWT_SECRET, JWTUtil.EXPIRATION_TIME);
        return R.success(token);
    }

    @ResponseBody
    @RequestMapping("test2")
    public String verifyToken(@RequestHeader("token") String token)
    {
        if (!JWTUtil.verifySpecifiedSecret(token, JWTUtil.JWT_SECRET)) {
          return "false";
        }
        UserJWTVo userJWTVo = JSON.parseObject(JWTUtil.getClaim(token, JWTUtil.USER_INFO), UserJWTVo.class);
        return  userJWTVo.toString();
    }

}

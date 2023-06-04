package com.yuan.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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
    public String test1()
    {



        return null;
    }


}

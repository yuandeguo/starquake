package com.yuan.controller;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.yuan.annotations.LoginCheck;
import com.yuan.myEnum.CommonConst;
import com.yuan.pojo.WebInfo;
import com.yuan.service.WebInfoService;
import com.yuan.utils.DataCacheUtil;
import com.yuan.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/2/28 16:29
 * @Description 仅站长可以操作
 */
@Slf4j
@RestController
@RequestMapping("/webInfo")
public class WebInfoController {

    @Resource
    private WebInfoService webInfoService;

    /**
     * 更新网站信息
     */
    @PostMapping("/admin/updateWebInfo")
    public R updateWebInfo(@RequestBody WebInfo webInfo) {
      return   webInfoService.updateWebInfo(webInfo);

    }
    /**
     * 这是管理员获取 网站信息
     */
    @GetMapping("/admin/getWebInfo")
    public R getWebInfo() {
       return  webInfoService.getWebInfo();

    }

    /**
     * 网站首页获取 网站信息
     */
    @GetMapping("/getWebInfo")
    public R getWebInfo0() {
      return  webInfoService.getWebInfo();

    }

}

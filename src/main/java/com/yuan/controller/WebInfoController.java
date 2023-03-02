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

        log.info("***WebInfoController.updateWebInfo业务结束，结果:{}", webInfo);
//注意：如果实体对象中某个属性为 null，不会更新该属性（即不会把对应的数据库字段值设置为 null）
        boolean b = webInfoService.updateById(webInfo);
        WebInfo newWebInfo = webInfoService.getWebInfo();
        if(!b) {
            R.fail("更新失败");
        }
        DataCacheUtil.put(CommonConst.WEB_INFO,newWebInfo);
        return R.success();
    }
    /**
     * 这是管理员获取 网站信息
     */
    @GetMapping("/admin/getWebInfo")
    public R getWebInfo() {
        WebInfo webInfo = webInfoService.getWebInfo();
        if(webInfo==null)
        {
            return R.fail("网站没有信息");
        }
        else {
            return R.success(webInfo);
        }
    }

    /**
     * 网站首页获取 网站信息
     */
    @GetMapping("/getWebInfo")
    public R getWebInfo0() {
        WebInfo webInfo = webInfoService.getWebInfo();
        if(webInfo==null)
        {
            return R.fail("网站没有信息");
        }
        else {
            webInfo.setRandomAvatar(null);
            webInfo.setRandomCover(null);
            webInfo.setRandomName(null);

            return R.success(webInfo);
        }
    }

}

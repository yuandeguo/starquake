package com.yuan.controller;

import com.yuan.exception.MyRuntimeException;
import com.yuan.pojo.User;
import com.yuan.service.RedisService;
import com.yuan.utils.QiniuUtil;
import com.yuan.utils.R;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/2/28 16:59
 * @Description 获取七牛云的上传凭证
 */
@RestController
@RequestMapping("/qiniu")
public class QiniuController {
    @Resource
    private RedisService redisService;

    /**
     * 获取覆盖凭证
     */
    @GetMapping("/getUpToken")
    public R<String> getUpToken(@RequestParam(value = "key", required = false) String key, @RequestHeader("Authorization") String authorization) {
        User user = redisService.get(authorization, User.class);
        if (user == null) {
            throw new MyRuntimeException("请先登录！");
        } else if (!StringUtils.hasText(user.getEmail())) {
            throw new MyRuntimeException("请先绑定邮箱！");
        }
        return R.success(QiniuUtil.getToken(key));
    }
}

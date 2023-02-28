package com.yuan.controller;

import com.yuan.annotations.LoginCheck;
import com.yuan.exception.MyRuntimeException;
import com.yuan.pojo.User;
import com.yuan.utils.DataCacheUtil;
import com.yuan.utils.QiniuUtil;
import com.yuan.utils.R;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/2/28 16:59
 * @Description 获取七牛云的上传凭证
 */
@RestController
@RequestMapping("/qiniu")
public class QiniuController {

    /**
     * 获取覆盖凭证
     */
    @GetMapping("/getUpToken")
    @LoginCheck
    public R<String> getUpToken(@RequestParam(value = "key", required = false) String key, @RequestHeader("Authorization") String authorization) {
        User user = (User) DataCacheUtil.get(authorization);
        if (user==null) {
            throw new MyRuntimeException("请先登录！");
        }
        else if(!StringUtils.hasText(user.getEmail())){
            throw new MyRuntimeException("请先绑定邮箱！");
        }


        return R.success(QiniuUtil.getToken(key));
    }
}

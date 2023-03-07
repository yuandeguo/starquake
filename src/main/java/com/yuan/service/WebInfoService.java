package com.yuan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuan.pojo.WebInfo;
import com.yuan.utils.R;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/2/28 16:30
 * @Description null
 */
public interface WebInfoService extends IService<WebInfo> {
    R getWebInfo();

    R updateWebInfo(WebInfo webInfo);
}

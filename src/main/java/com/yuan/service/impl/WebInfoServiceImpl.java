package com.yuan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.mapper.WebInfoMapper;
import com.yuan.pojo.WebInfo;
import com.yuan.service.WebInfoService;
import com.yuan.utils.R;
import org.springframework.stereotype.Service;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/2/28 16:34
 * @Description null
 */
@Service
public class WebInfoServiceImpl extends ServiceImpl<WebInfoMapper, WebInfo> implements WebInfoService {

    @Override
    public WebInfo getWebInfo() {

        WebInfo webInfo = baseMapper.selectById(1);

       return webInfo;
    }
}

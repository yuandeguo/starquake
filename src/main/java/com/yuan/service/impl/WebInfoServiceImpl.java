package com.yuan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.mapper.WebInfoMapper;
import com.yuan.myEnum.CommonConst;
import com.yuan.pojo.WebInfo;
import com.yuan.service.RedisService;
import com.yuan.service.WebInfoService;
import com.yuan.utils.DataCacheUtil;
import com.yuan.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/2/28 16:34
 * @Description null
 */
@Service
@Slf4j
public class WebInfoServiceImpl extends ServiceImpl<WebInfoMapper, WebInfo> implements WebInfoService {
    @Resource
    private RedisService redisService;
    @Override
    public R getWebInfo() {

        WebInfo webInfo = baseMapper.selectById(1);
        DataCacheUtil.put(CommonConst.WEB_INFO,webInfo);
       return R.success(webInfo);
    }

    @Override
    public R updateWebInfo(WebInfo webInfo) {

//注意：如果实体对象中某个属性为 null，不会更新该属性（即不会把对应的数据库字段值设置为 null）
        boolean b = updateById(webInfo);
        WebInfo newWebInfo = baseMapper.selectById(1);
        if(!b) {
            R.fail("更新失败");
        }
        DataCacheUtil.put(CommonConst.WEB_INFO,newWebInfo);
        return R.success();
    }
}

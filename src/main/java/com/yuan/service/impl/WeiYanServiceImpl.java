package com.yuan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.mapper.WeiYanMapper;
import com.yuan.myEnum.CommonConst;
import com.yuan.params.PageParam;
import com.yuan.pojo.User;
import com.yuan.pojo.WeiYan;
import com.yuan.service.RedisService;
import com.yuan.service.WeiYanService;
import com.yuan.utils.R;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;


/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/3/7 0:42
 * @Description null
 */
@Service
public class WeiYanServiceImpl extends ServiceImpl<WeiYanMapper, WeiYan> implements WeiYanService {
    @Resource
    private RedisService redisService;
    @Override
    public R listWeiYan(PageParam pageParam) {
        IPage<WeiYan> page=new Page<>(pageParam.getCurrent(),pageParam.getSize());
        QueryWrapper<WeiYan> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("is_public",Boolean.TRUE);
        queryWrapper.orderByDesc("create_time");
        IPage<WeiYan> weiYanIPage = baseMapper.selectPage(page, queryWrapper);
        return R.success(weiYanIPage);
    }

    @Override
    public R saveWeiYan(WeiYan weiYanVO, String authorization) {
        User user=  redisService.get(authorization, User.class);
        assert user != null;
        if(user.getUserType()!=0)
            return R.fail("错误，不是管理员");

        if (!StringUtils.hasText(weiYanVO.getContent())) {
            return R.fail("微言不能为空！");
        }
        WeiYan weiYan = new WeiYan();
        weiYan.setUserId(user.getId());
        weiYan.setContent(weiYanVO.getContent());
        weiYan.setIsPublic(weiYanVO.getIsPublic());
        weiYan.setType(CommonConst.WEIYAN_TYPE_FRIEND);
        weiYan.setCreateTime(LocalDateTime.now());
        save(weiYan);
        return R.success();
    }

    @Override
    public R deleteWeiYan(Integer id, String authorization) {

        User user= redisService.get(authorization,User.class);

        assert user != null;
        if(user.getUserType()!=0)
            return R.fail("错误，不是管理员");
        Integer userId = user.getId();

            lambdaUpdate().eq(WeiYan::getId, id)
                .eq(WeiYan::getUserId, userId)
                .remove();
        return R.success();
    }
}
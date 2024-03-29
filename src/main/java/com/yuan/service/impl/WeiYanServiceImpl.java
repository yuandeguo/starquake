package com.yuan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.mapper.WeiYanMapper;
import com.yuan.myEnum.CommonConst;
import com.yuan.myEnum.ParamsEnum;
import com.yuan.params.PageParam;
import com.yuan.pojo.User;
import com.yuan.pojo.WeiYan;
import com.yuan.service.RedisService;
import com.yuan.service.WeiYanService;
import com.yuan.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
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
@Slf4j
public class WeiYanServiceImpl extends ServiceImpl<WeiYanMapper, WeiYan> implements WeiYanService {
    @Resource
    private RedisService redisService;

    @Override
    public R listWeiYan(PageParam pageParam) {
        Page<WeiYan> res = redisService.get("listWeiYan:" + pageParam.getCurrent() + ':' + pageParam.getSize(), Page.class);
        if (res == null) {

            res = new Page<>(pageParam.getCurrent(), pageParam.getSize());
            QueryWrapper<WeiYan> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("is_public", Boolean.TRUE);
            queryWrapper.orderByDesc("create_time");
            res = baseMapper.selectPage(res, queryWrapper);
            redisService.set("listWeiYan:" + pageParam.getCurrent() + ':' + pageParam.getSize(), res, CommonConst.CACHE_EXPIRE);
            log.info("***WeiYanServiceImpl.listWeiYan业务结束，结果:{}", res);
            ;
        }
        return R.success(res);
    }

    @Override
    public R saveWeiYan(WeiYan weiYanVO) {
        User user =(User) SecurityUtils.getSubject().getPrincipals().getPrimaryPrincipal();
        assert user != null;
        if (user.getUserType() != ParamsEnum.USER_TYPE_ADMIN.getCode())
            return R.fail("错误，站长");

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
        redisService.removeList("listWeiYan:*");
        return R.success();
    }

    @Override
    public R deleteWeiYan(Integer id) {
        User user = (User) SecurityUtils.getSubject().getPrincipals().getPrimaryPrincipal();
        assert user != null;
        if (user.getUserType() != ParamsEnum.USER_TYPE_ADMIN.getCode())
            return R.fail("错误，不是管理员");
        Integer userId = user.getId();
        lambdaUpdate().eq(WeiYan::getId, id)
                .eq(WeiYan::getUserId, userId)
                .remove();
        redisService.removeList("listWeiYan:*");
        return R.success();
    }
}
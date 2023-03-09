package com.yuan.service.impl;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.mapper.ResourceMapper;
import com.yuan.params.SearchResourceParam;
import com.yuan.pojo.Resource;
import com.yuan.pojo.User;
import com.yuan.service.RedisService;
import com.yuan.service.ResourceService;
import com.yuan.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/2/28 17:13
 * @Description null
 */
@Service
@Slf4j
public class ResourceServiceImpl extends ServiceImpl<ResourceMapper, Resource> implements ResourceService {
    @javax.annotation.Resource
    private RedisService redisService;
    @Override
    public R listResource(SearchResourceParam searchResourceParam) {
        QueryWrapper<Resource> queryWrapper=new QueryWrapper<>();
        if(StringUtils.hasText(searchResourceParam.getResourceType()))
        {
            queryWrapper.eq("type",searchResourceParam.getResourceType());
        }
        queryWrapper.orderByDesc("id");
        IPage<Resource> page=new Page<>(searchResourceParam.getCurrent(),searchResourceParam.getSize());
        page= baseMapper.selectPage(page, queryWrapper);
         return R.success(page);

    }

    @Override
    public R saveResource(Resource resource, String authorization) {
        if (!StringUtils.hasText(resource.getType()) || !StringUtils.hasText(resource.getPath())) {
            return R.fail("资源类型和资源路径不能为空！");
        }
        Resource re = new Resource();
        re.setPath(resource.getPath());
        re.setType(resource.getType());
        re.setSize(resource.getSize());
        re.setMimeType(resource.getMimeType());
        re.setUserId(( redisService.get(authorization,User.class)).getId());
        re.setCreateTime( LocalDateTimeUtil.now());
        save(re);
        return R.success();
    }
}

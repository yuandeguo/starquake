package com.yuan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.mapper.ResourceMapper;
import com.yuan.params.SearchResourceParam;
import com.yuan.pojo.Resource;
import com.yuan.pojo.User;
import com.yuan.service.ResourceService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/2/28 17:13
 * @Description null
 */
@Service
public class ResourceServiceImpl extends ServiceImpl<ResourceMapper, Resource> implements ResourceService {

    @Override
    public IPage<Resource> listResource(SearchResourceParam searchResourceParam) {
        QueryWrapper<Resource> queryWrapper=new QueryWrapper<>();
        if(StringUtils.hasText(searchResourceParam.getResourceType()))
        {
            queryWrapper.eq("type",searchResourceParam.getResourceType());
        }
        queryWrapper.orderByDesc("id");
        IPage<Resource> page=new Page<>(searchResourceParam.getCurrent(),searchResourceParam.getSize());
        page= baseMapper.selectPage(page, queryWrapper);
         return page;

    }
}

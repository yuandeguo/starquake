package com.yuan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.mapper.ResourcePathMapper;
import com.yuan.params.SearchResourcePathParam;
import com.yuan.pojo.Resource;
import com.yuan.pojo.ResourcePath;
import com.yuan.service.ResourcePathService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/3/1 21:14
 * @Description null
 */
@Service
public class ResourcePathServiceImpl extends ServiceImpl<ResourcePathMapper, ResourcePath> implements ResourcePathService {

    @Override
    public IPage<ResourcePath> listResourcePath(SearchResourcePathParam resourcePathParam) {
        QueryWrapper<ResourcePath> queryWrapper=new QueryWrapper<>();
        if(StringUtils.hasText(resourcePathParam.getResourceType()))
        {
            queryWrapper.eq("type",resourcePathParam.getResourceType());
        }
        if(resourcePathParam.getStatus()!=null)
        { queryWrapper.eq("status",resourcePathParam.getStatus());
        }
        queryWrapper.orderByDesc("id");
        IPage<ResourcePath> page=new Page<>(resourcePathParam.getCurrent(),resourcePathParam.getSize());
        page= baseMapper.selectPage(page, queryWrapper);
        return page;


    }
}

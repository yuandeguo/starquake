package com.yuan.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.mapper.ResourcePathMapper;
import com.yuan.params.PageParam;
import com.yuan.params.SearchResourcePathParam;
import com.yuan.pojo.Resource;
import com.yuan.pojo.ResourcePath;
import com.yuan.service.ResourcePathService;
import com.yuan.utils.R;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/3/1 21:14
 * @Description null
 */
@Service
public class ResourcePathServiceImpl extends ServiceImpl<ResourcePathMapper, ResourcePath> implements ResourcePathService {

    @Override
    public R listResourcePath(SearchResourcePathParam resourcePathParam) {
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
        return R.success(page);


    }

    @Override
    public R listResourcePathOnFront(PageParam pageParam) {
     QueryWrapper<ResourcePath> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("type","friendUrl");
        queryWrapper.eq("status", Boolean.TRUE);
        IPage<ResourcePath> page=new Page<>(pageParam.getCurrent(),pageParam.getSize());
        page= baseMapper.selectPage(page, queryWrapper);
        return R.success(page);
    }
}

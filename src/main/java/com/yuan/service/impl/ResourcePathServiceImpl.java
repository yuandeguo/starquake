package com.yuan.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.mapper.ResourcePathMapper;
import com.yuan.myEnum.CommonConst;
import com.yuan.params.PageParam;
import com.yuan.params.SearchResourcePathParam;
import com.yuan.pojo.ResourcePath;
import com.yuan.service.RedisService;
import com.yuan.service.ResourcePathService;
import com.yuan.utils.DataCacheUtil;
import com.yuan.utils.R;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/3/1 21:14
 * @Description null
 */
@Service
public class ResourcePathServiceImpl extends ServiceImpl<ResourcePathMapper, ResourcePath> implements ResourcePathService {
    @Resource
    private RedisService redisService;
    @Resource
    ResourcePathMapper resourcePathMapper;

    @Override
    public R listResourcePath(SearchResourcePathParam resourcePathParam) {
        QueryWrapper<ResourcePath> queryWrapper = new QueryWrapper<>();
        if (StringUtils.hasText(resourcePathParam.getResourceType())) {
            queryWrapper.eq("type", resourcePathParam.getResourceType());
        }
        if (resourcePathParam.getStatus() != null) {
            queryWrapper.eq("status", resourcePathParam.getStatus());
        }
        queryWrapper.orderByDesc("id");
        IPage<ResourcePath> page = new Page<>(resourcePathParam.getCurrent(), resourcePathParam.getSize());
        page = baseMapper.selectPage(page, queryWrapper);
        return R.success(page);


    }

    @Override
    public R listResourcePathOnFriendUrl(PageParam pageParam) {
        QueryWrapper<ResourcePath> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", CommonConst.RESOURCE_PATH_TYPE_FRIEND);
        queryWrapper.eq("status", Boolean.TRUE);
        IPage<ResourcePath> page = new Page<>(pageParam.getCurrent(), pageParam.getSize());
        List<ResourcePath> records = page.getRecords();

        for (ResourcePath item : records) {
            if (!StringUtils.hasText(item.getCover())) {
                item.setCover(DataCacheUtil.getRandomCover());
            }
        }

        page = baseMapper.selectPage(page, queryWrapper);
        return R.success(page);
    }

    @Override
    public R listAllClassifys() {
        List<String> records = resourcePathMapper.listAllClassifys();
        return R.success(records);
    }

    @Override
    public R listResourcePathOnFavoritesUrl() {
        List<String> allClassifys = resourcePathMapper.listAllClassifys();
        Map<String, List<ResourcePath>> resultMap = allClassifys.stream()
                .collect(Collectors.toMap(classify -> classify, classify -> new ArrayList<>()));
        QueryWrapper<ResourcePath> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", CommonConst.RESOURCE_PATH_TYPE_FAVORITES);
        queryWrapper.eq("status", Boolean.TRUE);
        List<ResourcePath> resourcePaths = baseMapper.selectList(queryWrapper);
        resourcePaths.stream().forEach(obj -> resultMap.get(obj.getClassify()).add(obj));
        return R.success(resultMap);
    }


}

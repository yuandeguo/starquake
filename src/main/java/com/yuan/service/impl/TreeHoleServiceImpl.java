package com.yuan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.mapper.TreeHoleMapper;
import com.yuan.mapper.UserMapper;
import com.yuan.myEnum.CommonConst;
import com.yuan.params.PageParam;
import com.yuan.pojo.TreeHole;
import com.yuan.pojo.User;
import com.yuan.service.TreeHoleService;
import com.yuan.service.UserService;
import com.yuan.utils.DataCacheUtil;
import com.yuan.utils.GetRequestParamsUtil;
import com.yuan.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Random;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/3/6 16:31
 * @Description null
 */
@Slf4j
@Service
public class TreeHoleServiceImpl extends ServiceImpl<TreeHoleMapper, TreeHole> implements TreeHoleService {
    @Override
    public R listTreeHole() {
        List<TreeHole> treeHoles;
        Integer count = baseMapper.selectCount(null);
        if (count > CommonConst.TREE_HOLE_COUNT) {
            int i = new Random().nextInt(count + 1 - CommonConst.TREE_HOLE_COUNT);
            QueryWrapper<TreeHole> queryWrapper=new QueryWrapper<>();
            queryWrapper.last("limit "+String.valueOf(i)+", 200");

            treeHoles =baseMapper.selectList(queryWrapper);
        } else {
            treeHoles = baseMapper.selectList(null);
        }

        treeHoles.forEach(treeHole -> {
            if (!StringUtils.hasText(treeHole.getAvatar())) {
                //设置随机头像
                treeHole.setAvatar(GetRequestParamsUtil.getRandomAvatar(treeHole.getId().toString()));
            }
            treeHole.setDeleted(null);
        });
        return R.success(treeHoles);
    }

    @Override
    public R treeHoleListByAdmin(PageParam pageParam) {
        QueryWrapper<TreeHole> queryWrapper=new QueryWrapper<>();

        IPage<TreeHole> page=new Page<>(pageParam.getCurrent(),pageParam.getSize());
        page= baseMapper.selectPage(page, queryWrapper);
        return R.success(page);
    }
}

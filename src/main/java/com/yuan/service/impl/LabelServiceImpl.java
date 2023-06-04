package com.yuan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.mapper.LabelMapper;
import com.yuan.mapper.ResourceMapper;
import com.yuan.myEnum.CommonConst;
import com.yuan.pojo.Label;
import com.yuan.service.LabelService;
import com.yuan.service.RedisService;
import com.yuan.service.ResourceService;
import com.yuan.utils.R;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.Serializable;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/3/1 0:53
 * @Description null
 */
@Service
public class LabelServiceImpl extends ServiceImpl<LabelMapper, Label> implements LabelService {

    @Resource
    private RedisService redisService;

    @Override
    public R deleteLabel(Integer id) {
        boolean b = removeById(id);
        if (!b) {
            return R.fail("标签删除失败");
        }
        redisService.remove(CommonConst.Label_CACHE + "ById:" + id);
        redisService.remove("listSortAndLabel:getSortInfo");
        redisService.remove("listSortAndLabel:list");
        return R.success();
    }

    @Override
    public R saveLabel(Label label) {
        boolean b = save(label);
        if (!b) {
            return R.fail("标签保存失败");
        }
        redisService.remove("listSortAndLabel:getSortInfo");
        redisService.remove("listSortAndLabel:list");
        return R.success();
    }

    @Override
    public R updateLabel(Label label) {

        boolean b = updateById(label);
        if (!b) {

            return R.fail("标签修改失败");
        }
        redisService.remove(CommonConst.Label_CACHE + "ById:" + label.getId());
        redisService.remove("listSortAndLabel:getSortInfo");
        redisService.remove("listSortAndLabel:list");
        return R.success();
    }

    @Override
    public Label getById(Serializable id) {
        Label label = redisService.get(CommonConst.Label_CACHE + "ById:" + id, Label.class);
        if (label == null) {
            label = super.getById(id);
            redisService.set(CommonConst.Label_CACHE + "ById:" + id, label, CommonConst.CACHE_EXPIRE);
        }
        return label;
    }
}

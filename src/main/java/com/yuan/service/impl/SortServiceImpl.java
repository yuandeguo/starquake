package com.yuan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.mapper.ResourceMapper;
import com.yuan.mapper.SortMapper;
import com.yuan.myEnum.CommonConst;
import com.yuan.pojo.Article;
import com.yuan.pojo.Label;
import com.yuan.pojo.Sort;
import com.yuan.service.*;
import com.yuan.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/3/1 0:46
 * @Description null
 */
@Slf4j
@Service
public class SortServiceImpl extends ServiceImpl<SortMapper, Sort> implements SortService {

    @Resource
    LabelService labelService;
    @Resource
    private RedisService redisService;
    @Resource
    private ArticleService articleService;
    @Override
    public R getSortInfo() {
        List<Sort> list=   redisService.get("listSortAndLabel:getSortInfo",List.class);
        if(list==null)
        {
            list = baseMapper.selectList(null);
            for (Sort item : list) {
                QueryWrapper<Label> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("sort_id", item.getId());
                List<Label> listLabel = labelService.list(queryWrapper);

                for (Label label : listLabel) {
                    QueryWrapper<Article> labelArticleCount = new QueryWrapper<>();
                    labelArticleCount.eq("label_id", label.getId());
                    label.setCountOfLabel(articleService.count(labelArticleCount));
                }
                item.setLabels(listLabel);
                QueryWrapper<Article> SortArticleCount = new QueryWrapper<>();
                SortArticleCount.eq("sort_id", item.getId());
                item.setCountOfSort(articleService.count(SortArticleCount));
            }
            redisService.set("listSortAndLabel:getSortInfo", list,CommonConst.CACHE_EXPIRE);
        }
        return R.success(list);
    }

    @Override
    public R listSortAndLabel() {
        Map<String, List> map=   redisService.get("listSortAndLabel:list",Map.class);
        if(map==null)
        {
            List<Sort> sortList = baseMapper.selectList(null);
            List<Label> labelList = labelService.list();
           map = new HashMap<>();
            map.put("sorts", sortList);
            map.put("labels", labelList);
            redisService.set("listSortAndLabel:list", map,CommonConst.CACHE_EXPIRE);
        }

        return R.success(map)  ;
    }

    @Override
    public R deleteSort(Integer id) {
        boolean b = removeById(id);
        if(!b) {
            return R.fail("分类删除失败");
        }
        redisService.remove(CommonConst.SORT_CACHE + "ById:" + id);
        redisService.remove("listSortAndLabel:getSortInfo");
        redisService.remove("listSortAndLabel:list");
        return R.success();
    }

    @Override
    public R saveSort(Sort sort) {
        boolean b = save(sort);
        if(!b) {
            return R.fail("分类保存失败");
        }
        redisService.remove("listSortAndLabel:getSortInfo");
        redisService.remove("listSortAndLabel:list");
        return R.success();
    }

    @Override
    public R updateSort(Sort sort) {
        boolean b = updateById(sort);
        if(!b) {
            return R.fail("分类修改失败");
        }
        redisService.remove(CommonConst.SORT_CACHE + "ById:" + sort.getId());
        redisService.remove("listSortAndLabel:getSortInfo");
        redisService.remove("listSortAndLabel:list");
        return R.success();
    }

    @Override
    public Sort getById(Serializable id) {
        Sort sort = redisService.get(CommonConst.SORT_CACHE + "ById:" + id, Sort.class);
        if(sort==null) {
            sort = super.getById(id);
            redisService.set(CommonConst.SORT_CACHE + "ById:" + id, sort,CommonConst.CACHE_EXPIRE);
        }
        return sort;
    }
}

package com.yuan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.mapper.ResourceMapper;
import com.yuan.mapper.SortMapper;
import com.yuan.pojo.Article;
import com.yuan.pojo.Label;
import com.yuan.pojo.Sort;
import com.yuan.service.ArticleService;
import com.yuan.service.LabelService;
import com.yuan.service.ResourceService;
import com.yuan.service.SortService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
    private ArticleService articleService;
    @Override
    public List<Sort> getSortInfo() {
        List<Sort> list = baseMapper.selectList(null);
        for(Sort item:list)
        {
            QueryWrapper<Label> queryWrapper=new QueryWrapper<>();
            queryWrapper.eq("sort_id",item.getId());
            List<Label> listLabel = labelService.list(queryWrapper);

            for(Label label:listLabel) {
                QueryWrapper<Article> labelArticleCount=new QueryWrapper<>();
                labelArticleCount.eq("label_id",label.getId());
                label.setCountOfLabel( articleService.count(labelArticleCount));
            }
            item.setLabels(listLabel);
            QueryWrapper<Article> SortArticleCount=new QueryWrapper<>();
            SortArticleCount.eq("sort_id",item.getId());
            item.setCountOfSort( articleService.count(SortArticleCount));
        }
        log.info("***SortController.getSortInfo业务结束，结果:{}",list );
        return list;
    }

    @Override
    public Map<String, List> listSortAndLabel() {
        List<Sort> sortList=baseMapper.selectList(null);
        List<Label> labelList=labelService.list();
        Map<String, List> map = new HashMap<>();
        map.put("sorts",sortList);
        map.put("labels",labelList);
        return map;
    }
}
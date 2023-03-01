package com.yuan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.mapper.CommentMapper;
import com.yuan.myEnum.CommentTypeEnum;
import com.yuan.params.SearchCommentParam;
import com.yuan.pojo.Article;
import com.yuan.pojo.Comment;
import com.yuan.pojo.User;
import com.yuan.service.ArticleService;
import com.yuan.service.CommentService;
import com.yuan.utils.DataCacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/3/1 10:28
 * @Description null
 */
@Slf4j
@Service
public class CommentServiceImpl  extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    @Resource
    ArticleService articleService;
    @Override
    public IPage<Comment> searchCommentList(SearchCommentParam searchCommentParam, String authorization) {
        QueryWrapper<Comment> queryWrapper=new QueryWrapper<>();
        User user=(User)DataCacheUtil.get(authorization);
        //如果不是boss，只能查看自己文章的评论
        if(user.getUserType()!=0)
        {
            QueryWrapper<Article> articleQueryWrapper=new QueryWrapper<>();
            articleQueryWrapper.select("id");
            articleQueryWrapper.eq("user_id",user.getId());
            List<Integer> integers =   articleService.listObjs(articleQueryWrapper).stream().map(i->(Integer)i).collect(Collectors.toList());
            log.info("***CommentServiceImpl.searchCommentList业务结束，结果:{}",integers );
            IPage<Comment> page=new Page<>();
            if(integers.isEmpty())
                return  page;
            queryWrapper.in("source",integers);
            queryWrapper.eq("type", CommentTypeEnum.COMMENT_TYPE_ARTICLE.getCode());
        }
        else {
            if (searchCommentParam.getSource() != null) {
                queryWrapper.eq("source", searchCommentParam.getSource());
            }
            if (StringUtils.hasText(searchCommentParam.getCommentType())) {
                queryWrapper.eq("type", searchCommentParam.getCommentType());
            }
        }
        IPage<Comment> page=new Page<>(searchCommentParam.getCurrent(),searchCommentParam.getSize());
        page= baseMapper.selectPage(page, queryWrapper);
        log.info("***CommentServiceImpl.searchCommentList业务结束，结果:{}", page.getRecords());
        return page;
    }
}

package com.yuan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.mapper.ArticleMapper;
import com.yuan.myEnum.CommentTypeEnum;
import com.yuan.myEnum.CommonConst;
import com.yuan.params.ArticleUpdateStatusParams;
import com.yuan.params.SearchArticleParam;
import com.yuan.pojo.*;
import com.yuan.service.*;
import com.yuan.utils.DataCacheUtil;
import com.yuan.utils.R;
import com.yuan.vo.ArticleVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/2/28 23:37
 * @Description null
 */
@Slf4j
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {
    @Resource
    private SortService sortService;
    @Resource
    private LabelService labelService;
    @Resource
    private UserService userService;
    @Resource
    private CommentService commentService;

    @Resource
    private WeiYanService weiYanService;

    @Override
    public IPage<ArticleVo> listArticle(SearchArticleParam searchArticleParam, String  authorization) {

        QueryWrapper<Article> queryWrapper=new QueryWrapper<>();
        if (StringUtils.hasText(searchArticleParam.getSearchKey())) {
            queryWrapper.eq("article_title", searchArticleParam.getSearchKey());
        }
        if (searchArticleParam.getRecommendStatus() != null) {
            queryWrapper.eq("recommend_status", searchArticleParam.getRecommendStatus());
        }
        if (searchArticleParam.getSortId() != null) {
            queryWrapper.eq("sort_id", searchArticleParam.getSortId());
        }
        if (searchArticleParam.getLabelId() != null) {
            queryWrapper.eq("label_id", searchArticleParam.getLabelId());
        }
        if(!searchArticleParam.getIsBossSearch())//不是boss只能查看直接的文章
        {
            queryWrapper.eq("user_id", ((User)DataCacheUtil.get(authorization)).getId());
        }



        IPage<Article> page=new Page<>(searchArticleParam.getCurrent(),searchArticleParam.getSize());
        page= baseMapper.selectPage(page, queryWrapper);
        List<Article> records = page.getRecords();
        IPage<ArticleVo> articleIPage=new Page<>();
        List<ArticleVo> collect=new ArrayList<>();
        if (!CollectionUtils.isEmpty(records)) {
             collect = records.stream().map(article -> {
                ArticleVo articleVO = buildArticleVO(article);
                return articleVO;
            }).collect(Collectors.toList());
        }
        articleIPage.setRecords(collect);
        articleIPage.setTotal(page.getTotal());
        log.info("***ArticleServiceImpl.listArticle业务结束，结果:{}", articleIPage.getRecords());

        return articleIPage;
    }

    /**
     * 主界面查找文章
     * @param searchArticleParam
     * @return
     */
    @Override
    public R listArticleByFront(SearchArticleParam searchArticleParam) {
        QueryWrapper<Article> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("view_status",1);
        queryWrapper.orderByDesc("create_time");
        if (searchArticleParam.getRecommendStatus() != null) {
            queryWrapper.eq("recommend_status", searchArticleParam.getRecommendStatus());
        }
        if (searchArticleParam.getSortId() != null) {
            queryWrapper.eq("sort_id", searchArticleParam.getSortId());
        }
        if (searchArticleParam.getLabelId() != null) {
            queryWrapper.eq("label_id", searchArticleParam.getLabelId());
        }
        IPage<Article> page=new Page<>(searchArticleParam.getCurrent(),searchArticleParam.getSize());
        page= baseMapper.selectPage(page, queryWrapper);
        List<Article> records = page.getRecords();
        IPage<ArticleVo> articleIPage=new Page<>();
        List<ArticleVo> collect=new ArrayList<>();
        if (!CollectionUtils.isEmpty(records)) {
            collect = records.stream().map(article -> {
                ArticleVo articleVO = buildArticleVO(article);
                return articleVO;
            }).collect(Collectors.toList());
        }
        articleIPage.setRecords(collect);
        articleIPage.setTotal(page.getTotal());
        return R.success(articleIPage);

    }

    @Override
    public ArticleVo getByIdToFront(Integer id) {
        Article article = baseMapper.selectById(id);
        ArticleVo articleVO = buildArticleVO(article);
        return articleVO;

    }

    @Override
    public R saveArticle(Article article, String authorization) {
                if(!article.getViewStatus())
        {
            if(!StringUtils.hasText(article.getPassword()))
            {
                return R.fail("文章参数错误");
            }
        }
        Integer userId=  ((User)DataCacheUtil.get(authorization)).getId();
        article.setUserId(userId);
        article.setCreateTime(LocalDateTime.now());
        int insert = baseMapper.insert(article);
        if(insert!=1)
        {
            return R.fail("文章保存失败");
        }
        WeiYan weiYan = new WeiYan();
        weiYan.setUserId(userId);
        weiYan.setContent("发表新文章: <br>"+"   "+article.getArticleTitle());
        weiYan.setIsPublic(Boolean.TRUE);
        weiYan.setSource(article.getId());
        weiYan.setType(CommonConst.WEIYAN_TYPE_FRIEND);
        weiYan.setCreateTime(LocalDateTime.now());
        weiYanService.save(weiYan);


        return R.success();

    }

    /**
     * 更新文章状态
     * @param articleUpdateStatusParams
     * @return
     */
    @Override
    public R updateArticleStatus(ArticleUpdateStatusParams articleUpdateStatusParams) {
        Article article=new Article();
        article.setId(articleUpdateStatusParams.getArticleId());
        article.setViewStatus(articleUpdateStatusParams.getViewStatus());
        article.setCommentStatus(articleUpdateStatusParams.getCommentStatus());
        article.setRecommendStatus(articleUpdateStatusParams.getRecommendStatus());
        boolean b = updateById(article);
        if(!b) {
            return R.fail("更新失败");
        }
return R.success();
    }

    @Override
    public R deleteArticle(Integer id) {
        boolean b = removeById(id);
        if(!b) {
            return R.fail("更新失败");
        }
        return R.success();
    }

    @Override
    public R getArticleById(Integer id) {
        Article byId = getById(id);
        if(byId==null)
        {
            return R.fail("错误，文章不存在");
        }
        return R.success(byId);
    }

    @Override
    public R updateArticle(Article article) {


        boolean b=updateById(article);
        if(!b)
        {
            return R.fail("文章更新失败");
        }
        return R.success();
    }

    @Override
    public R getArticleByIdFront(Integer id) {
        ArticleVo byId = getByIdToFront(id);
        return R.success(byId);

    }

    /**
     * 封装对象
     * @param article
     * @return
     */
    private ArticleVo buildArticleVO(Article article){
        ArticleVo articleVO = new ArticleVo();
        BeanUtils.copyProperties(article, articleVO);
        if (!StringUtils.hasText(articleVO.getArticleCover())) {
           // 没有封面就随机封面，还没做好
            articleVO.setArticleCover("http://rqldcqw23.hn-bkt.clouddn.com/articleCover/Sara11677397871718631.jpg");
        }
        articleVO.setSort(sortService.getById(articleVO.getSortId()));
        articleVO.setLabel(labelService.getById(articleVO.getLabelId()));
        articleVO.setUsername(userService.getById(articleVO.getUserId()).getUsername());
        QueryWrapper<Comment> commentQueryWrapper=new QueryWrapper<>();
        commentQueryWrapper.eq("type", CommentTypeEnum.COMMENT_TYPE_ARTICLE.getCode());
        commentQueryWrapper.eq("source", articleVO.getId());
        articleVO.setCommentCount(commentService.count(commentQueryWrapper));
        return articleVO;
    }


}

package com.yuan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuan.mapper.ArticleMapper;
import com.yuan.myEnum.CommentTypeEnum;
import com.yuan.myEnum.CommonConst;
import com.yuan.params.ArticleUpdateStatusParams;
import com.yuan.params.SearchArticleParam;
import com.yuan.pojo.*;
import com.yuan.service.*;
import com.yuan.utils.R;
import com.yuan.vo.ArticleVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
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

    @Resource
    private RedisService redisService;


    /**
     * 后台管理查找文章
     *
     * @param searchArticleParam
     * @param authorization
     * @return
     */
    @Override
    public R listArticle(SearchArticleParam searchArticleParam ) {

        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
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
        if (!searchArticleParam.getIsBossSearch())//不是boss只能查看直接的文章
        {
            queryWrapper.eq("user_id", ((User) SecurityUtils.getSubject().getPrincipals().getPrimaryPrincipal()).getId());
        }
        queryWrapper.orderByDesc("create_time");
        IPage<Article> page = new Page<>(searchArticleParam.getCurrent(), searchArticleParam.getSize());
        page = baseMapper.selectPage(page, queryWrapper);
        List<Article> records = page.getRecords();
        IPage<ArticleVo> articleIPage = new Page<>();
        List<ArticleVo> collect = new ArrayList<>();
        if (!CollectionUtils.isEmpty(records)) {
            collect = records.stream().map(article -> {
                ArticleVo articleVO = buildArticleVO(article, true);
                return articleVO;
            }).collect(Collectors.toList());
        }
        articleIPage.setRecords(collect);
        articleIPage.setTotal(page.getTotal());

        return R.success(articleIPage);
    }

    /**
     * 主界面查找文章
     *
     * @param searchArticleParam
     * @return
     */
    @Override
    public R listArticleByFront(SearchArticleParam searchArticleParam) {

        IPage<ArticleVo> articleIPage = null;
        articleIPage = redisService.get("listArticleByFront:" + searchArticleParam.getRecommendStatus() + ":" + searchArticleParam.getSortId() + ":" + searchArticleParam.getLabelId() + ":" + searchArticleParam.getCurrent() + ":" + searchArticleParam.getSize(), Page.class);

        if (articleIPage == null) {
            QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("view_status", 1);
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
            IPage<Article> page = null;
            page = new Page<>(searchArticleParam.getCurrent(), searchArticleParam.getSize());
            queryWrapper.select("substring(article_content,1,100) article_content", "id", "user_id", "sort_id", "label_id", "article_cover", "article_title"
                    , "view_count", "like_count", "password", "recommend_status", "comment_status", "create_time", "update_time");
            page = baseMapper.selectPage(page, queryWrapper);

            List<Article> records = page.getRecords();
            articleIPage = new Page<>();
            List<ArticleVo> collect = new ArrayList<>();

            if (!CollectionUtils.isEmpty(records)) {
                collect = records.stream().map(article -> {
                    ArticleVo articleVO = buildArticleVO(article, true);
                    return articleVO;
                }).collect(Collectors.toList());
            }
            articleIPage.setRecords(collect);
            articleIPage.setTotal(page.getTotal());
            redisService.set("listArticleByFront:" + searchArticleParam.getRecommendStatus() + ":" + searchArticleParam.getSortId() + ":" + searchArticleParam.getLabelId() + ":" + searchArticleParam.getCurrent() + ":" + searchArticleParam.getSize(), articleIPage, CommonConst.CACHE_EXPIRE);
        } else {
            List<ArticleVo> records = articleIPage.getRecords();
            for (Object r : records) {
                articleVOSetCount((Map) r);
            }

        }

        return R.success(articleIPage);

    }


    /**
     * 保存文章信息
     *
     * @param article
     * @return
     */
    @Override
    @Transactional
    public R saveArticle(Article article ) {
        if (!article.getViewStatus()) {
            if (!StringUtils.hasText(article.getPassword())) {
                return R.fail("文章参数错误");
            }
        }
        Integer userId = ((User) SecurityUtils.getSubject().getPrincipals().getPrimaryPrincipal()).getId();
        article.setUserId(userId);
        article.setCreateTime(LocalDateTime.now());
        article.setUpdateTime(LocalDateTime.now());
        int insert = baseMapper.insert(article);
        if (insert != 1) {
            return R.fail("文章保存失败");
        }
        WeiYan weiYan = new WeiYan();
        weiYan.setUserId(userId);
        weiYan.setContent("发表新文章: <br>" + "   " + article.getArticleTitle());
        weiYan.setIsPublic(Boolean.TRUE);
        weiYan.setSource(article.getId());
        weiYan.setType(CommonConst.WEIYAN_TYPE_FRIEND);
        weiYan.setCreateTime(LocalDateTime.now());
        weiYanService.save(weiYan);
        redisService.removeList("listWeiYan:*");
        redisService.removeList("listArticleByFront:*");
        return R.success();

    }

    /**
     * 更新文章状态
     *
     * @param articleUpdateStatusParams
     * @return
     */
    @Override
    public R updateArticleStatus(ArticleUpdateStatusParams articleUpdateStatusParams) {
        redisService.remove(CommonConst.ARTICLE_CACHE + "ById:" + articleUpdateStatusParams.getArticleId());
        Article article = new Article();
        article.setId(articleUpdateStatusParams.getArticleId());
        article.setViewStatus(articleUpdateStatusParams.getViewStatus());
        article.setCommentStatus(articleUpdateStatusParams.getCommentStatus());
        article.setRecommendStatus(articleUpdateStatusParams.getRecommendStatus());
        boolean b = updateById(article);
        if (!b) {
            return R.fail("更新失败");
        }
        return R.success();
    }

    /**
     * 删除文章
     *
     * @param id
     * @return
     */
    @Override
    @Transactional
    public R deleteArticle(Integer id) {
        redisService.remove(CommonConst.ARTICLE_CACHE + "ById:" + id);

        boolean b = removeById(id);
        if (!b) {
            return R.fail("更新失败");
        }
        redisService.removeList("listArticleByFront:*");
        return R.success();
    }

    /**
     * 后台管理 根据id查找文章
     *
     * @param id
     * @return
     */

    @Override
    public R getArticleById(Integer id) {
        Article byId = getById(id);
        if (byId == null) {
            return R.fail("错误，文章不存在");
        }
        return R.success(byId);
    }

    /**
     * 更新文章
     *
     * @param article
     * @return
     */
    @Override
    @Transactional
    public R updateArticle(Article article ) {
        redisService.remove(CommonConst.ARTICLE_CACHE + "ById:" + article.getId());

        article.setUpdateBy(((User) SecurityUtils.getSubject().getPrincipals().getPrimaryPrincipal()).getUsername());
        boolean b = updateById(article);
        if (!b) {
            return R.fail("文章更新失败");
        }
        redisService.removeList("listArticleByFront:*");
        return R.success();
    }

    /**
     * 前台根据id查找文章
     *
     * @param id
     * @return
     */

    @Override
    public R getArticleByIdFront(Integer id) {
        String data = redisService.get(CommonConst.ARTICLE_CACHE + "ById:" + id);
        Article article1 = null;
        if ("\"\"".equals(data)) {
            return R.fail("文章不存在");
        }
        if (data == null) {
            String lockKey = "lock:" + "getArticleByIdFront:" + id;
            try {
                boolean lock = redisService.getLock(lockKey);
                System.out.println("getlock");
                if (!lock) {
                    Thread.sleep(50);
                    System.out.println("sleep");
                    return getArticleByIdFront(id);
                }

                article1 = baseMapper.selectById(id);
                if (article1 == null) {
                    redisService.set(CommonConst.ARTICLE_CACHE + "ById:" + id, "", 60);
                    return R.fail("文章不存在");
                } else {
                    redisService.set(CommonConst.ARTICLE_CACHE + "ById:" + id, article1, CommonConst.CACHE_EXPIRE);
                }

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                System.out.println("unlock");
                redisService.unlock(lockKey);
            }
        } else {
            ObjectMapper mapper = new ObjectMapper();
            try {
                article1 = mapper.readValue(data, Article.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            ArticleVo articleVO = buildArticleVO(article1, false);
            return R.success(articleVO);
        }
        ArticleVo articleVO = buildArticleVO(article1, false);

        return R.success(articleVO);


    }

    /**
     * 封装对象
     *
     * @param article
     * @return
     */
    private ArticleVo buildArticleVO(Article article, boolean isList) {
        ArticleVo articleVO = new ArticleVo();
        BeanUtils.copyProperties(article, articleVO);


        if (!StringUtils.hasText(articleVO.getArticleCover())) {
            // 没有封面就随机封面，还没做好
            articleVO.setArticleCover("http://rqldcqw23.hn-bkt.clouddn.com/articleCover/Sara11677397871718631.jpg");
        }
        /**
         * 查询文章喜欢和热度信息。
         */
        Map<String, String> articleLikeAndHeat = redisService.getArticleLikeAndHeat(article.getId());
        if (articleLikeAndHeat != null && !articleLikeAndHeat.isEmpty()) {
            Integer num = 0;
            String like = articleLikeAndHeat.get("like");

            if (StringUtils.hasText(like)) {
                num = Integer.parseInt(like);
                articleVO.setLikeCount(num + articleVO.getLikeCount());
            }
            num = 0;
            String heat = articleLikeAndHeat.get("heat");
            if (StringUtils.hasText(heat)) {
                num = Integer.parseInt(heat);
                articleVO.setViewCount(num + articleVO.getViewCount());
            }
        }

        articleVO.setSort(sortService.getById(articleVO.getSortId()));
        articleVO.setLabel(labelService.getById(articleVO.getLabelId()));
        articleVO.setUsername(userService.getById(articleVO.getUserId()).getUsername());
        QueryWrapper<Comment> commentQueryWrapper = new QueryWrapper<>();
        commentQueryWrapper.eq("type", CommentTypeEnum.COMMENT_TYPE_ARTICLE.getCode());
        commentQueryWrapper.eq("source", articleVO.getId());
        articleVO.setCommentCount(commentService.count(commentQueryWrapper));
        return articleVO;
    }

    private void articleVOSetCount(Map m) {
        /**
         * 查询文章喜欢和热度信息。
         */
        Map<String, String> articleLikeAndHeat = redisService.getArticleLikeAndHeat((Integer) m.get("id"));
        if (articleLikeAndHeat != null && !articleLikeAndHeat.isEmpty()) {
            Integer num = 0;
            String like = articleLikeAndHeat.get("like");

            if (StringUtils.hasText(like)) {
                num = Integer.parseInt(like);

                m.put(("likeCount"), num + (Integer) m.get("likeCount"));
            }
            num = 0;
            String heat = articleLikeAndHeat.get("heat");
            if (StringUtils.hasText(heat)) {
                num = Integer.parseInt(heat);
                m.put(("viewCount"), num + (Integer) m.get("viewCount"));

            }
        }
        QueryWrapper<Comment> commentQueryWrapper = new QueryWrapper<>();
        commentQueryWrapper.eq("type", CommentTypeEnum.COMMENT_TYPE_ARTICLE.getCode());
        commentQueryWrapper.eq("source", m.get("id"));
        commentService.count(commentQueryWrapper);
        m.put(("commentCount"), commentService.count(commentQueryWrapper));

    }

}

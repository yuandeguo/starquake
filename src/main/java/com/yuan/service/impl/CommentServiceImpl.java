package com.yuan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.mapper.CommentMapper;
import com.yuan.myEnum.CodeMsg;
import com.yuan.myEnum.CommentTypeEnum;
import com.yuan.myEnum.CommonConst;
import com.yuan.params.SearchCommentParam;
import com.yuan.pojo.Article;
import com.yuan.pojo.Comment;
import com.yuan.pojo.ResourcePath;
import com.yuan.pojo.User;
import com.yuan.service.ArticleService;
import com.yuan.service.CommentService;
import com.yuan.service.RedisService;
import com.yuan.service.UserService;
import com.yuan.utils.DataCacheUtil;
import com.yuan.utils.R;
import com.yuan.vo.CommentVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
 * @date 2023/3/1 10:28
 * @Description null
 */
@Slf4j
@Service
public class CommentServiceImpl  extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    @Resource
    ArticleService articleService;
    @Resource
    private UserService userService;

    @Resource
    private RedisService redisService;
    @Override
    public R searchCommentList(SearchCommentParam searchCommentParam, String authorization) {
        QueryWrapper<Comment> queryWrapper=new QueryWrapper<>();
        User user=(User)redisService.get(authorization,User.class);
        //如果不是boss，只能查看自己文章的评论
        if(user.getUserType()!=0)
        {
            QueryWrapper<Article> articleQueryWrapper=new QueryWrapper<>();
            articleQueryWrapper.select("id");
            articleQueryWrapper.eq("user_id",user.getId());
            List<Integer> integers =   articleService.listObjs(articleQueryWrapper).stream().map(i->(Integer)i).collect(Collectors.toList());
            IPage<Comment> page=new Page<>();
            if(integers.isEmpty())
                return R.success( page);
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
         return   R.success( page);
    }

    @Override
    public Integer getCommentCount(Integer source, String type) {
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
       wrapper.eq("source", source)
                .eq("type", type);
    return    baseMapper.selectCount(wrapper);
    }

    /**
     * 文章主页查询评论信息
     * @param searchCommentParam
     * @return
     */
    @Override
    public R listCommentVo(SearchCommentParam searchCommentParam) {
        if (searchCommentParam.getSource() == null || !StringUtils.hasText(searchCommentParam.getCommentType())) {
            return R.fail(CodeMsg.PARAMETER_ERROR);
        }

        if (CommentTypeEnum.COMMENT_TYPE_ARTICLE.getCode().equals(searchCommentParam.getCommentType())) {

            Article one = articleService.getById(searchCommentParam.getSource());
            if (one != null && !one.getCommentStatus()) {
                return R.fail("评论功能已关闭！");
            }
        }
        IPage<Comment> page=new Page<>(searchCommentParam.getCurrent(),searchCommentParam.getSize());
        QueryWrapper<Comment> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("source",searchCommentParam.getSource());
        queryWrapper.eq("type",searchCommentParam.getCommentType());

        if (searchCommentParam.getFloorCommentId() != null) { //查找子评论

            queryWrapper.eq("floor_comment_id",searchCommentParam.getFloorCommentId());
            queryWrapper.orderByAsc("create_time");
            IPage<Comment> iPageChildComments = baseMapper.selectPage(page, queryWrapper);




            List<Comment> childComments = iPageChildComments.getRecords();
            IPage<CommentVo> iPageChildCommentsVo=new Page<>();
            List<CommentVo> childCommentsVO = childComments.stream().map(cc -> buildCommentVO(cc)).collect(Collectors.toList());
            iPageChildCommentsVo.setRecords(childCommentsVO);
            iPageChildCommentsVo.setTotal(iPageChildComments.getTotal());



            return R.success(iPageChildCommentsVo);
        }
        else{
            /**
             * 第一条评论
             */
            queryWrapper.orderByDesc("create_time");
            queryWrapper.eq("parent_comment_id",0);
            IPage<Comment> iPage = baseMapper.selectPage(page, queryWrapper);
            List<Comment> comments = iPage.getRecords();
            if (CollectionUtils.isEmpty(comments)) {
                return R.success(null);
            }

            IPage<CommentVo> iPageCommentVo=new Page<>();
            List<CommentVo> ccVO = comments.stream().map(cc ->buildCommentVO(cc)).collect(Collectors.toList());

            for(CommentVo item :ccVO)
            {
                IPage<Comment> childPage=new Page<>(1,5);
                QueryWrapper<Comment> queryWrapperChild=new QueryWrapper<>();
                queryWrapperChild.eq("source",searchCommentParam.getSource());
                queryWrapperChild.eq("type",searchCommentParam.getCommentType());
                queryWrapperChild.eq("floor_comment_id",item.getId());
                queryWrapperChild.orderByAsc("create_time");
                IPage<Comment> iPageChildComments = baseMapper.selectPage(childPage, queryWrapperChild);

                List<Comment> childComments = iPageChildComments.getRecords();
                IPage<CommentVo> iPageChildCommentsVo=new Page<>();
                List<CommentVo> childCommentsVO = childComments.stream().map(cc -> buildCommentVO(cc)).collect(Collectors.toList());
                iPageChildCommentsVo.setRecords(childCommentsVO);
                iPageChildCommentsVo.setTotal(iPageChildComments.getTotal());
                item.setChildComments(iPageChildCommentsVo);
            }
            iPageCommentVo.setRecords(ccVO);
            iPageCommentVo.setTotal(iPage.getTotal());
            return R.success(iPageCommentVo);
        }

    }

    @Override
    public R deleteComment(Integer id) {
        boolean b = removeById(id);
        if(!b) {
            return R.fail("评论删除失败");
        }
        return R.success();
    }

    @Override
    public R saveComment(Comment comment, String authorization) {
        User user=   (User)redisService.get(authorization,User.class);
        if(user==null)
        {
            return R.fail("评论保存失败,请重新登录");
        }
        comment.setUserId(user.getId());
        comment.setCreateTime(LocalDateTime.now());
        boolean save = save(comment);
        if(!save) {
            return R.fail("评论保存失败");
        }
        return R.success();
    }

    private CommentVo buildCommentVO(Comment c) {
        CommentVo commentVO = new CommentVo();
        BeanUtils.copyProperties(c, commentVO);
        User user = userService.getById(commentVO.getUserId());
        if (user != null) {
            commentVO.setAvatar(user.getAvatar());
            commentVO.setUsername(user.getUsername());
        }
        if (commentVO.getParentUserId() != null) {
            User u = userService.getById(commentVO.getParentUserId());
            if (u != null) {
                commentVO.setParentUsername(u.getUsername());
            }
        }
        return commentVO;
    }

}

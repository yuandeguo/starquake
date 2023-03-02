package com.yuan.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yuan.annotations.LoginCheck;
import com.yuan.myEnum.CommonConst;
import com.yuan.params.SearchCommentParam;
import com.yuan.pojo.Comment;
import com.yuan.pojo.User;
import com.yuan.service.CommentService;
import com.yuan.utils.DataCacheUtil;
import com.yuan.utils.R;
import com.yuan.vo.CommentVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/3/1 15:36
 * @Description null
 */
@Slf4j
@RestController
@RequestMapping("/comment")
public class CommentController {
    @Resource
    private CommentService commentService;

    @PostMapping("/admin/commentList")
    public R commentList(@RequestBody SearchCommentParam searchCommentParam,@RequestHeader("Authorization") String authorization)
    {
        IPage<Comment> list= commentService.searchCommentList(searchCommentParam,authorization);
        return R.success(list);
    }
    @GetMapping("/admin/deleteComment")
    public R deleteComment(@RequestParam("id") Integer id)
    {
        boolean b = commentService.removeById(id);
        if(!b) {
            return R.fail("评论删除失败");
        }
        return R.success();
    }

    /**
     * 查询评论数量
     */
    @GetMapping("/getCommentCount")
    public R getCommentCount(@RequestParam("source") Integer source, @RequestParam("type") String type) {
      Integer count=  commentService.getCommentCount(source,type);
        return R.success(count);
    }


    /**
     * 文章主页查询评论
     */
    @PostMapping("/listComment")
    public R listComment(@RequestBody SearchCommentParam SearchCommentParam) {
        R iPage= commentService.listCommentVo(SearchCommentParam);
        return iPage;

    }
/**
 * 保存评论
 */
    @PostMapping("/saveComment")
    public R saveComment(@Validated @RequestBody Comment comment,@RequestHeader("Authorization") String authorization) {
     User user=   (User)DataCacheUtil.get(authorization);
        if(user==null)
        {
            return R.fail("评论保存失败,请重新登录");
        }
        comment.setUserId(user.getId());
        comment.setCreateTime(LocalDateTime.now());
        boolean save = commentService.save(comment);
        if(!save) {
            return R.fail("评论保存失败");
        }
        return R.success();
    }




}

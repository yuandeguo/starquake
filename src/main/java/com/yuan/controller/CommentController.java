package com.yuan.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yuan.params.SearchCommentParam;
import com.yuan.pojo.Comment;
import com.yuan.service.CommentService;
import com.yuan.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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

}

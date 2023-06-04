package com.yuan.controller;

import com.yuan.annotations.OperationLogAnnotation;
import com.yuan.params.SearchCommentParam;
import com.yuan.pojo.Comment;
import com.yuan.service.CommentService;
import com.yuan.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

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
    public R commentList(@RequestBody SearchCommentParam searchCommentParam, @RequestHeader("Authorization") String authorization) {
        return commentService.searchCommentList(searchCommentParam, authorization);

    }

    @OperationLogAnnotation(operModul = "deleteComment", operType = "删除", operDesc = "deleteComment")
    @GetMapping("/admin/deleteComment")
    public R deleteComment(@RequestParam("id") Integer id) {
        return commentService.deleteComment(id);

    }

    /**
     * 查询评论数量
     */
    @GetMapping("/getCommentCount")
    public R getCommentCount(@RequestParam("source") Integer source, @RequestParam("type") String type) {
        Integer count = commentService.getCommentCount(source, type);
        return R.success(count);
    }


    /**
     * 文章主页查询评论
     */
    @PostMapping("/listComment")
    public R listComment(@RequestBody SearchCommentParam SearchCommentParam) {
        return commentService.listCommentVo(SearchCommentParam);


    }

    /**
     * 保存评论
     */
    @OperationLogAnnotation(operModul = "saveComment", operType = "保存", operDesc = "saveComment")
    @PostMapping("/saveComment")
    public R saveComment(@Validated @RequestBody Comment comment, @RequestHeader("Authorization") String authorization) {

        return commentService.saveComment(comment, authorization);

    }


}

package com.yuan.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yuan.params.SearchCommentParam;
import com.yuan.pojo.Comment;
import com.yuan.utils.R;
import com.yuan.vo.CommentVo;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/3/1 10:27
 * @Description null
 */
public interface CommentService extends IService<Comment> {
    IPage<Comment> searchCommentList(SearchCommentParam searchCommentParam, String authorization);

    Integer getCommentCount(Integer source, String type);

    R listCommentVo(SearchCommentParam searchCommentParam);
}

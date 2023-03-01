package com.yuan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.mapper.CommentMapper;
import com.yuan.mapper.LabelMapper;
import com.yuan.pojo.Comment;
import com.yuan.pojo.Label;
import com.yuan.service.CommentService;
import com.yuan.service.LabelService;
import org.springframework.stereotype.Service;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/3/1 10:28
 * @Description null
 */
@Service
public class CommentServiceImpl  extends ServiceImpl<CommentMapper, Comment> implements CommentService {
}

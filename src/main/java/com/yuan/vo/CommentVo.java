package com.yuan.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/3/2 12:49
 * @Description null
 */
@Data
public class CommentVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    @NotNull(message = "评论来源标识不能为空")
    private Integer source;
    /**
     * 评论来源类型
     */
    @NotBlank(message = "评论来源类型不能为空")
    private String type;

    //层主的parentCommentId是0，回复的parentCommentId是层主的id
    private Integer parentCommentId;

    //层主的parentUserId是null，回复的parentUserId是被回复的userId
    private Integer parentUserId;

    private Integer userId;

    private Integer likeCount;

    @NotBlank(message = "评论内容不能为空")
    private String commentContent;

    private String commentInfo;

    //子评论必须传评论楼层ID
    private Integer floorCommentId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    // 需要查询封装
    private IPage<CommentVo> childComments;
    private String parentUsername;
    private String username;
    private String avatar;



}

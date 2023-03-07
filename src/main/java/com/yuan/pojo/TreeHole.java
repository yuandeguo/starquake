package com.yuan.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/3/6 16:25
 * @Description null
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tree_hole")
public class TreeHole implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 头像
     */
    @TableField("avatar")
    private String avatar;

    /**
     * 留言
     */
    @TableField("message")
    private String message;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 是否启用[0:未删除，1:已删除]
     */
    @TableField("deleted")
    @TableLogic
    private Boolean deleted;
}

package com.yuan.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/3/7 0:38
 * @Description null
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("wei_yan")
public class WeiYan implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Integer userId;

    /**
     * 点赞数
     */
    @TableField("like_count")
    private Integer likeCount;

    /**
     * 内容
     */
    @TableField("content")
    private String content;

    /**
     * 是否公开[0:仅自己可见，1:所有人可见]
     */
    @TableField("is_public")
    private Boolean isPublic;

    /**
     * 类型
     */
    @TableField("type")
    private String type;

    /**
     * 来源标识  表示文章id
     */
    @TableField("source")
    private Integer source;

    /**
     * 创建时间
     */
    @TableField("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createTime;

    /**
     * 是否启用[0:未删除，1:已删除]
     */
    @TableField("deleted")
    @TableLogic
    private Boolean deleted;


}


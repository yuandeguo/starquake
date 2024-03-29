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
 * @date 2023/3/1 21:12
 * @Description 友情链接资源，其他未拓展
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("resource_path")
public class ResourcePath implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 标题
     */
    @TableField("title")
    private String title;

    /**
     * 资源类别
     */
    @TableField("classify")
    private String classify;

    /**
     * 封面
     */
    @TableField("cover")
    private String cover;

    /**
     * 链接
     */
    @TableField("url")
    private String url;

    /**
     * 资源类型
     */
    @TableField("type")
    private String type;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 是否启用[0:否，1:是]
     */
    @TableField("status")
    private Boolean status;

    /**
     * 简介
     */
    @TableField("introduction")
    private String introduction;

    /**
     * 创建时间
     */
    @TableField("create_time")
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
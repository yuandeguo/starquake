package com.yuan.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/2/28 17:14
 * @Description null
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("resource")
public class Resource implements Serializable {

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
     * 资源类型
     */
    @TableField("type")
    private String type;

    /**
     * 是否启用[0:否，1:是]
     */
    @TableField("status")
    private Boolean status;

    /**
     * 资源路径
     */
    @TableField("path")
    private String path;

    /**
     * 资源内容的大小，单位：字节
     */
    @TableField("size")
    private Integer size;

    /**
     * 资源的 MIME 类型
     */
    @TableField("mime_type")
    private String mimeType;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

}


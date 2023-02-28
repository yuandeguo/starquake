package com.yuan.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/2/27 12:50
 * @Description 标签
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("label")
public class Label implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 分类ID
     */
    @TableField("sort_id")
    private Integer sortId;

    /**
     * 标签名称
     */
    @TableField("label_name")
    private String labelName;

    /**
     * 标签描述
     */
    @TableField("label_description")
    private String labelDescription;

    /**
     * 标签数量
     */
    @TableField(exist = false)
    private Integer countOfLabel;
}

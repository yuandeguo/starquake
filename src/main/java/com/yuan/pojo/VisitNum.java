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
 * @date 2023/3/8 23:29
 * @Description null
 */

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("visit_num")
public class VisitNum implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 创建时间
     */
    @TableField("write_time")
    private LocalDateTime writeTime;

    /**
     * num
     */
    @TableField("num")
    private Integer num;



    /**
     *
     */
    @TableField("type")
    private Integer type;
}

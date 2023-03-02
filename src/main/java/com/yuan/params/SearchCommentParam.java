package com.yuan.params;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/3/1 15:39
 * @Description null
 */
@Data
public class SearchCommentParam extends PageParam implements Serializable {
    public static final Long serialVersionUID = 1L;
    private String commentType;
    @NotNull(message = "评论来源标识不能为空")
    private Integer source;
    private Integer floorCommentId;
}

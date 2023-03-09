package com.yuan.params;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/3/8 16:55
 * @Description null
 */
@Data
@AllArgsConstructor
public class ArticleLikeAndViewCurrentParam {
    private Integer articleId;
   private Integer like=0;
    private Integer view=0;

}

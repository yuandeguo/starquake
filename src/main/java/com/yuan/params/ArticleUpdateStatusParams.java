package com.yuan.params;


import lombok.Data;

import java.io.Serializable;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/3/1 0:31
 * @Description null
 */
@Data
public class ArticleUpdateStatusParams implements Serializable {
   public static final Long serialVersionUID = 1L;
  private Integer  articleId;
  private Boolean commentStatus;
  private Boolean recommendStatus;
  private Boolean viewStatus;

}

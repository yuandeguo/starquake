package com.yuan.params;


import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/2/28 20:45
 * @Description null
 */
@Data
@ToString
public class SearchUserParam extends PageParam implements Serializable {
   public static final Long serialVersionUID = 1L;
  private String  searchKey;
    private  Boolean userStatus;
    private Integer userType;
}

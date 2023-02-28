package com.yuan.params;

import lombok.Data;

import java.io.Serializable;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/2/28 21:31
 * @Description null
 */
@Data
public class UserStatusOrTypeParam implements Serializable {
     public static final Long serialVersionUID = 1L;
     private Integer userId;
     private Boolean userStatus;
     private Integer userType;

}

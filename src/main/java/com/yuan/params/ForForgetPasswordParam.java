package com.yuan.params;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/3/2 17:09
 * @Description null
 */
@Data
public class ForForgetPasswordParam {
    @NotNull
   private Integer code;
    @NotBlank
  private String  password;
}

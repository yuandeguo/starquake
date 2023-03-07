package com.yuan.params;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/3/2 17:09
 * @Description null
 */
@Data
public class ForForgetPasswordParam implements Serializable {
     public static final Long serialVersionUID = 1L;
    @NotBlank
   private String code;
    @NotBlank
     private String  password;
    @NotBlank
    private  String place;
    @NotNull
    private Integer flag;


}

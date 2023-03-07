package com.yuan.params;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/3/6 0:32
 * @Description null
 */
@Data
public class UserUpdateSecretInfoParam implements Serializable {
    public static final Long serialVersionUID = 1L;
    @NotBlank
    String place;
    @NotNull
    Integer flag;
    String code;
    @NotBlank
    String password;
}

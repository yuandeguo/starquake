package com.yuan.params;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/3/6 15:43
 * @Description null
 */
@Data
public class UserRegisterParam implements Serializable {
    public static final Long serialVersionUID = 1L;

    @NotBlank(message = "用户名不能为空")
    private String username;
    @NotBlank
    private String email;
    @NotBlank(message = "密码不能为空")
    private String password;
    @NotBlank(message = "验证码不能为空")
    private String code;

}

package com.yuan.params;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/2/28 15:48
 * @Description null
 */
@Data
public class LoginParam {
    @NotBlank(message = "用户名不能为空")
    private String username;
    @NotBlank(message = "密码不能为空")
    private String password;
    private Boolean isAdmin=false;
}

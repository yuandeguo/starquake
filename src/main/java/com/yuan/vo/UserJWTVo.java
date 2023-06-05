package com.yuan.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * @author yuanyuan
 * @version V1.0
 * @date 2023/6/5 23:14
 * @Description null
 */
@Data
@AllArgsConstructor
@ToString
public class UserJWTVo {
    private Integer id;
    private String username;
    private String phoneNumber;
    private String email;
    private String password;
    private Integer gender;
    private String avatar;
    private String introduction;
    private Boolean isBoss = false;
}

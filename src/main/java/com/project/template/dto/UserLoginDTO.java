package com.project.template.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDTO implements Serializable {

    @Length(min = 2,max = 15,message = "用户名长度需要在[2和15]之间")
    @ApiModelProperty("用户名")
    private String username;
    @Length(min = 2,max = 15,message = "密码需要在[2和15]之间")
    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("验证码")
    private String captcha;


}

package com.project.template.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class News extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("标题")
    private String name;

    @ApiModelProperty("封面图")
    private String img;

    @ApiModelProperty("内容")
    private String content;

    @ApiModelProperty("浏览量")
    private Integer views;

    @ApiModelProperty("发布时间")
    private Date createTime;

    @ApiModelProperty("发布用户")
    private String user;
}

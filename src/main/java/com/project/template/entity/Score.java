package com.project.template.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class Score extends BaseEntity{

    //    @NotBlank
    private Integer userId;
    private Integer teacherId;
    private Integer examId;

    private Integer score;

    private String userAnswers;
    private String name;
    private String ids;
    private String isScore;
    @TableField(exist = false)
    private String user;
}

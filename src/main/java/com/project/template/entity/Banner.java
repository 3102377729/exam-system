package com.project.template.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Banner extends BaseEntity{

    //    @NotBlank
    private String name;

    private String img;

    private String link;

}
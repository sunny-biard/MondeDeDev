package com.openclassrooms.mddapi.model.dto;

import java.sql.Date;

import lombok.Data;

@Data
public class CommentDto {

    private Integer id;
    private String content;
    private Date createdAt;
    private UserDto user;
}
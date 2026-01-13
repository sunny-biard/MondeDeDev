package com.openclassrooms.mddapi.model.dto;

import java.sql.Date;

import com.openclassrooms.mddapi.model.entity.Topic;

import lombok.Data;

@Data
public class PostDto {

    private String title;
    private String content;
    private Date createdAt;
    private Date updatedAt;
    private Topic topic;
    private UserDto user;
}

package com.openclassrooms.mddapi.model.request;

import com.openclassrooms.mddapi.model.entity.Topic;

import lombok.Data;

@Data
public class PostUpdateRequest {

    private String title;
    private String content;
    private Topic topic;
}

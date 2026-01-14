package com.openclassrooms.mddapi.model.request;

import com.openclassrooms.mddapi.model.entity.Topic;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PostCreateRequest {

    @NotNull(message = "Title is mandatory")
    private String title;

    @NotNull(message = "Content is mandatory")
    private String content;

    @NotNull(message = "Topic is mandatory")
    private Topic topic;
}
package com.openclassrooms.mddapi.model.request;

import com.openclassrooms.mddapi.model.entity.Topic;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PostCreateRequest {

    @NotBlank(message = "Title is mandatory")
    private String title;

    @NotBlank(message = "Content is mandatory")
    private String content;

    @NotBlank(message = "Topic is mandatory")
    private Topic topic;
}
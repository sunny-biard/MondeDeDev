package com.openclassrooms.mddapi.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentCreateRequest {

    @NotBlank(message = "Content is mandatory")
    private String content;

    @NotNull(message = "Post ID is mandatory")
    private Integer postId;
}
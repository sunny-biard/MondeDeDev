package com.openclassrooms.mddapi.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginRequest {

    @NotNull(message = "Identifier is required")
    private String identifier;

    @NotNull(message = "Password is required")
    private String password;
}
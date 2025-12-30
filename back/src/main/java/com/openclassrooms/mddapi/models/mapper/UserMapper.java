package com.openclassrooms.mddapi.model.mapper;

public class UserMapper {
    public static UserModel toEntity(RegisterRequest req, PasswordEncoder passwordEncoder) {
        UserModel user = new UserModel();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        return user;
    }

    public static UserResponse toDto(UserModel user) {
        UserResponse userDto = new UserResponse();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setUsername(user.getUsername());
        return userDto;
    }
}
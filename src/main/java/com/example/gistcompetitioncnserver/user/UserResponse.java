package com.example.gistcompetitioncnserver.user;

import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class UserResponse {
    private final String username;
    private final UserRole userRole;

    public static UserResponse of(User user) {
        return new UserResponse(
                user.getUsername(),
                user.getUserRole()
        );
    }

    public static List<UserResponse> listOf(List<User> users) {
        return users.stream()
                .map(UserResponse::of)
                .collect(Collectors.toList());
    }
}

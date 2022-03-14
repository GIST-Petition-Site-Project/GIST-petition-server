package com.gistpetition.api.user.dto.response;

import com.gistpetition.api.user.domain.User;
import com.gistpetition.api.user.domain.UserRole;
import lombok.Data;
import org.springframework.data.domain.Page;

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

    public static Page<UserResponse> pageOf(Page<User> users) {
        return users.map(UserResponse::of);
    }
}

package com.queenstouch.queenstouchbackend.dto.response;

import com.queenstouch.queenstouchbackend.model.enums.Role;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class UserResponse {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Role role;
    private boolean emailVerified;
    private Instant createdAt;
}

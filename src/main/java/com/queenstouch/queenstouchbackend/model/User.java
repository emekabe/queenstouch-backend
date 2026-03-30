package com.queenstouch.queenstouchbackend.model;

import com.queenstouch.queenstouchbackend.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private String id;

    private String name;

    @Indexed(unique = true)
    private String email;

    private String phone;

    private String passwordHash;

    @Builder.Default
    private Role role = Role.USER;

    @Builder.Default
    private boolean emailVerified = false;

    private String emailVerificationOtp;
    private Instant emailVerificationOtpExpiry;

    private String passwordResetOtp;
    private Instant passwordResetOtpExpiry;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}

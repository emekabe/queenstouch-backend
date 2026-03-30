package com.queenstouch.queenstouchbackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AchievementBuilderRequest {

    @NotBlank(message = "Role/job title is required")
    private String role;

    @NotBlank(message = "Task or responsibility description is required")
    private String task;

    @NotBlank(message = "Result or impact is required")
    private String result;
}

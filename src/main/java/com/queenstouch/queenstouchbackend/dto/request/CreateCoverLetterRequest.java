package com.queenstouch.queenstouchbackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class CreateCoverLetterRequest {

    @NotBlank(message = "Job title is required")
    private String jobTitle;

    @NotBlank(message = "Company name is required")
    private String companyName;

    private List<String> keySkills;

    @NotBlank(message = "Relevant experience is required")
    private String relevantExperience;
}

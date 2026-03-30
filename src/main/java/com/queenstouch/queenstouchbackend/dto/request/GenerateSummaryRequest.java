package com.queenstouch.queenstouchbackend.dto.request;

import lombok.Data;

@Data
public class GenerateSummaryRequest {
    private String jobTitle;
    private String yearsOfExperience;
    private String skills;
    private String achievements;
}

package com.queenstouch.queenstouchbackend.dto.request;

import lombok.Data;

import java.util.List;

/**
 * Generic patch DTO – all fields optional. Only non-null fields will be applied.
 */
@Data
public class UpdateCvRequest {

    // Personal Info
    private String title;
    private String fullName;
    private String email;
    private String phone;
    private String location;
    private String linkedinUrl;
    private String portfolioUrl;
    private String summary;

    // Toggle flags
    private Boolean scholarshipMode;

    // Structured sections (replaces entire list when present)
    private List<WorkExperienceDto> workExperiences;
    private List<EducationDto> educations;
    private List<SkillDto> skills;
    private List<CertificationDto> certifications;
    private List<String> languages;

    // Academic-only
    private List<String> researchInterests;
    private List<String> publications;
    private List<String> conferences;
    private List<String> teachingExperience;
    private List<String> awards;

    @Data
    public static class WorkExperienceDto {
        private String jobTitle;
        private String company;
        private String location;
        private String startDate;
        private String endDate;
        private boolean current;
        private List<String> bullets;
    }

    @Data
    public static class EducationDto {
        private String institution;
        private String degree;
        private String fieldOfStudy;
        private String grade;
        private String startDate;
        private String endDate;
    }

    @Data
    public static class SkillDto {
        private String name;
        private String level;
    }

    @Data
    public static class CertificationDto {
        private String name;
        private String issuer;
        private String year;
    }
}

package com.queenstouch.queenstouchbackend.model;

import com.queenstouch.queenstouchbackend.model.enums.CvStatus;
import com.queenstouch.queenstouchbackend.model.enums.CvType;
import com.queenstouch.queenstouchbackend.model.enums.SkillLevel;
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
import java.util.ArrayList;
import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "cv_documents")
public class CvDocument {

    @Id
    private String id;

    @Indexed
    private String userId;

    private String title;

    @Builder.Default
    private CvType cvType = CvType.STANDARD;

    @Builder.Default
    private CvStatus status = CvStatus.DRAFT;

    /** True when Academic CV is for a scholarship / study abroad application */
    @Builder.Default
    private boolean scholarshipMode = false;

    // ── Personal Info ────────────────────────────────────────────────────────
    private String fullName;
    private String email;
    private String phone;
    private String location;
    private String linkedinUrl;
    private String portfolioUrl;

    // ── Professional Summary ─────────────────────────────────────────────────
    private String summary;

    // ── Work Experience ──────────────────────────────────────────────────────
    @Builder.Default
    private List<WorkExperience> workExperiences = new ArrayList<>();

    // ── Education ────────────────────────────────────────────────────────────
    @Builder.Default
    private List<Education> educations = new ArrayList<>();

    // ── Skills ───────────────────────────────────────────────────────────────
    @Builder.Default
    private List<Skill> skills = new ArrayList<>();

    // ── Certifications ───────────────────────────────────────────────────────
    @Builder.Default
    private List<Certification> certifications = new ArrayList<>();

    // ── Languages ────────────────────────────────────────────────────────────
    @Builder.Default
    private List<String> languages = new ArrayList<>();

    // ── Academic-Only Sections (null/empty for STANDARD) ─────────────────────
    @Builder.Default
    private List<String> researchInterests = new ArrayList<>();

    @Builder.Default
    private List<String> publications = new ArrayList<>();

    @Builder.Default
    private List<String> conferences = new ArrayList<>();

    @Builder.Default
    private List<String> teachingExperience = new ArrayList<>();

    @Builder.Default
    private List<String> awards = new ArrayList<>();

    // ── AI-Generated Analysis ────────────────────────────────────────────────
    private CvScore cvScore;
    private JobMatchResult lastJobMatchResult;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    // ── Embedded Documents ───────────────────────────────────────────────────

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WorkExperience {
        private String jobTitle;
        private String company;
        private String location;
        private String startDate;   // "YYYY-MM" or "Month YYYY"
        private String endDate;
        @Builder.Default
        private boolean current = false;
        @Builder.Default
        private List<String> bullets = new ArrayList<>();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Education {
        private String institution;
        private String degree;
        private String fieldOfStudy;
        private String grade;
        private String startDate;
        private String endDate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Skill {
        private String name;
        @Builder.Default
        private SkillLevel level = SkillLevel.INTERMEDIATE;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Certification {
        private String name;
        private String issuer;
        private String year;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CvScore {
        private int overall;              // out of 100
        private int structure;
        private int keywordStrength;
        private int atsCompatibility;
        @Builder.Default
        private List<String> strengths = new ArrayList<>();
        @Builder.Default
        private List<String> improvements = new ArrayList<>();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JobMatchResult {
        private String jdSnippetHash;
        private int matchPercent;
        @Builder.Default
        private List<String> missingKeywords = new ArrayList<>();
        @Builder.Default
        private List<String> suggestions = new ArrayList<>();
    }
}

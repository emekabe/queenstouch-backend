package com.queenstouch.queenstouchbackend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.queenstouch.queenstouchbackend.dto.request.*;
import com.queenstouch.queenstouchbackend.exception.AppException;
import com.queenstouch.queenstouchbackend.model.CvDocument;
import com.queenstouch.queenstouchbackend.model.CvDocument.*;
import com.queenstouch.queenstouchbackend.model.enums.CvStatus;
import com.queenstouch.queenstouchbackend.model.enums.CvType;
import com.queenstouch.queenstouchbackend.model.enums.SkillLevel;
import com.queenstouch.queenstouchbackend.repository.CvRepository;
import com.queenstouch.queenstouchbackend.service.ai.AiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CvService {

    private final CvRepository cvRepository;
    private final UserService userService;
    private final AiService aiService;
    private final ObjectMapper objectMapper;

    // ── CRUD ─────────────────────────────────────────────────────────────────

    public CvDocument create(String userEmail, CreateCvRequest request) {
        var user = userService.findByEmail(userEmail);
        CvDocument cv = CvDocument.builder()
                .userId(user.getId())
                .title(request.getTitle() != null ? request.getTitle() : "My CV")
                .cvType(request.getCvType() != null ? request.getCvType() : CvType.STANDARD)
                .scholarshipMode(request.isScholarshipMode())
                .build();
        return cvRepository.save(cv);
    }

    public List<CvDocument> listForUser(String userEmail) {
        var user = userService.findByEmail(userEmail);
        return cvRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    public CvDocument getForUser(String userEmail, String cvId) {
        var user = userService.findByEmail(userEmail);
        return cvRepository.findByIdAndUserId(cvId, user.getId())
                .orElseThrow(() -> AppException.notFound("CV not found"));
    }

    public CvDocument update(String userEmail, String cvId, UpdateCvRequest req) {
        CvDocument cv = getForUser(userEmail, cvId);
        applyPatch(cv, req);
        autoSetStatus(cv);
        return cvRepository.save(cv);
    }

    public void delete(String userEmail, String cvId) {
        CvDocument cv = getForUser(userEmail, cvId);
        cvRepository.delete(cv);
    }

    // ── AI Features ──────────────────────────────────────────────────────────

    public String generateSummary(String userEmail, String cvId, GenerateSummaryRequest request) {
        CvDocument cv = getForUser(userEmail, cvId);
        String summary = aiService.generateProfessionalSummary(
                cv.getFullName() != null ? cv.getFullName() : "Professional",
                request.getJobTitle(),
                request.getYearsOfExperience(),
                request.getSkills(),
                request.getAchievements()
        );
        cv.setSummary(summary);
        cvRepository.save(cv);
        return summary;
    }

    public String generateAchievementBullet(AchievementBuilderRequest request) {
        return aiService.generateAchievementBullet(request.getRole(), request.getTask(), request.getResult());
    }

    public CvScore scoreCv(String userEmail, String cvId) {
        CvDocument cv = getForUser(userEmail, cvId);
        String cvText = buildCvText(cv);
        String json = aiService.scoreCv(cvText);
        CvScore score = parseJson(json, CvScore.class);
        cv.setCvScore(score);
        cvRepository.save(cv);
        return score;
    }

    public JobMatchResult matchJobDescription(String userEmail, String cvId, JobMatchRequest request) {
        CvDocument cv = getForUser(userEmail, cvId);
        String cvText = buildCvText(cv);
        String json = aiService.matchJobDescription(cvText, request.getJobDescription());
        JobMatchResult result = parseJson(json, JobMatchResult.class);

        // Persist result + hash of JD
        result.setJdSnippetHash(String.valueOf(request.getJobDescription().hashCode()));
        cv.setLastJobMatchResult(result);
        cvRepository.save(cv);
        return result;
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void applyPatch(CvDocument cv, UpdateCvRequest req) {
        if (req.getTitle() != null)        cv.setTitle(req.getTitle());
        if (req.getFullName() != null)     cv.setFullName(req.getFullName());
        if (req.getEmail() != null)        cv.setEmail(req.getEmail());
        if (req.getPhone() != null)        cv.setPhone(req.getPhone());
        if (req.getLocation() != null)     cv.setLocation(req.getLocation());
        if (req.getLinkedinUrl() != null)  cv.setLinkedinUrl(req.getLinkedinUrl());
        if (req.getPortfolioUrl() != null) cv.setPortfolioUrl(req.getPortfolioUrl());
        if (req.getSummary() != null)      cv.setSummary(req.getSummary());
        if (req.getScholarshipMode() != null) cv.setScholarshipMode(req.getScholarshipMode());

        if (req.getWorkExperiences() != null) {
            cv.setWorkExperiences(req.getWorkExperiences().stream().map(w -> WorkExperience.builder()
                    .jobTitle(w.getJobTitle()).company(w.getCompany()).location(w.getLocation())
                    .startDate(w.getStartDate()).endDate(w.getEndDate()).current(w.isCurrent())
                    .bullets(w.getBullets() != null ? w.getBullets() : List.of())
                    .build()).collect(Collectors.toList()));
        }

        if (req.getEducations() != null) {
            cv.setEducations(req.getEducations().stream().map(e -> Education.builder()
                    .institution(e.getInstitution()).degree(e.getDegree())
                    .fieldOfStudy(e.getFieldOfStudy()).grade(e.getGrade())
                    .startDate(e.getStartDate()).endDate(e.getEndDate())
                    .build()).collect(Collectors.toList()));
        }

        if (req.getSkills() != null) {
            cv.setSkills(req.getSkills().stream().map(s -> Skill.builder()
                    .name(s.getName())
                    .level(s.getLevel() != null ? SkillLevel.valueOf(s.getLevel().toUpperCase()) : SkillLevel.INTERMEDIATE)
                    .build()).collect(Collectors.toList()));
        }

        if (req.getCertifications() != null) {
            cv.setCertifications(req.getCertifications().stream().map(c -> Certification.builder()
                    .name(c.getName()).issuer(c.getIssuer()).year(c.getYear())
                    .build()).collect(Collectors.toList()));
        }

        if (req.getLanguages() != null)          cv.setLanguages(req.getLanguages());
        if (req.getResearchInterests() != null)  cv.setResearchInterests(req.getResearchInterests());
        if (req.getPublications() != null)        cv.setPublications(req.getPublications());
        if (req.getConferences() != null)         cv.setConferences(req.getConferences());
        if (req.getTeachingExperience() != null)  cv.setTeachingExperience(req.getTeachingExperience());
        if (req.getAwards() != null)              cv.setAwards(req.getAwards());
    }

    private void autoSetStatus(CvDocument cv) {
        // Mark complete when key sections are filled
        if (cv.getFullName() != null && cv.getSummary() != null && !cv.getWorkExperiences().isEmpty()) {
            cv.setStatus(CvStatus.COMPLETE);
        }
    }

    private String buildCvText(CvDocument cv) {
        StringBuilder sb = new StringBuilder();
        sb.append("Name: ").append(cv.getFullName()).append("\n");
        sb.append("Summary: ").append(cv.getSummary()).append("\n\n");
        sb.append("Work Experience:\n");
        cv.getWorkExperiences().forEach(w -> {
            sb.append("- ").append(w.getJobTitle()).append(" at ").append(w.getCompany())
              .append(" (").append(w.getStartDate()).append(" - ")
              .append(w.isCurrent() ? "Present" : w.getEndDate()).append(")\n");
            w.getBullets().forEach(b -> sb.append("  * ").append(b).append("\n"));
        });
        sb.append("\nEducation:\n");
        cv.getEducations().forEach(e -> sb.append("- ").append(e.getDegree())
                .append(" in ").append(e.getFieldOfStudy())
                .append(", ").append(e.getInstitution()).append("\n"));
        sb.append("\nSkills: ");
        sb.append(cv.getSkills().stream().map(Skill::getName).collect(Collectors.joining(", ")));
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private <T> T parseJson(String json, Class<T> type) {
        try {
            // Strip markdown code block if present
            String cleaned = json.replaceAll("```json\\s*", "").replaceAll("```\\s*", "").trim();
            return objectMapper.readValue(cleaned, type);
        } catch (Exception e) {
            log.error("Failed to parse AI response JSON: {}", e.getMessage());
            throw AppException.internalError("AI response could not be parsed. Please try again.");
        }
    }
}

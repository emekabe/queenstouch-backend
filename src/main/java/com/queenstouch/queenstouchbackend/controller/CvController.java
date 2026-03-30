package com.queenstouch.queenstouchbackend.controller;

import com.queenstouch.queenstouchbackend.dto.request.*;
import com.queenstouch.queenstouchbackend.dto.response.ApiResponse;
import com.queenstouch.queenstouchbackend.model.CvDocument;
import com.queenstouch.queenstouchbackend.model.CvDocument.CvScore;
import com.queenstouch.queenstouchbackend.model.CvDocument.JobMatchResult;
import com.queenstouch.queenstouchbackend.service.CvService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/cvs")
@RequiredArgsConstructor
@Tag(name = "CV Builder", description = "Create, manage, and AI-enhance CVs")
@SecurityRequirement(name = "bearerAuth")
public class CvController {

    private final CvService cvService;

    // ── CRUD ─────────────────────────────────────────────────────────────────

    @PostMapping
    @Operation(summary = "Create a new CV draft")
    public ResponseEntity<ApiResponse<CvDocument>> create(
            @AuthenticationPrincipal String email,
            @RequestBody CreateCvRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("CV created", cvService.create(email, request)));
    }

    @GetMapping
    @Operation(summary = "List all CVs for the current user")
    public ResponseEntity<ApiResponse<List<CvDocument>>> list(@AuthenticationPrincipal String email) {
        return ResponseEntity.ok(ApiResponse.success(cvService.listForUser(email)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a specific CV")
    public ResponseEntity<ApiResponse<CvDocument>> get(
            @AuthenticationPrincipal String email,
            @PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(cvService.getForUser(email, id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update / patch any CV section")
    public ResponseEntity<ApiResponse<CvDocument>> update(
            @AuthenticationPrincipal String email,
            @PathVariable String id,
            @RequestBody UpdateCvRequest request) {
        return ResponseEntity.ok(ApiResponse.success("CV updated", cvService.update(email, id, request)));
    }

    // Section-specific PATCH endpoints (all delegate to the same UpdateCvRequest)
    @PatchMapping("/{id}/personal-info")
    @Operation(summary = "Update personal info section")
    public ResponseEntity<ApiResponse<CvDocument>> patchPersonalInfo(
            @AuthenticationPrincipal String email, @PathVariable String id,
            @RequestBody UpdateCvRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Personal info updated", cvService.update(email, id, request)));
    }

    @PatchMapping("/{id}/experience")
    @Operation(summary = "Update work experience section")
    public ResponseEntity<ApiResponse<CvDocument>> patchExperience(
            @AuthenticationPrincipal String email, @PathVariable String id,
            @RequestBody UpdateCvRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Experience updated", cvService.update(email, id, request)));
    }

    @PatchMapping("/{id}/education")
    @Operation(summary = "Update education section")
    public ResponseEntity<ApiResponse<CvDocument>> patchEducation(
            @AuthenticationPrincipal String email, @PathVariable String id,
            @RequestBody UpdateCvRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Education updated", cvService.update(email, id, request)));
    }

    @PatchMapping("/{id}/skills")
    @Operation(summary = "Update skills section")
    public ResponseEntity<ApiResponse<CvDocument>> patchSkills(
            @AuthenticationPrincipal String email, @PathVariable String id,
            @RequestBody UpdateCvRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Skills updated", cvService.update(email, id, request)));
    }

    @PatchMapping("/{id}/certifications")
    @Operation(summary = "Update certifications section")
    public ResponseEntity<ApiResponse<CvDocument>> patchCertifications(
            @AuthenticationPrincipal String email, @PathVariable String id,
            @RequestBody UpdateCvRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Certifications updated", cvService.update(email, id, request)));
    }

    @PatchMapping("/{id}/academic-sections")
    @Operation(summary = "Update academic-only sections (research, publications, etc.)")
    public ResponseEntity<ApiResponse<CvDocument>> patchAcademicSections(
            @AuthenticationPrincipal String email, @PathVariable String id,
            @RequestBody UpdateCvRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Academic sections updated", cvService.update(email, id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a CV")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal String email,
            @PathVariable String id) {
        cvService.delete(email, id);
        return ResponseEntity.ok(ApiResponse.success("CV deleted", null));
    }

    // ── AI Sub-resources ──────────────────────────────────────────────────────

    @PostMapping("/{id}/generate-summary")
    @Operation(summary = "AI: Generate a professional summary for this CV")
    public ResponseEntity<ApiResponse<Map<String, String>>> generateSummary(
            @AuthenticationPrincipal String email,
            @PathVariable String id,
            @RequestBody GenerateSummaryRequest request) {
        String summary = cvService.generateSummary(email, id, request);
        return ResponseEntity.ok(ApiResponse.success("Summary generated", Map.of("summary", summary)));
    }

    @PostMapping("/generate-achievement")
    @Operation(summary = "AI Achievement Builder: Convert role/task/result into a CV bullet point")
    public ResponseEntity<ApiResponse<Map<String, String>>> generateAchievement(
            @AuthenticationPrincipal String email,
            @Valid @RequestBody AchievementBuilderRequest request) {
        String bullet = cvService.generateAchievementBullet(request);
        return ResponseEntity.ok(ApiResponse.success("Achievement generated", Map.of("bullet", bullet)));
    }

    @PostMapping("/{id}/score")
    @Operation(summary = "AI: Compute CV Strength Score")
    public ResponseEntity<ApiResponse<CvScore>> scoreCv(
            @AuthenticationPrincipal String email,
            @PathVariable String id) {
        CvScore score = cvService.scoreCv(email, id);
        return ResponseEntity.ok(ApiResponse.success("CV scored", score));
    }

    @PostMapping("/{id}/job-match")
    @Operation(summary = "AI: Match CV against a job description — returns % match and keyword gaps")
    public ResponseEntity<ApiResponse<JobMatchResult>> jobMatch(
            @AuthenticationPrincipal String email,
            @PathVariable String id,
            @Valid @RequestBody JobMatchRequest request) {
        JobMatchResult result = cvService.matchJobDescription(email, id, request);
        return ResponseEntity.ok(ApiResponse.success("Job match analysis complete", result));
    }
}

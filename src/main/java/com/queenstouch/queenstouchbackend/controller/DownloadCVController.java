package com.queenstouch.queenstouchbackend.controller;

import com.queenstouch.queenstouchbackend.service.DocumentGeneratorService;
import com.queenstouch.queenstouchbackend.service.DocumentGeneratorService.Format;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cvs")
@RequiredArgsConstructor
@Tag(name = "CV Builder", description = "Create, manage, and AI-enhance CVs")
@SecurityRequirement(name = "bearerAuth")
public class DownloadCVController {

    private final DocumentGeneratorService documentGeneratorService;

    @GetMapping("/{id}/download")
    @Operation(
        summary = "Download a CV as PDF or DOCX",
        description = "Requires a paid order referencing this CV. Returns the file as a download attachment."
    )
    public ResponseEntity<byte[]> download(
            @AuthenticationPrincipal String email,
            @PathVariable String id,
            @Parameter(description = "Output format: PDF or DOCX", example = "PDF")
            @RequestParam(defaultValue = "PDF") String format) {

        Format fmt;
        try {
            fmt = Format.valueOf(format.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw com.queenstouch.queenstouchbackend.exception.AppException
                    .badRequest("Invalid format. Use PDF or DOCX.");
        }

        DocumentGeneratorService.GeneratedDocument doc =
                documentGeneratorService.generateCv(email, id, fmt);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(doc.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.filename() + "\"")
                .body(doc.bytes());
    }
}

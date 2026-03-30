package com.queenstouch.queenstouchbackend.service;

import com.queenstouch.queenstouchbackend.exception.AppException;
import com.queenstouch.queenstouchbackend.model.CvDocument;
import com.queenstouch.queenstouchbackend.service.docx.DocxCvGenerator;
import com.queenstouch.queenstouchbackend.service.pdf.PdfCvGenerator;
import com.queenstouch.queenstouchbackend.service.docx.DocxCoverLetterGenerator;
import com.queenstouch.queenstouchbackend.service.pdf.PdfCoverLetterGenerator;
import com.queenstouch.queenstouchbackend.model.CoverLetter;
import com.queenstouch.queenstouchbackend.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentGeneratorService {

    public enum Format { PDF, DOCX }

    private final CvService cvService;
    private final OrderService orderService;
    private final PdfCvGenerator pdfCvGenerator;
    private final DocxCvGenerator docxCvGenerator;
    private final CoverLetterService coverLetterService;
    private final UserService userService;
    private final PdfCoverLetterGenerator pdfCoverLetterGenerator;
    private final DocxCoverLetterGenerator docxCoverLetterGenerator;

    public record GeneratedDocument(byte[] bytes, String filename, String contentType) {}

    public GeneratedDocument generateCoverLetter(String userEmail, String clId, Format format) {
        CoverLetter cl = coverLetterService.getForUser(userEmail, clId);

        if (!orderService.hasValidOrderForDocument(clId)) {
            throw AppException.forbidden(
                    "A paid order is required to download this document. " +
                    "Please place an order via POST /api/v1/orders.");
        }

        User user = userService.findByEmail(userEmail);

        try {
            String safeName = (user.getFirstName() + "_" + user.getLastName())
                    .replaceAll("[^a-zA-Z0-9 _-]", "").replace(" ", "_");

            return switch (format) {
                case PDF -> new GeneratedDocument(
                        pdfCoverLetterGenerator.generate(cl, user),
                        "CoverLetter_" + safeName + ".pdf",
                        "application/pdf");
                case DOCX -> new GeneratedDocument(
                        docxCoverLetterGenerator.generate(cl, user),
                        "CoverLetter_" + safeName + ".docx",
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            };
        } catch (IOException e) {
            log.error("Cover Letter generation failed for id={} format={}: {}", clId, format, e.getMessage());
            throw AppException.internalError("Document generation failed. Please try again.");
        }
    }

    /**
     * Generates a CV document in the requested format, enforcing the order gate.
     *
     * @param userEmail  authenticated user
     * @param cvId       target CV
     * @param format     PDF or DOCX
     * @return           byte content + metadata for HTTP response
     */
    public GeneratedDocument generateCv(String userEmail, String cvId, Format format) {
        // 1. Ownership check (throws 404 if not owned)
        CvDocument cv = cvService.getForUser(userEmail, cvId);

        // 2. Order gate — must have a paid order that references this CV
        if (!orderService.hasValidOrderForDocument(cvId)) {
            throw AppException.forbidden(
                    "A paid order is required to download this document. " +
                    "Please place an order via POST /api/v1/orders.");
        }

        // 3. Generate
        try {
            String safeName = (cv.getFullName() != null ? cv.getFullName() : "CV")
                    .replaceAll("[^a-zA-Z0-9 _-]", "").replace(" ", "_");

            return switch (format) {
                case PDF -> new GeneratedDocument(
                        pdfCvGenerator.generate(cv),
                        "CV_" + safeName + ".pdf",
                        "application/pdf");
                case DOCX -> new GeneratedDocument(
                        docxCvGenerator.generate(cv),
                        "CV_" + safeName + ".docx",
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            };
        } catch (IOException e) {
            log.error("Document generation failed for cv={} format={}: {}", cvId, format, e.getMessage());
            throw AppException.internalError("Document generation failed. Please try again.");
        }
    }
}

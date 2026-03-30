package com.queenstouch.queenstouchbackend.service.docx;

import com.queenstouch.queenstouchbackend.model.CvDocument;
import com.queenstouch.queenstouchbackend.model.enums.CvType;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Component
public class DocxCvGenerator {

    // Colour constant for headings (deep teal: 0A4D68)
    private static final String BRAND_COLOR_HEX = "0A4D68";

    public byte[] generate(CvDocument cv) throws IOException {
        try (XWPFDocument doc = new XWPFDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // ── Name ──────────────────────────────────────────────────────────
            XWPFParagraph namePara = doc.createParagraph();
            namePara.setAlignment(ParagraphAlignment.LEFT);
            XWPFRun nameRun = namePara.createRun();
            nameRun.setText(isNotBlank(cv.getFullName()) ? cv.getFullName() : "Your Name");
            nameRun.setFontFamily("Calibri");
            nameRun.setFontSize(20);
            nameRun.setBold(true);
            nameRun.setColor(BRAND_COLOR_HEX);

            // ── Contact line ──────────────────────────────────────────────────
            StringBuilder contact = new StringBuilder();
            appendIfPresent(contact, cv.getEmail());
            appendIfPresent(contact, cv.getPhone());
            appendIfPresent(contact, cv.getLocation());
            appendIfPresent(contact, cv.getLinkedinUrl());
            appendIfPresent(contact, cv.getPortfolioUrl());

            if (contact.length() > 0) {
                XWPFParagraph contactPara = doc.createParagraph();
                XWPFRun contactRun = contactPara.createRun();
                contactRun.setText(contact.toString());
                contactRun.setFontFamily("Calibri");
                contactRun.setFontSize(9);
                contactRun.setColor("505050");
            }

            // ── Summary ───────────────────────────────────────────────────────
            if (isNotBlank(cv.getSummary())) {
                addHeading(doc, "Professional Summary");
                addBody(doc, cv.getSummary());
            }

            // ── Work Experience ───────────────────────────────────────────────
            if (notEmpty(cv.getWorkExperiences())) {
                addHeading(doc, "Work Experience");
                for (CvDocument.WorkExperience w : cv.getWorkExperiences()) {
                    String dates = nullSafe(w.getStartDate()) + " – " + (w.isCurrent() ? "Present" : nullSafe(w.getEndDate()));
                    addEntryTitle(doc, w.getJobTitle(), dates);
                    String subtitle = nullSafe(w.getCompany()) + (isNotBlank(w.getLocation()) ? " · " + w.getLocation() : "");
                    if (!subtitle.isBlank()) addItalicBody(doc, subtitle);
                    for (String bullet : w.getBullets()) addBullet(doc, bullet);
                    doc.createParagraph(); // spacer
                }
            }

            // ── Education ─────────────────────────────────────────────────────
            if (notEmpty(cv.getEducations())) {
                addHeading(doc, "Education");
                for (CvDocument.Education e : cv.getEducations()) {
                    String degree = nullSafe(e.getDegree()) + (isNotBlank(e.getFieldOfStudy()) ? " – " + e.getFieldOfStudy() : "");
                    String dates  = nullSafe(e.getStartDate()) + (isNotBlank(e.getEndDate()) ? " – " + e.getEndDate() : "");
                    addEntryTitle(doc, degree, dates);
                    String subtitle = nullSafe(e.getInstitution()) + (isNotBlank(e.getGrade()) ? " | Grade: " + e.getGrade() : "");
                    addItalicBody(doc, subtitle);
                }
                doc.createParagraph();
            }

            // ── Skills ────────────────────────────────────────────────────────
            if (notEmpty(cv.getSkills())) {
                addHeading(doc, "Skills");
                StringBuilder sb = new StringBuilder();
                cv.getSkills().forEach(s -> sb.append(s.getName()).append("  ·  "));
                addBody(doc, sb.toString().replaceAll("  ·  $", ""));
            }

            // ── Certifications ────────────────────────────────────────────────
            if (notEmpty(cv.getCertifications())) {
                addHeading(doc, "Certifications");
                for (CvDocument.Certification c : cv.getCertifications()) {
                    String line = nullSafe(c.getName())
                            + (isNotBlank(c.getIssuer()) ? " — " + c.getIssuer() : "")
                            + (isNotBlank(c.getYear()) ? " (" + c.getYear() + ")" : "");
                    addBullet(doc, line);
                }
                doc.createParagraph();
            }

            // ── Languages ─────────────────────────────────────────────────────
            if (notEmpty(cv.getLanguages())) {
                addHeading(doc, "Languages");
                addBody(doc, String.join("  ·  ", cv.getLanguages()));
            }

            // ── Academic Sections ─────────────────────────────────────────────
            if (CvType.ACADEMIC.equals(cv.getCvType())) {
                addBulletSection(doc, "Research Interests", cv.getResearchInterests());
                addBulletSection(doc, "Publications", cv.getPublications());
                addBulletSection(doc, "Conferences & Presentations", cv.getConferences());
                addBulletSection(doc, "Teaching Experience", cv.getTeachingExperience());
                addBulletSection(doc, "Awards & Honours", cv.getAwards());
            }

            doc.write(out);
            return out.toByteArray();
        }
    }

    // ── Paragraph Builders ────────────────────────────────────────────────────

    private void addHeading(XWPFDocument doc, String text) {
        XWPFParagraph p = doc.createParagraph();
        p.setSpacingAfter(40);
        p.setSpacingBefore(160);
        XWPFRun run = p.createRun();
        run.setText(text.toUpperCase());
        run.setFontFamily("Calibri");
        run.setFontSize(11);
        run.setBold(true);
        run.setColor(BRAND_COLOR_HEX);
    }

    private void addEntryTitle(XWPFDocument doc, String title, String rightText) {
        // POI doesn't natively support two-column inline; use tab-based layout
        XWPFParagraph p = doc.createParagraph();
        XWPFRun titleRun = p.createRun();
        titleRun.setText(nullSafe(title));
        titleRun.setFontFamily("Calibri");
        titleRun.setFontSize(11);
        titleRun.setBold(true);
        if (isNotBlank(rightText)) {
            XWPFRun dateRun = p.createRun();
            dateRun.addTab();
            dateRun.setText(rightText);
            dateRun.setFontFamily("Calibri");
            dateRun.setFontSize(10);
            dateRun.setColor("505050");
        }
    }

    private void addBody(XWPFDocument doc, String text) {
        XWPFParagraph p = doc.createParagraph();
        p.setSpacingAfter(60);
        XWPFRun run = p.createRun();
        run.setText(nullSafe(text));
        run.setFontFamily("Calibri");
        run.setFontSize(11);
    }

    private void addItalicBody(XWPFDocument doc, String text) {
        XWPFParagraph p = doc.createParagraph();
        XWPFRun run = p.createRun();
        run.setText(nullSafe(text));
        run.setFontFamily("Calibri");
        run.setFontSize(10);
        run.setItalic(true);
        run.setColor("505050");
    }

    private void addBullet(XWPFDocument doc, String text) {
        XWPFParagraph p = doc.createParagraph();
        XWPFRun run = p.createRun();
        run.setText("• " + nullSafe(text));
        run.setFontFamily("Calibri");
        run.setFontSize(11);
    }

    private void addBulletSection(XWPFDocument doc, String heading, List<String> items) {
        if (!notEmpty(items)) return;
        addHeading(doc, heading);
        items.forEach(item -> addBullet(doc, item));
        doc.createParagraph();
    }

    // ── Utils ─────────────────────────────────────────────────────────────────

    private void appendIfPresent(StringBuilder sb, String val) {
        if (isNotBlank(val)) {
            if (sb.length() > 0) sb.append("  |  ");
            sb.append(val);
        }
    }

    private boolean isNotBlank(String s) { return s != null && !s.isBlank(); }
    private String nullSafe(String s)     { return s != null ? s : ""; }
    private <T> boolean notEmpty(List<T> l) { return l != null && !l.isEmpty(); }
}

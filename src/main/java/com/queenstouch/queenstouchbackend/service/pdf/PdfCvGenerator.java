package com.queenstouch.queenstouchbackend.service.pdf;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.queenstouch.queenstouchbackend.model.CvDocument;
import com.queenstouch.queenstouchbackend.model.enums.CvType;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Component
public class PdfCvGenerator {

    // Brand colour: deep teal
    private static final DeviceRgb BRAND_BLUE = new DeviceRgb(10, 77, 104);
    private static final DeviceRgb LIGHT_GREY = new DeviceRgb(245, 245, 245);
    private static final DeviceRgb DIVIDER_GREY = new DeviceRgb(200, 200, 200);

    public byte[] generate(CvDocument cv) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document doc = new Document(pdf, PageSize.A4);
        doc.setMargins(36, 50, 36, 50);

        PdfFont bold   = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont regular = PdfFontFactory.createFont(StandardFonts.HELVETICA);

        // ── Header ────────────────────────────────────────────────────────────
        addHeader(doc, cv, bold, regular);

        // ── Summary ───────────────────────────────────────────────────────────
        if (isNotBlank(cv.getSummary())) {
            addSectionHeading(doc, "Professional Summary", bold);
            doc.add(new Paragraph(cv.getSummary()).setFont(regular).setFontSize(10).setMultipliedLeading(1.3f));
        }

        // ── Work Experience ───────────────────────────────────────────────────
        if (notEmpty(cv.getWorkExperiences())) {
            addSectionHeading(doc, "Work Experience", bold);
            for (CvDocument.WorkExperience w : cv.getWorkExperiences()) {
                String dates = w.getStartDate() + " – " + (w.isCurrent() ? "Present" : w.getEndDate());
                addEntryTitle(doc, w.getJobTitle(), dates, bold, regular);
                if (isNotBlank(w.getCompany())) {
                    doc.add(new Paragraph(w.getCompany() + (isNotBlank(w.getLocation()) ? " · " + w.getLocation() : ""))
                            .setFont(regular).setFontSize(10).setItalic().setMarginBottom(2));
                }
                for (String bullet : w.getBullets()) {
                    doc.add(new Paragraph("• " + bullet).setFont(regular).setFontSize(10)
                            .setMarginLeft(10).setMultipliedLeading(1.2f));
                }
                doc.add(new Paragraph("\n").setFontSize(4));
            }
        }

        // ── Education ─────────────────────────────────────────────────────────
        if (notEmpty(cv.getEducations())) {
            addSectionHeading(doc, "Education", bold);
            for (CvDocument.Education e : cv.getEducations()) {
                String degree = e.getDegree() + (isNotBlank(e.getFieldOfStudy()) ? " — " + e.getFieldOfStudy() : "");
                String dates  = nullSafe(e.getStartDate()) + (isNotBlank(e.getEndDate()) ? " – " + e.getEndDate() : "");
                addEntryTitle(doc, degree, dates, bold, regular);
                doc.add(new Paragraph(nullSafe(e.getInstitution()) + (isNotBlank(e.getGrade()) ? " | Grade: " + e.getGrade() : ""))
                        .setFont(regular).setFontSize(10).setItalic().setMarginBottom(6));
            }
        }

        // ── Skills ────────────────────────────────────────────────────────────
        if (notEmpty(cv.getSkills())) {
            addSectionHeading(doc, "Skills", bold);
            StringBuilder sb = new StringBuilder();
            cv.getSkills().forEach(s -> sb.append(s.getName()).append("  ·  "));
            String skillStr = sb.toString().replaceAll("  ·  $", "");
            doc.add(new Paragraph(skillStr).setFont(regular).setFontSize(10).setMarginBottom(6));
        }

        // ── Certifications ────────────────────────────────────────────────────
        if (notEmpty(cv.getCertifications())) {
            addSectionHeading(doc, "Certifications", bold);
            for (CvDocument.Certification c : cv.getCertifications()) {
                String line = c.getName() + (isNotBlank(c.getIssuer()) ? " — " + c.getIssuer() : "")
                        + (isNotBlank(c.getYear()) ? " (" + c.getYear() + ")" : "");
                doc.add(new Paragraph("• " + line).setFont(regular).setFontSize(10).setMarginLeft(10));
            }
            doc.add(new Paragraph("\n").setFontSize(4));
        }

        // ── Languages ─────────────────────────────────────────────────────────
        if (notEmpty(cv.getLanguages())) {
            addSectionHeading(doc, "Languages", bold);
            doc.add(new Paragraph(String.join("  ·  ", cv.getLanguages()))
                    .setFont(regular).setFontSize(10).setMarginBottom(6));
        }

        // ── Academic Sections (only for ACADEMIC CVs) ─────────────────────────
        if (CvType.ACADEMIC.equals(cv.getCvType())) {
            addAcademicSections(doc, cv, bold, regular);
        }

        doc.close();
        return out.toByteArray();
    }

    // ── Layout Helpers ────────────────────────────────────────────────────────

    private void addHeader(Document doc, CvDocument cv, PdfFont bold, PdfFont regular) throws IOException {
        String name = isNotBlank(cv.getFullName()) ? cv.getFullName() : "Your Name";
        Paragraph namePara = new Paragraph(name)
                .setFont(bold).setFontSize(22).setFontColor(BRAND_BLUE)
                .setMarginBottom(3);
        doc.add(namePara);

        StringBuilder contact = new StringBuilder();
        appendIfPresent(contact, cv.getEmail());
        appendIfPresent(contact, cv.getPhone());
        appendIfPresent(contact, cv.getLocation());
        appendIfPresent(contact, cv.getLinkedinUrl());
        appendIfPresent(contact, cv.getPortfolioUrl());

        if (contact.length() > 0) {
            doc.add(new Paragraph(contact.toString().replaceAll("  \\|  $", ""))
                    .setFont(regular).setFontSize(9.5f).setFontColor(new DeviceRgb(80, 80, 80))
                    .setMarginBottom(8));
        }

        // Divider line
        doc.add(new Paragraph("").setBorderBottom(new SolidBorder(BRAND_BLUE, 1.5f)).setMarginBottom(10));
    }

    private void addSectionHeading(Document doc, String title, PdfFont bold) {
        doc.add(new Paragraph(title.toUpperCase())
                .setFont(bold).setFontSize(10.5f).setFontColor(BRAND_BLUE)
                .setMarginTop(10).setMarginBottom(4));
        doc.add(new Paragraph("").setBorderBottom(new SolidBorder(DIVIDER_GREY, 0.5f)).setMarginBottom(6));
    }

    private void addEntryTitle(Document doc, String title, String rightText, PdfFont bold, PdfFont regular) {
        Table row = new Table(UnitValue.createPercentArray(new float[]{70, 30})).useAllAvailableWidth();
        Cell left  = new Cell().add(new Paragraph(title).setFont(bold).setFontSize(10.5f)).setBorder(null);
        Cell right = new Cell().add(new Paragraph(nullSafe(rightText)).setFont(regular).setFontSize(9f)
                .setTextAlignment(TextAlignment.RIGHT)).setBorder(null);
        row.addCell(left).addCell(right);
        row.setMarginBottom(1);
        doc.add(row);
    }

    private void addAcademicSections(Document doc, CvDocument cv, PdfFont bold, PdfFont regular) {
        addBulletSection(doc, "Research Interests", cv.getResearchInterests(), bold, regular);
        addBulletSection(doc, "Publications", cv.getPublications(), bold, regular);
        addBulletSection(doc, "Conferences & Presentations", cv.getConferences(), bold, regular);
        addBulletSection(doc, "Teaching Experience", cv.getTeachingExperience(), bold, regular);
        addBulletSection(doc, "Awards & Honours", cv.getAwards(), bold, regular);
    }

    private void addBulletSection(Document doc, String title, List<String> items, PdfFont bold, PdfFont regular) {
        if (!notEmpty(items)) return;
        addSectionHeading(doc, title, bold);
        items.forEach(item -> doc.add(new Paragraph("• " + item)
                .setFont(regular).setFontSize(10).setMarginLeft(10).setMultipliedLeading(1.2f)));
        doc.add(new Paragraph("\n").setFontSize(4));
    }

    // ── Utils ─────────────────────────────────────────────────────────────────

    private void appendIfPresent(StringBuilder sb, String val) {
        if (isNotBlank(val)) {
            if (sb.length() > 0) sb.append("  |  ");
            sb.append(val);
        }
    }

    private boolean isNotBlank(String s) {
        return s != null && !s.isBlank();
    }

    private String nullSafe(String s) {
        return s != null ? s : "";
    }

    private <T> boolean notEmpty(List<T> list) {
        return list != null && !list.isEmpty();
    }
}

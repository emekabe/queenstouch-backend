package com.queenstouch.queenstouchbackend.service.docx;

import com.queenstouch.queenstouchbackend.model.CvDocument;
import com.queenstouch.queenstouchbackend.model.enums.CvType;
import com.queenstouch.queenstouchbackend.model.enums.SkillLevel;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DocxCvGenerator – unit tests")
class DocxCvGeneratorTest {

    private DocxCvGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new DocxCvGenerator();
    }

    private CvDocument buildCv() {
        return CvDocument.builder()
                .id("cv-002")
                .userId("user-002")
                .cvType(CvType.STANDARD)
                .fullName("John Smith")
                .email("john@example.com")
                .phone("+234 801 000 0001")
                .location("Lagos, Nigeria")
                .summary("Seasoned product manager with 8 years of experience.")
                .workExperiences(List.of(
                        CvDocument.WorkExperience.builder()
                                .jobTitle("Product Manager")
                                .company("StartupXYZ")
                                .startDate("2018-03")
                                .current(true)
                                .bullets(List.of("Launched 3 products generating over ₦50M revenue"))
                                .build()))
                .educations(List.of(
                        CvDocument.Education.builder()
                                .institution("University of Ibadan")
                                .degree("MBA")
                                .startDate("2015")
                                .endDate("2017")
                                .build()))
                .skills(List.of(
                        CvDocument.Skill.builder().name("Product Strategy").level(SkillLevel.EXPERT).build(),
                        CvDocument.Skill.builder().name("Agile").level(SkillLevel.ADVANCED).build()))
                .certifications(List.of(
                        CvDocument.Certification.builder().name("PMP").issuer("PMI").year("2019").build()))
                .languages(List.of("English", "Yoruba"))
                .build();
    }

    @Test
    @DisplayName("generate() returns a non-empty byte array")
    void generate_returnsNonEmptyBytes() throws Exception {
        byte[] docx = generator.generate(buildCv());
        assertThat(docx).isNotEmpty();
    }

    @Test
    @DisplayName("generated DOCX contains a paragraph with the applicant full name")
    void generate_containsFullNameParagraph() throws Exception {
        byte[] docx = generator.generate(buildCv());
        try (XWPFDocument doc = new XWPFDocument(new ByteArrayInputStream(docx))) {
            boolean found = doc.getParagraphs().stream()
                    .map(XWPFParagraph::getText)
                    .anyMatch(t -> t.contains("John Smith"));
            assertThat(found).isTrue();
        }
    }

    @Test
    @DisplayName("generated DOCX contains 'Work Experience' heading text")
    void generate_containsWorkExperienceHeading() throws Exception {
        byte[] docx = generator.generate(buildCv());
        try (XWPFDocument doc = new XWPFDocument(new ByteArrayInputStream(docx))) {
            boolean found = doc.getParagraphs().stream()
                    .map(XWPFParagraph::getText)
                    .anyMatch(t -> t.toUpperCase().contains("WORK EXPERIENCE"));
            assertThat(found).isTrue();
        }
    }

    @Test
    @DisplayName("generate() on minimal CV (no fields) still produces valid DOCX")
    void generate_minimalCv_producesValidDocx() throws Exception {
        CvDocument minimal = CvDocument.builder()
                .id("min-002").userId("u2").cvType(CvType.STANDARD).build();
        byte[] docx = generator.generate(minimal);
        assertThat(docx).isNotEmpty();
        // Verify it can be parsed by Apache POI without exception
        try (XWPFDocument doc = new XWPFDocument(new ByteArrayInputStream(docx))) {
            assertThat(doc.getParagraphs()).isNotNull();
        }
    }

    @Test
    @DisplayName("ACADEMIC CV includes research interests section")
    void generate_academicCv_includesResearchSection() throws Exception {
        CvDocument academic = buildCv().toBuilder()
                .cvType(CvType.ACADEMIC)
                .researchInterests(List.of("AI Ethics", "NLP"))
                .build();
        byte[] docx = generator.generate(academic);
        try (XWPFDocument doc = new XWPFDocument(new ByteArrayInputStream(docx))) {
            boolean found = doc.getParagraphs().stream()
                    .map(XWPFParagraph::getText)
                    .anyMatch(t -> t.toUpperCase().contains("RESEARCH INTERESTS"));
            assertThat(found).isTrue();
        }
    }
}

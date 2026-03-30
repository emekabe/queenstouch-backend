package com.queenstouch.queenstouchbackend.service.pdf;

import com.queenstouch.queenstouchbackend.model.CvDocument;
import com.queenstouch.queenstouchbackend.model.enums.CvType;
import com.queenstouch.queenstouchbackend.model.enums.SkillLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PdfCvGenerator – unit tests")
class PdfCvGeneratorTest {

    private PdfCvGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new PdfCvGenerator();
    }

    private CvDocument buildStandardCv() {
        return CvDocument.builder()
                .id("cv-001")
                .userId("user-001")
                .cvType(CvType.STANDARD)
                .fullName("Jane Doe")
                .email("jane@example.com")
                .phone("+44 7700 900123")
                .location("London, UK")
                .linkedinUrl("linkedin.com/in/janedoe")
                .summary("Experienced software engineer specialising in backend systems.")
                .workExperiences(List.of(
                        CvDocument.WorkExperience.builder()
                                .jobTitle("Senior Engineer")
                                .company("TechCorp Ltd")
                                .location("remote")
                                .startDate("2021-01")
                                .current(true)
                                .bullets(List.of(
                                        "Designed microservices reducing latency by 35%",
                                        "Mentored 4 junior engineers"))
                                .build()))
                .educations(List.of(
                        CvDocument.Education.builder()
                                .institution("University of Lagos")
                                .degree("BSc Computer Science")
                                .fieldOfStudy("Computer Science")
                                .grade("First Class")
                                .startDate("2012")
                                .endDate("2016")
                                .build()))
                .skills(List.of(
                        CvDocument.Skill.builder().name("Java").level(SkillLevel.EXPERT).build(),
                        CvDocument.Skill.builder().name("Spring Boot").level(SkillLevel.EXPERT).build(),
                        CvDocument.Skill.builder().name("MongoDB").level(SkillLevel.ADVANCED).build()))
                .certifications(List.of(
                        CvDocument.Certification.builder()
                                .name("AWS Solutions Architect").issuer("Amazon").year("2022").build()))
                .languages(List.of("English", "French"))
                .build();
    }

    @Test
    @DisplayName("generate() returns a non-empty byte array")
    void generate_returnsNonEmptyBytes() throws Exception {
        byte[] pdf = generator.generate(buildStandardCv());
        assertThat(pdf).isNotEmpty();
    }

    @Test
    @DisplayName("generate() output starts with the PDF magic bytes '%PDF'")
    void generate_outputIsPdfFile() throws Exception {
        byte[] pdf = generator.generate(buildStandardCv());
        String header = new String(pdf, 0, 4);
        assertThat(header).isEqualTo("%PDF");
    }

    @Test
    @DisplayName("generate() on minimal CV (blank sections) still produces valid PDF")
    void generate_minimalCv_producesPdf() throws Exception {
        CvDocument minimal = CvDocument.builder()
                .id("min-001").userId("u1").cvType(CvType.STANDARD).build();
        byte[] pdf = generator.generate(minimal);
        assertThat(pdf).isNotEmpty();
        assertThat(new String(pdf, 0, 4)).isEqualTo("%PDF");
    }

    @Test
    @DisplayName("generate() with ACADEMIC type produces a larger PDF than STANDARD (academic sections added)")
    void generate_academicCvIsLargerThanStandard() throws Exception {
        CvDocument standard = buildStandardCv();

        CvDocument academic = buildStandardCv().toBuilder()
                .cvType(CvType.ACADEMIC)
                .researchInterests(List.of("Machine Learning", "Distributed Systems"))
                .publications(List.of("J. Doe et al. — IEEE 2023"))
                .awards(List.of("Dean's Prize 2016"))
                .build();

        byte[] standardPdf = generator.generate(standard);
        byte[] academicPdf = generator.generate(academic);

        assertThat(academicPdf.length).isGreaterThan(standardPdf.length);
    }
}

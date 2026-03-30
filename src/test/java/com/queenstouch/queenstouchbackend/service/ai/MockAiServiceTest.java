package com.queenstouch.queenstouchbackend.service.ai;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MockAiService – unit tests")
class MockAiServiceTest {

    private MockAiService service;

    @BeforeEach
    void setUp() {
        service = new MockAiService();
    }

    @Test
    @DisplayName("generateProfessionalSummary returns a non-blank string")
    void generateSummary_returnsNonBlank() {
        String result = service.generateProfessionalSummary(
                "John Doe", "Software Engineer", "5", "Java, Spring Boot", "Led team of 8");
        assertThat(result).isNotBlank();
    }

    @Test
    @DisplayName("generateSummary interpolates job title into the response")
    void generateSummary_containsJobTitle() {
        String result = service.generateProfessionalSummary(
                "Jane", "Data Analyst", "3", "Python, SQL", "Reduced costs by 20%");
        assertThat(result).containsIgnoringCase("Data Analyst");
    }

    @Test
    @DisplayName("generateAchievementBullet returns a non-blank string")
    void generateAchievementBullet_returnsNonBlank() {
        String result = service.generateAchievementBullet("Engineering Lead", "CI/CD pipeline", "50% faster deployments");
        assertThat(result).isNotBlank();
    }

    @Test
    @DisplayName("generateCoverLetter mentions the company name")
    void generateCoverLetter_mentionsCompany() {
        String result = service.generateCoverLetter(
                "Backend Developer", "Acme Corp", "Java, REST APIs", "3 years", "Alice");
        assertThat(result).containsIgnoringCase("Acme Corp");
    }

    @Test
    @DisplayName("generateCoverLetter is signed with the applicant name")
    void generateCoverLetter_signedWithApplicantName() {
        String result = service.generateCoverLetter("Dev", "Corp", "Skills", "2 years", "Bob Smith");
        assertThat(result).containsIgnoringCase("Bob Smith");
    }

    @Test
    @DisplayName("generateLinkedInHeadline returns a non-blank string")
    void generateLinkedInHeadline_returnsNonBlank() {
        String result = service.generateLinkedInHeadline("Experienced project manager in fintech");
        assertThat(result).isNotBlank();
    }

    @Test
    @DisplayName("generateLinkedInSummary returns a multi-word response")
    void generateLinkedInSummary_returnsMultiWord() {
        String result = service.generateLinkedInSummary("Background in marketing");
        assertThat(result.split("\\s+")).hasSizeGreaterThan(10);
    }

    @Test
    @DisplayName("generateLinkedInSkills returns a CSV of at least 5 skills")
    void generateLinkedInSkills_returnsAtLeastFiveSkills() {
        String result = service.generateLinkedInSkills("Seasoned developer");
        String[] skills = result.split(",");
        assertThat(skills).hasSizeGreaterThanOrEqualTo(5);
    }

    @Test
    @DisplayName("scoreCv returns JSON containing 'overall' key")
    void scoreCv_returnsJsonWithOverall() {
        String result = service.scoreCv("name: Data Analyst\nexperience: 5 years");
        assertThat(result).contains("overall");
        assertThat(result).contains("keywordStrength");
        assertThat(result).contains("structure");
    }

    @Test
    @DisplayName("matchJobDescription returns JSON containing 'matchPercent' key")
    void matchJobDescription_returnsJsonWithMatchPercent() {
        String result = service.matchJobDescription(
                "CV content here", "We need a Java developer with Spring Boot skills");
        assertThat(result).contains("matchPercent");
        assertThat(result).contains("missingKeywords");
    }
}

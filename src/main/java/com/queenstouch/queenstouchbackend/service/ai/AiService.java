package com.queenstouch.queenstouchbackend.service.ai;

/**
 * Abstraction over the Spring AI / Gemini integration.
 * Any method here can be stubbed by MockAiService when no API key is available.
 */
public interface AiService {

    /** Generate a professional summary from the provided context. */
    String generateProfessionalSummary(String name, String jobTitle,
                                       String yearsOfExperience, String skills,
                                       String achievements);

    /** Transform a plain user-written job description into a powerful achievement bullet. */
    String generateAchievementBullet(String role, String task, String result);

    /** Generate a tailored cover letter. */
    String generateCoverLetter(String jobTitle, String company,
                               String keySkills, String relevantExperience,
                               String applicantName);

    /** Generate a LinkedIn headline. */
    String generateLinkedInHeadline(String careerSummary);

    /** Generate a LinkedIn professional summary. */
    String generateLinkedInSummary(String careerSummary);

    /** Generate a comma-separated list of professional skills. */
    String generateLinkedInSkills(String careerSummary);

    /**
     * Score the provided CV content.
     * Returns structured JSON: {"overall":82,"structure":80,"keywordStrength":75,"atsCompatibility":90,
     *   "strengths":["..."],"improvements":["..."]}
     */
    String scoreCv(String cvContent);

    /**
     * Match the CV against a job description.
     * Returns structured JSON: {"matchPercent":68,"missingKeywords":["..."],"suggestions":["..."]}
     */
    String matchJobDescription(String cvContent, String jobDescription);
}

package com.queenstouch.queenstouchbackend.service.ai;

import org.springframework.stereotype.Service;

/**
 * Mock implementation of AiService for use in tests or when GEMINI_API_KEY is not set.
 * Returns plausible stub responses that match the expected format.
 */
@Service("mockAiService")
public class MockAiService implements AiService {

    @Override
    public String generateProfessionalSummary(String name, String jobTitle,
                                               String yearsOfExperience, String skills,
                                               String achievements) {
        return "A results-driven %s with %s years of experience in %s. Proven track record of delivering high-impact solutions and driving organisational success. Passionate about continuous improvement and professional excellence."
                .formatted(jobTitle, yearsOfExperience, skills);
    }

    @Override
    public String generateAchievementBullet(String role, String task, String result) {
        return "Delivered measurable improvements in %s by executing %s, resulting in %s."
                .formatted(role, task, result);
    }

    @Override
    public String generateCoverLetter(String jobTitle, String company,
                                       String keySkills, String relevantExperience,
                                       String applicantName) {
        return """
                Dear Hiring Manager,
                
                I am writing to express my strong interest in the %s position at %s. With expertise in %s \
                and %s of relevant experience, I am confident I can contribute significantly to your team.
                
                Throughout my career, I have consistently demonstrated the ability to deliver results and \
                exceed expectations. I am particularly drawn to %s's commitment to excellence and innovation.
                
                I would welcome the opportunity to discuss how my background aligns with your needs. \
                Thank you for considering my application.
                
                Sincerely,
                %s
                """.formatted(jobTitle, company, keySkills, relevantExperience, company, applicantName);
    }

    @Override
    public String generateLinkedInHeadline(String careerSummary) {
        return "Professional | Expert in Key Skills | Driving Results & Innovation";
    }

    @Override
    public String generateLinkedInSummary(String careerSummary) {
        return "Experienced professional with a passion for delivering impactful results. " +
                "Known for combining technical expertise with strategic thinking to solve complex challenges. " +
                "Committed to continuous learning and professional growth.";
    }

    @Override
    public String generateLinkedInSkills(String careerSummary) {
        return "Project Management, Strategic Planning, Team Leadership, Communication, " +
                "Problem Solving, Data Analysis, Microsoft Office, Stakeholder Management, " +
                "Process Improvement, Critical Thinking";
    }

    @Override
    public String scoreCv(String cvContent) {
        return """
                {
                  "overall": 74,
                  "structure": 80,
                  "keywordStrength": 65,
                  "atsCompatibility": 78,
                  "strengths": ["Clear section structure", "Good education detail", "ATS-friendly formatting"],
                  "improvements": ["Add quantified achievements to work experience", "Include more industry keywords", "Expand certifications section"]
                }
                """;
    }

    @Override
    public String matchJobDescription(String cvContent, String jobDescription) {
        return """
                {
                  "matchPercent": 68,
                  "missingKeywords": ["Project coordination", "Stakeholder management", "Data reporting"],
                  "suggestions": [
                    "Add 'Project coordination' to your skills or experience section",
                    "Highlight stakeholder communication in your work experience",
                    "Mention any data reporting tools you have used"
                  ]
                }
                """;
    }
}

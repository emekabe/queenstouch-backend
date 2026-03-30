package com.queenstouch.queenstouchbackend.service.ai;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Primary
@RequiredArgsConstructor
public class GeminiAiService implements AiService {

    private final ChatClient.Builder chatClientBuilder;

    private String call(String prompt) {
        ChatClient client = chatClientBuilder.build();
        return client.prompt()
                .user(prompt)
                .call()
                .content();
    }

    @Override
    public String generateProfessionalSummary(String name, String jobTitle,
                                               String yearsOfExperience, String skills,
                                               String achievements) {
        String prompt = """
                You are a professional CV writer. Write a compelling 3–4 sentence professional summary for a CV.
                
                Candidate details:
                - Name: %s
                - Job Title: %s
                - Years of Experience: %s
                - Key Skills: %s
                - Key Achievements: %s
                
                Return ONLY the summary text. No labels, no preamble.
                """.formatted(name, jobTitle, yearsOfExperience, skills, achievements);
        return call(prompt);
    }

    @Override
    public String generateAchievementBullet(String role, String task, String result) {
        String prompt = """
                Transform the following into a single powerful, ATS-friendly CV achievement bullet point.
                Use the CAR (Challenge–Action–Result) or PAR format. Include metrics where implied.
                
                Role: %s
                Task/Responsibility: %s
                Result/Impact: %s
                
                Return ONLY the bullet text (no dash or bullet character). No preamble.
                """.formatted(role, task, result);
        return call(prompt);
    }

    @Override
    public String generateCoverLetter(String jobTitle, String company,
                                       String keySkills, String relevantExperience,
                                       String applicantName) {
        String prompt = """
                Write a professional, tailored cover letter for the following details.
                Keep it to 3 paragraphs: introduction, value proposition, call to action.
                
                Applicant Name: %s
                Job Title: %s
                Company Name: %s
                Key Skills: %s
                Relevant Experience: %s
                
                Return ONLY the cover letter body text. No subject line. No address blocks.
                """.formatted(applicantName, jobTitle, company, keySkills, relevantExperience);
        return call(prompt);
    }

    @Override
    public String generateLinkedInHeadline(String careerSummary) {
        String prompt = """
                Generate a compelling LinkedIn headline (max 220 characters) based on:
                %s
                Return ONLY the headline. No quotes. No preamble.
                """.formatted(careerSummary);
        return call(prompt);
    }

    @Override
    public String generateLinkedInSummary(String careerSummary) {
        String prompt = """
                Write a compelling LinkedIn "About" section (3–5 short paragraphs) based on:
                %s
                Return ONLY the summary text. No labels. No preamble.
                """.formatted(careerSummary);
        return call(prompt);
    }

    @Override
    public String generateLinkedInSkills(String careerSummary) {
        String prompt = """
                Based on the following career profile, list 10–15 relevant professional skills
                for a LinkedIn profile. Format: comma-separated list only. No preamble.
                
                %s
                """.formatted(careerSummary);
        return call(prompt);
    }

    @Override
    public String scoreCv(String cvContent) {
        String prompt = """
                You are an expert ATS and CV evaluator. Score the following CV and return ONLY valid JSON.
                
                JSON schema:
                {
                  "overall": <0-100>,
                  "structure": <0-100>,
                  "keywordStrength": <0-100>,
                  "atsCompatibility": <0-100>,
                  "strengths": ["...", "..."],
                  "improvements": ["...", "..."]
                }
                
                CV Content:
                %s
                """.formatted(cvContent);
        return call(prompt);
    }

    @Override
    public String matchJobDescription(String cvContent, String jobDescription) {
        String prompt = """
                You are an ATS expert. Compare the following CV against the job description.
                Return ONLY valid JSON using this schema:
                {
                  "matchPercent": <0-100>,
                  "missingKeywords": ["...", "..."],
                  "suggestions": ["...", "..."]
                }
                
                CV:
                %s
                
                Job Description:
                %s
                """.formatted(cvContent, jobDescription);
        return call(prompt);
    }
}

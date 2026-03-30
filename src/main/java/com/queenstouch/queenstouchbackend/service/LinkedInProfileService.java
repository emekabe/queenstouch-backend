package com.queenstouch.queenstouchbackend.service;

import com.queenstouch.queenstouchbackend.dto.request.GenerateLinkedInRequest;
import com.queenstouch.queenstouchbackend.exception.AppException;
import com.queenstouch.queenstouchbackend.model.LinkedInProfile;
import com.queenstouch.queenstouchbackend.repository.LinkedInProfileRepository;
import com.queenstouch.queenstouchbackend.service.ai.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LinkedInProfileService {

    private final LinkedInProfileRepository linkedInProfileRepository;
    private final UserService userService;
    private final AiService aiService;

    public LinkedInProfile generate(String userEmail, GenerateLinkedInRequest request) {
        var user = userService.findByEmail(userEmail);
        String input = request.getCareerSummaryInput();

        String headline = aiService.generateLinkedInHeadline(input);
        String summary = aiService.generateLinkedInSummary(input);
        String skillsCsv = aiService.generateLinkedInSkills(input);
        List<String> skills = Arrays.stream(skillsCsv.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toList());

        LinkedInProfile profile = LinkedInProfile.builder()
                .userId(user.getId())
                .careerSummaryInput(input)
                .headline(headline)
                .summary(summary)
                .skills(skills)
                .build();
        return linkedInProfileRepository.save(profile);
    }

    public List<LinkedInProfile> listForUser(String userEmail) {
        var user = userService.findByEmail(userEmail);
        return linkedInProfileRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    public LinkedInProfile getForUser(String userEmail, String id) {
        var user = userService.findByEmail(userEmail);
        return linkedInProfileRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> AppException.notFound("LinkedIn profile not found"));
    }

    public void delete(String userEmail, String id) {
        LinkedInProfile profile = getForUser(userEmail, id);
        linkedInProfileRepository.delete(profile);
    }
}

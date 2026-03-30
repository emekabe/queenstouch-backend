package com.queenstouch.queenstouchbackend.service;

import com.queenstouch.queenstouchbackend.dto.request.CreateCoverLetterRequest;
import com.queenstouch.queenstouchbackend.exception.AppException;
import com.queenstouch.queenstouchbackend.model.CoverLetter;
import com.queenstouch.queenstouchbackend.repository.CoverLetterRepository;
import com.queenstouch.queenstouchbackend.service.ai.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CoverLetterService {

    private final CoverLetterRepository coverLetterRepository;
    private final UserService userService;
    private final AiService aiService;

    public CoverLetter create(String userEmail, CreateCoverLetterRequest request) {
        var user = userService.findByEmail(userEmail);
        String skillsStr = request.getKeySkills() != null
                ? String.join(", ", request.getKeySkills()) : "";

        String content = aiService.generateCoverLetter(
                request.getJobTitle(), request.getCompanyName(),
                skillsStr, request.getRelevantExperience(), user.getFirstName() + " " + user.getLastName());

        CoverLetter letter = CoverLetter.builder()
                .userId(user.getId())
                .jobTitle(request.getJobTitle())
                .companyName(request.getCompanyName())
                .keySkills(request.getKeySkills())
                .relevantExperience(request.getRelevantExperience())
                .generatedContent(content)
                .build();
        return coverLetterRepository.save(letter);
    }

    public List<CoverLetter> listForUser(String userEmail) {
        var user = userService.findByEmail(userEmail);
        return coverLetterRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    public CoverLetter getForUser(String userEmail, String id) {
        var user = userService.findByEmail(userEmail);
        return coverLetterRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> AppException.notFound("Cover letter not found"));
    }

    public void delete(String userEmail, String id) {
        CoverLetter letter = getForUser(userEmail, id);
        coverLetterRepository.delete(letter);
    }
}

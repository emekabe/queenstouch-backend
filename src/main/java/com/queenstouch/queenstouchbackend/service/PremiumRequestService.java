package com.queenstouch.queenstouchbackend.service;

import com.queenstouch.queenstouchbackend.dto.request.CreatePremiumRequestDto;
import com.queenstouch.queenstouchbackend.exception.AppException;
import com.queenstouch.queenstouchbackend.model.PremiumServiceRequest;
import com.queenstouch.queenstouchbackend.model.enums.RequestStatus;
import com.queenstouch.queenstouchbackend.repository.PremiumServiceRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PremiumRequestService {

    private final PremiumServiceRequestRepository requestRepository;
    private final UserService userService;
    private final GoogleCloudStorageService googleCloudStorageService;

    public PremiumServiceRequest create(String userEmail, CreatePremiumRequestDto dto) {
        var user = userService.findByEmail(userEmail);
        PremiumServiceRequest req = PremiumServiceRequest.builder()
                .userId(user.getId())
                .serviceType(dto.getServiceType())
                .notes(dto.getNotes())
                .build();
        return requestRepository.save(req);
    }

    public PremiumServiceRequest addUploadedFile(String userEmail, String requestId, MultipartFile file) {
        PremiumServiceRequest req = getForUser(userEmail, requestId);
        String url = googleCloudStorageService.uploadFile(file);
        if (req.getUploadedFileUrls() == null) req.setUploadedFileUrls(new ArrayList<>());
        req.getUploadedFileUrls().add(url);
        req.setUpdatedAt(Instant.now());
        return requestRepository.save(req);
    }

    public List<PremiumServiceRequest> listForUser(String userEmail) {
        var user = userService.findByEmail(userEmail);
        return requestRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    public PremiumServiceRequest getForUser(String userEmail, String id) {
        var user = userService.findByEmail(userEmail);
        return requestRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> AppException.notFound("Premium request not found"));
    }

    /** Admin: update status */
    public PremiumServiceRequest updateStatus(String id, RequestStatus status, String adminNotes) {
        PremiumServiceRequest req = requestRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Premium request not found"));
        req.setStatus(status);
        if (adminNotes != null) req.setAdminNotes(adminNotes);
        req.setUpdatedAt(Instant.now());
        return requestRepository.save(req);
    }

    public List<PremiumServiceRequest> listAll() {
        return requestRepository.findAll();
    }

    /** Admin: Upload final CV back to user */
    public PremiumServiceRequest adminDeliverFile(String id, MultipartFile file, String adminNotes) {
        PremiumServiceRequest req = requestRepository.findById(id)
                .orElseThrow(() -> AppException.notFound("Premium request not found"));
        String url = googleCloudStorageService.uploadFile(file);
        if (req.getUploadedFileUrls() == null) req.setUploadedFileUrls(new ArrayList<>());
        req.getUploadedFileUrls().add(url);
        req.setStatus(RequestStatus.COMPLETED);
        if (adminNotes != null && !adminNotes.isBlank()) {
            req.setAdminNotes(adminNotes);
        }
        req.setUpdatedAt(Instant.now());
        return requestRepository.save(req);
    }
}

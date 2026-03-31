package com.queenstouch.queenstouchbackend.model;

import com.queenstouch.queenstouchbackend.model.enums.PremiumServiceType;
import com.queenstouch.queenstouchbackend.model.enums.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "premium_service_requests")
public class PremiumServiceRequest {

    @Id
    private String id;

    @Indexed
    private String userId;

    private PremiumServiceType serviceType;

    private String notes;

    @Builder.Default
    private List<String> uploadedFileUrls = new ArrayList<>();

    @Builder.Default
    private RequestStatus status = RequestStatus.PENDING;

    private String adminNotes;

    @Builder.Default
    @CreatedDate
    private Instant createdAt = Instant.now();

    @Builder.Default
    @LastModifiedDate
    private Instant updatedAt = Instant.now();
}

package com.queenstouch.queenstouchbackend.repository;

import com.queenstouch.queenstouchbackend.model.PremiumServiceRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PremiumServiceRequestRepository extends MongoRepository<PremiumServiceRequest, String> {
    List<PremiumServiceRequest> findByUserIdOrderByCreatedAtDesc(String userId);
    Optional<PremiumServiceRequest> findByIdAndUserId(String id, String userId);
}

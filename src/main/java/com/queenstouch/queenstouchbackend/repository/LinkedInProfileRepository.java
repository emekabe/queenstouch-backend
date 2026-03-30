package com.queenstouch.queenstouchbackend.repository;

import com.queenstouch.queenstouchbackend.model.LinkedInProfile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LinkedInProfileRepository extends MongoRepository<LinkedInProfile, String> {
    List<LinkedInProfile> findByUserIdOrderByCreatedAtDesc(String userId);
    Optional<LinkedInProfile> findByIdAndUserId(String id, String userId);
}

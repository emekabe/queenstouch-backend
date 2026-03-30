package com.queenstouch.queenstouchbackend.repository;

import com.queenstouch.queenstouchbackend.model.CoverLetter;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CoverLetterRepository extends MongoRepository<CoverLetter, String> {
    List<CoverLetter> findByUserIdOrderByCreatedAtDesc(String userId);
    Optional<CoverLetter> findByIdAndUserId(String id, String userId);
}

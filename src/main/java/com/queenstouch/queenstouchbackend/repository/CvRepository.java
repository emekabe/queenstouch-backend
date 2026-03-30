package com.queenstouch.queenstouchbackend.repository;

import com.queenstouch.queenstouchbackend.model.CvDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CvRepository extends MongoRepository<CvDocument, String> {

    List<CvDocument> findByUserIdOrderByCreatedAtDesc(String userId);

    Optional<CvDocument> findByIdAndUserId(String id, String userId);

    boolean existsByIdAndUserId(String id, String userId);

    long countByUserId(String userId);
}

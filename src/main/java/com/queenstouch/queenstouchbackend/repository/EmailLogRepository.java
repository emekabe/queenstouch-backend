package com.queenstouch.queenstouchbackend.repository;

import com.queenstouch.queenstouchbackend.model.EmailLog;
import com.queenstouch.queenstouchbackend.model.enums.EmailStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailLogRepository extends MongoRepository<EmailLog, String> {
    List<EmailLog> findByToAddress(String toAddress);
    List<EmailLog> findByStatus(EmailStatus status);
}

package com.queenstouch.queenstouchbackend.repository;

import com.queenstouch.queenstouchbackend.model.PricingConfig;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PricingRepository extends MongoRepository<PricingConfig, String> {
}

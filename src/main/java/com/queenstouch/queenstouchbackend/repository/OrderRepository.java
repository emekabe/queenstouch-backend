package com.queenstouch.queenstouchbackend.repository;

import com.queenstouch.queenstouchbackend.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    List<Order> findByUserIdOrderByCreatedAtDesc(String userId);
    boolean existsByRelatedDocumentIdAndStatusIn(String relatedDocumentId,
                                                  List<com.queenstouch.queenstouchbackend.model.enums.OrderStatus> statuses);
}

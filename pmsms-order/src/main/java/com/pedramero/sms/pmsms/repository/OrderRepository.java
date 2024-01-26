package com.pedramero.sms.pmsms.repository;

import com.pedramero.sms.pmsms.model.Order;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends MongoRepository<Order,String> {
    List<Order> findAllByOrderNumberContaining(Integer orderNumber);
}

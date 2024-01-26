package com.pedramero.sms.pmsms.repository;

import com.pedramero.sms.pmsms.model.ProductReturn;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductReturnRepository extends MongoRepository<ProductReturn, String> {
}

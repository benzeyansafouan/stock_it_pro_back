package com.pedramero.sms.pmsms.repository;

import com.pedramero.sms.pmsms.model.Product;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findAllByNameContainingIgnoreCase(String name);
}

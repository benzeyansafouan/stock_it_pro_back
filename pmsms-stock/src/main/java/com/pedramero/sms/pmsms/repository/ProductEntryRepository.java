package com.pedramero.sms.pmsms.repository;

import com.pedramero.sms.pmsms.model.ProductEntry;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductEntryRepository extends MongoRepository<ProductEntry, String> {
    List<ProductEntry> findByProductId(String productId);
}

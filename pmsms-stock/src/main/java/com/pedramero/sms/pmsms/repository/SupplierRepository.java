package com.pedramero.sms.pmsms.repository;


import com.pedramero.sms.pmsms.model.Supplier;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepository extends MongoRepository<Supplier, String> {
    List<Supplier> findAllByNameContainingIgnoreCase(String name);
}

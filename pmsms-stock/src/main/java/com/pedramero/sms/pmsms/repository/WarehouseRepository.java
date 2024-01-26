package com.pedramero.sms.pmsms.repository;

import com.pedramero.sms.pmsms.model.Warehouse;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WarehouseRepository extends MongoRepository<Warehouse,String> {
    List<Warehouse> findAllByNameContainingIgnoreCase(String name);
}

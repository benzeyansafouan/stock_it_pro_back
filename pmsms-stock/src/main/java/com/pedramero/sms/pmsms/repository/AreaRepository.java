package com.pedramero.sms.pmsms.repository;

import com.pedramero.sms.pmsms.model.Area;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AreaRepository extends MongoRepository<Area, String> {

    List<Area> findAllByNameContainingIgnoreCase(String name);
    List<Area> findAllByNameContainingIgnoreCaseAndWarehouseId(String name,String warehouseId);
    List<Area> findAllByWarehouseId(String warehouseId);
}

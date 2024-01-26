package com.pedramero.sms.pmsms.service;


import com.pedramero.sms.pmsms.exception.WarehouseNotFoundException;
import com.pedramero.sms.pmsms.model.Warehouse;
import com.pedramero.sms.pmsms.repository.WarehouseRepository;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class WarehouseService {

    @Autowired
    WarehouseRepository warehouseRepository;

    public Page<Warehouse> getAllByPage(Pageable pageable){
        return warehouseRepository.findAll(pageable);
    }

    public List<Warehouse> getAll() {
        return StreamSupport.stream(warehouseRepository.findAll().spliterator(), false)
            .collect(Collectors.toList());
    }

    public List<Warehouse> search(String term){
        return warehouseRepository.findAllByNameContainingIgnoreCase(term);
    }

    public Warehouse save(Warehouse warehouse){
        warehouse.setId(new ObjectId().toString());
        return warehouseRepository.save(warehouse);
    }

    public Warehouse update(Warehouse warehouse) throws WarehouseNotFoundException {
        var warehouseToUpdate = warehouseRepository.findById(warehouse.getId()).orElseThrow(
            WarehouseNotFoundException::new);
        warehouseToUpdate.setAddress(warehouse.getAddress());
        warehouseToUpdate.setName(warehouse.getName());
        warehouseToUpdate.setNote(warehouse.getNote());
        return warehouseRepository.save(warehouseToUpdate);
    }

    public Warehouse findById(String id) throws WarehouseNotFoundException {
        var warehouse = warehouseRepository.findById(id).orElseThrow(WarehouseNotFoundException::new);
        return warehouse;
    }

    public void delete(String id) throws WarehouseNotFoundException {
        var warehouse = warehouseRepository.findById(id).orElseThrow(WarehouseNotFoundException::new);
        warehouseRepository.delete(warehouse);
    }
}

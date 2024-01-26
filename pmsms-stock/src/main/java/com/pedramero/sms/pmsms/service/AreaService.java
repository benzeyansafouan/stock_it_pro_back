package com.pedramero.sms.pmsms.service;

import com.pedramero.sms.pmsms.exception.AreaNotFoundException;
import com.pedramero.sms.pmsms.model.Area;
import com.pedramero.sms.pmsms.repository.AreaRepository;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AreaService {

    @Autowired
    AreaRepository areaRepository;

    public Page<Area> getAllByPage(Pageable pageable){
        return areaRepository.findAll(pageable);
    }

    public List<Area> getAll() {
        return StreamSupport.stream(areaRepository.findAll().spliterator(), false)
            .collect(Collectors.toList());
    }

    public List<Area> search(String term, String selectedWarehouse){
        if (selectedWarehouse == null || selectedWarehouse.isEmpty()){
            return areaRepository.findAllByNameContainingIgnoreCase(term);
        }
        return areaRepository.findAllByNameContainingIgnoreCaseAndWarehouseId(term,selectedWarehouse);
    }

    public Area save(Area area){
        area.setId(new ObjectId().toString());
        return areaRepository.save(area);
    }

    public Area update(Area area) throws AreaNotFoundException {
        var areaToUpdate = areaRepository.findById(area.getId()).orElseThrow(
            AreaNotFoundException::new);
        areaToUpdate.setName(area.getName());
        areaToUpdate.setOrdinalNumber(area.getOrdinalNumber());
        areaToUpdate.setWarehouseId(area.getWarehouseId());
        return areaRepository.save(areaToUpdate);
    }

    public Area findById(String id) throws AreaNotFoundException {
        var area = areaRepository.findById(id).orElseThrow(AreaNotFoundException::new);
        return area;
    }

    public void delete(String id) throws AreaNotFoundException {
        var area = areaRepository.findById(id).orElseThrow(AreaNotFoundException::new);
        areaRepository.delete(area);
    }

    public List<Area> findByWarehouseId(String warehouseId){
       return areaRepository.findAllByWarehouseId(warehouseId);
    }
}

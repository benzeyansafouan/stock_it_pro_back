package com.pedramero.sms.pmsms.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.pedramero.sms.pmsms.config.Logger;
import com.pedramero.sms.pmsms.exception.WarehouseNotFoundException;
import com.pedramero.sms.pmsms.model.Warehouse;
import com.pedramero.sms.pmsms.model.dto.AreaDto;
import com.pedramero.sms.pmsms.model.dto.WarehouseDto;
import com.pedramero.sms.pmsms.service.AreaService;
import com.pedramero.sms.pmsms.service.WarehouseService;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/warehouse")
public class WarehouseController implements Logger {

    @Autowired
    WarehouseService warehouseService;
    @Autowired
    AreaService areaService;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    ObjectMapper objectMapper;

    @GetMapping("/all/paging")
    public ResponseEntity<Page<WarehouseDto>> getAllByPage(@RequestParam int page,
                                                          @RequestParam int pageSize) {
        var pageable = (page == -1 || pageSize == -1) ? Pageable.unpaged() :
            PageRequest.of(page, pageSize, Sort.by("name"));
        var warehousePage = warehouseService.getAllByPage(pageable);
        var warehouseDtos =
            warehousePage.map(warehouse -> {
                   var warehouseDto = modelMapper.map(warehouse, WarehouseDto.class);
                   var warehouseAreas = areaService.findByWarehouseId(warehouseDto.getId())
                       .stream().map(area-> modelMapper.map(area,AreaDto.class))
                       .collect(Collectors.toList());
                   warehouseDto.setAreas(warehouseAreas);
                   return warehouseDto;
                }).stream().collect(Collectors.toList());
        return ResponseEntity.ok(
            new PageImpl<>(warehouseDtos, pageable, warehousePage.getTotalElements()));
    }


    @GetMapping("/all")
    public ResponseEntity<List<WarehouseDto>> getAll() {
        var warehouses = warehouseService.getAll();
        return getListResponseEntity(warehouses);
    }

    @GetMapping("/search/{term}")
    public ResponseEntity<List<WarehouseDto>> search(@PathVariable String term) {
        var foundWarehouses = warehouseService.search(term);
        return getListResponseEntity(foundWarehouses);
    }



    @PostMapping("/save")
    public ResponseEntity<WarehouseDto> save(@RequestParam String warehouse){
        try {
            var warehouseObject = objectMapper.readValue(warehouse,WarehouseDto.class);
            var newWarehouse = new Warehouse();
            modelMapper.map(warehouseObject,newWarehouse);
            var savedWarehouse = warehouseService.save(newWarehouse);
            return getWarehouseDtoResponseEntity(savedWarehouse);
        }catch (IOException e){
            getLogger().error("ERROR WHILE SAVING WAREHOUSE{}", warehouse, e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<WarehouseDto> get(@PathVariable("id") String id){
        try {
            var warehouse = warehouseService.findById(id);
            return getWarehouseDtoResponseEntity(warehouse);
        } catch (WarehouseNotFoundException exception){
            getLogger().warn("WAREHOUSE NOT FOUND EXCEPTION {}", id, exception);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/update")
    public ResponseEntity<WarehouseDto> update(@RequestParam String warehouse){
        try {
            var warehouseObject = objectMapper.readValue(warehouse,WarehouseDto.class);
            var newWarehouse = new Warehouse();
            modelMapper.map(warehouseObject,newWarehouse);
            var updatedWarehouse = warehouseService.update(newWarehouse);
            return getWarehouseDtoResponseEntity(updatedWarehouse);
        } catch (WarehouseNotFoundException | IOException exception){
            getLogger().warn("ERROR WHILE UPDATING WAREHOUSE {}",warehouse, exception);
            return ResponseEntity.badRequest().build();
        }
    }
    @DeleteMapping("/delete/{id}")
    public HttpStatus delete(@PathVariable String id){
        try {
            warehouseService.delete(id);
            return HttpStatus.OK;
        } catch (WarehouseNotFoundException e){
            getLogger().warn("ERROR WHILE DELETING WAREHOUSE {}", id,e);
            return HttpStatus.BAD_REQUEST;
        }
    }
    private ResponseEntity<List<WarehouseDto>> getListResponseEntity(
        List<Warehouse> foundWarehouses) {
        var foundWarehouseDtos =
            foundWarehouses.stream().map(warehouse -> {
                var warehouseDto = modelMapper.map(warehouse, WarehouseDto.class);
                var warehouseAreas = areaService.findByWarehouseId(warehouseDto.getId())
                    .stream().map(area-> modelMapper.map(area, AreaDto.class))
                    .collect(Collectors.toList());
                warehouseDto.setAreas(warehouseAreas);
                return warehouseDto;
            }).collect(Collectors.toList());
        return ResponseEntity.ok(foundWarehouseDtos);
    }

    private ResponseEntity<WarehouseDto> getWarehouseDtoResponseEntity(Warehouse savedWarehouse) {
        var savedWarehouseDto = modelMapper.map(savedWarehouse,WarehouseDto.class);
        var warehouseAreas = areaService.findByWarehouseId(savedWarehouseDto.getId())
            .stream().map(area-> modelMapper.map(area,AreaDto.class))
            .collect(Collectors.toList());
        savedWarehouseDto.setAreas(warehouseAreas);
        return ResponseEntity.ok(savedWarehouseDto);
    }
}

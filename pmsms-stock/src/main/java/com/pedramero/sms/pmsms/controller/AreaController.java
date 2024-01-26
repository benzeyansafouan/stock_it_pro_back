package com.pedramero.sms.pmsms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pedramero.sms.pmsms.config.Logger;
import com.pedramero.sms.pmsms.exception.AreaNotFoundException;
import com.pedramero.sms.pmsms.model.Area;
import com.pedramero.sms.pmsms.model.dto.AreaDto;
import com.pedramero.sms.pmsms.service.AreaService;
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
@RequestMapping("/area")
public class AreaController implements Logger {

    @Autowired
    AreaService areaService;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    ObjectMapper objectMapper;

    @GetMapping("/all/paging")
    public ResponseEntity<Page<AreaDto>> getAllByPage(@RequestParam int page,
                                                      @RequestParam int pageSize) {
        var pageable = (page == -1 || pageSize == -1) ? Pageable.unpaged() :
            PageRequest.of(page, pageSize, Sort.by("name"));
        var areaPage = areaService.getAllByPage(pageable);
        var areaDtoList =
            areaPage.map(area -> modelMapper.map(area, AreaDto.class)).stream()
                .collect(
                    Collectors.toList());
        return ResponseEntity.ok(
            new PageImpl<>(areaDtoList, pageable, areaPage.getTotalElements()));
    }


    @GetMapping("/all")
    public ResponseEntity<List<AreaDto>> getAll() {
        var areas = areaService.getAll();
        var areaDtoList =
            areas.stream().map(area -> modelMapper.map(area, AreaDto.class))
                .collect(
                    Collectors.toList());
        return ResponseEntity.ok(areaDtoList);
    }

    @GetMapping("/search")
    public ResponseEntity<List<AreaDto>> search(@RequestParam(required = false) String terms,
                                                @RequestParam(required = false) String selectedWarehouse) {
        var foundAreas = areaService.search(terms,selectedWarehouse);
        var foundAreaDtos =
            foundAreas.stream().map(area -> modelMapper.map(area, AreaDto.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(foundAreaDtos);
    }

    @PostMapping("/save")
    public ResponseEntity<AreaDto> save(@RequestParam String area){
        try {
            var areaObject = objectMapper.readValue(area,AreaDto.class);
            var newArea = new Area();
            modelMapper.map(areaObject,newArea);
            var savedArea = areaService.save(newArea);
            var savedAreaDto = modelMapper.map(savedArea,AreaDto.class);
            return ResponseEntity.ok(savedAreaDto);
        }catch (IOException e){
            getLogger().error("ERROR WHILE SAVING AREA{}", area, e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<AreaDto> get(@PathVariable("id") String id){
        try {
            var area = areaService.findById(id);
            var areaDto = modelMapper.map(area,AreaDto.class);
            return ResponseEntity.ok(areaDto);
        } catch (AreaNotFoundException exception){
            getLogger().warn("AREA NOT FOUND EXCEPTION {}", id, exception);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/update")
    public ResponseEntity<AreaDto> update(@RequestParam String area){
        try {
            var areaObject = objectMapper.readValue(area,AreaDto.class);
            var newArea = new Area();
            modelMapper.map(areaObject,newArea);
            var updatedArea = areaService.update(newArea);
            var areaDto = modelMapper.map(updatedArea,AreaDto.class);
            return ResponseEntity.ok(areaDto);
        } catch (AreaNotFoundException | IOException exception){
            getLogger().warn("ERROR WHILE UPDATING AREA{}",area, exception);
            return ResponseEntity.badRequest().build();
        }
    }
    @DeleteMapping("/delete/{id}")
    public HttpStatus delete(@PathVariable String id){
        try {
            areaService.delete(id);
            return HttpStatus.OK;
        } catch (AreaNotFoundException e){
            getLogger().warn("ERROR WHILE DELETING AREA {}",id, e);
            return HttpStatus.BAD_REQUEST;
        }
    }
    @GetMapping("warehouse/{warehouseId}")
    public ResponseEntity<List<AreaDto>> getAllByWarehouseId(@PathVariable String warehouseId){
        var areas = areaService.findByWarehouseId(warehouseId);
        var areaDtos = areas.stream().map(area -> modelMapper.map(area, AreaDto.class))
            .collect(
                Collectors.toList());
        return ResponseEntity.ok(areaDtos);
    }

}

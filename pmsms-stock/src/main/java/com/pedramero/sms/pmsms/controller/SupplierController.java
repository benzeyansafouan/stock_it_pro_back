package com.pedramero.sms.pmsms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pedramero.sms.pmsms.config.Logger;
import com.pedramero.sms.pmsms.exception.SupplierNotFoundException;
import com.pedramero.sms.pmsms.model.Supplier;
import com.pedramero.sms.pmsms.model.dto.SupplierDto;
import com.pedramero.sms.pmsms.service.SupplierService;
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
@RequestMapping("/supplier")
public class SupplierController implements Logger {

    @Autowired
    SupplierService supplierService;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    ObjectMapper objectMapper;

    @GetMapping("/all/paging")
    public ResponseEntity<Page<SupplierDto>> getAllByPage(@RequestParam int page,
                                                          @RequestParam int pageSize) {
        var pageable = (page == -1 || pageSize == -1) ? Pageable.unpaged() :
            PageRequest.of(page, pageSize, Sort.by("name"));
        var supplierPage = supplierService.getAllByPage(pageable);
        var suppliers =
            supplierPage.map(supplier -> modelMapper.map(supplier, SupplierDto.class)).stream()
                .collect(
                    Collectors.toList());
        return ResponseEntity.ok(
            new PageImpl<>(suppliers, pageable, supplierPage.getTotalElements()));
    }

    @GetMapping("/all")
    public ResponseEntity<List<SupplierDto>> getAll() {
        var suppliers = supplierService.getAll();
        var supplierDtos =
            suppliers.stream().map(supplier -> modelMapper.map(supplier, SupplierDto.class))
                .collect(
                    Collectors.toList());
        return ResponseEntity.ok(supplierDtos);
    }

    @GetMapping("/search/{term}")
    public ResponseEntity<List<SupplierDto>> search(@PathVariable String term) {
        var foundSuppliers = supplierService.search(term);
        var foundSupplierDtos =
            foundSuppliers.stream().map(supplier -> modelMapper.map(supplier, SupplierDto.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(foundSupplierDtos);
    }

    @PostMapping("/save")
    public ResponseEntity<SupplierDto> save(@RequestParam String supplier){
        try {
            var supplierObject = objectMapper.readValue(supplier,SupplierDto.class);
            var newSupplier = new Supplier();
            modelMapper.map(supplierObject,newSupplier);
            var savedSupplier = supplierService.save(newSupplier);
            var savedSupplierDto = modelMapper.map(savedSupplier,SupplierDto.class);
            return ResponseEntity.ok(savedSupplierDto);
        }catch (IOException e){
            getLogger().error("ERROR WHILE SAVING SUPPLIER{}", supplier, e);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplierDto> get(@PathVariable("id") String id){
        try {
            var supplier = supplierService.findById(id);
            var supplierDto = modelMapper.map(supplier,SupplierDto.class);
            return ResponseEntity.ok(supplierDto);
        } catch (SupplierNotFoundException exception){
            getLogger().warn("SUPPLIER NOT FOUND EXCEPTION {}", id, exception);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/update")
    public ResponseEntity<SupplierDto> update(@RequestParam String supplier){
        try {
            var supplierObject = objectMapper.readValue(supplier,SupplierDto.class);
            var newSupplier = new Supplier();
            modelMapper.map(supplierObject,newSupplier);
            var updatedSupplier = supplierService.update(newSupplier);
            var supplierDto = modelMapper.map(updatedSupplier,SupplierDto.class);
            return ResponseEntity.ok(supplierDto);
        } catch (SupplierNotFoundException | IOException exception){
            getLogger().warn("ERROR WHILE UPDATING SUPPLIER {}",supplier, exception);
            return ResponseEntity.badRequest().build();
        }
    }
    @DeleteMapping("/delete/{id}")
    public HttpStatus delete(@PathVariable String id){
        try {
            supplierService.delete(id);
            return HttpStatus.OK;
        } catch (SupplierNotFoundException e){
            getLogger().warn("ERROR WHILE DELETING SUPPLIER {}",id, e);
            return HttpStatus.BAD_REQUEST;
        }
    }

}

package com.pedramero.sms.pmsms.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.pedramero.sms.pmsms.config.Logger;
import com.pedramero.sms.pmsms.exception.ProductEntryNotFoundException;
import com.pedramero.sms.pmsms.model.ProductEntry;
import com.pedramero.sms.pmsms.model.dto.ProductDto;
import com.pedramero.sms.pmsms.model.dto.ProductEntryDto;
import com.pedramero.sms.pmsms.service.ProductEntryService;
import com.pedramero.sms.pmsms.service.ProductService;
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
@RequestMapping("product-entry")
public class ProductEntryController implements Logger {

    @Autowired
    ProductEntryService productEntryService;
    @Autowired
    ProductService productService;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    ObjectMapper objectMapper;


    @GetMapping("/all/page")
    public ResponseEntity<Page<ProductEntryDto>> getAllByPage(@RequestParam int page,
                                                              @RequestParam int pageSize) {
        var pageable = (page == -1 || pageSize == -1) ? Pageable.unpaged() :
            PageRequest.of(page, pageSize, Sort.by("name"));

        var productEntryPage = productEntryService.getAllByPage(pageable);
        var productEntries = productEntryPage.stream()
            .map(productEntry -> modelMapper.map(productEntry, ProductEntryDto.class))
            .collect(Collectors.toList());
        return ResponseEntity.ok(
            new PageImpl<>(productEntries, pageable, productEntryPage.getTotalElements()));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProductEntryDto>> getAll() {
        var productEntries = productEntryService.getAll();
        var productEntryDtos =
            productEntries.stream()
                .map(productEntry -> modelMapper.map(productEntry, ProductEntryDto.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(productEntryDtos);
    }

    @GetMapping("/search/{term}")
    public ResponseEntity<List<ProductDto>> search(@PathVariable String term) {
        var foundProduct = productService.searchProductsByName(term);
        if (!foundProduct.isEmpty()){
            var productId = foundProduct.get(0).getId();
        var productEntries = productEntryService.searchProductsById(productId);
        var productEntryDtos = productEntries.stream()
            .map(productEntry -> modelMapper.map(productEntry, ProductDto.class))
            .collect(Collectors.toList());
        return ResponseEntity.ok(productEntryDtos);
        }
        return ResponseEntity.unprocessableEntity().build();
    }

    @PostMapping("/save")
    public ResponseEntity<ProductEntryDto> save(@RequestParam String productEntry) {
        try {
            var newProductEntry = new ProductEntry();
            var productEntryObject = objectMapper.readValue(productEntry, ProductEntryDto.class);
            modelMapper.map(productEntryObject,newProductEntry);
            var savedProductEntry = productEntryService.save(newProductEntry);
            var productEntryDto = modelMapper.map(savedProductEntry, ProductEntryDto.class);
            return ResponseEntity.ok(productEntryDto);
        } catch (IOException exception) {
            getLogger().error("ERROR WHILE SAVING PRODUCT ENTRY{}", productEntry, exception);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductEntryDto> get(@PathVariable("id") String id) {
        try {
            var productEntry = productEntryService.findById(id);
            var productEntryDto = modelMapper.map(productEntry, ProductEntryDto.class);
            return ResponseEntity.ok(productEntryDto);
        } catch (ProductEntryNotFoundException e) {
            getLogger().warn("PRODUCT ENTRY NOT FOUND EXCEPTION {}", id, e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/update")
    public ResponseEntity<ProductEntryDto> update(@RequestParam String productEntry) {
        try {
            var newProductEntry = new ProductEntry();
            var productEntryObject = objectMapper.readValue(productEntry, ProductEntryDto.class);
            modelMapper.map(productEntryObject, newProductEntry);
            var updatedProductEntry = productEntryService.update(newProductEntry);
            var productEntryDto = modelMapper.map(updatedProductEntry, ProductEntryDto.class);
            return ResponseEntity.ok(productEntryDto);
        } catch (ProductEntryNotFoundException | IOException exception) {
            getLogger().warn("ERROR WHILE UPDATING PRODUCT ENTRY {}",productEntry, exception);
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public HttpStatus delete(@PathVariable String id) {
        try {
            productEntryService.delete(id);
            return HttpStatus.OK;
        } catch (ProductEntryNotFoundException e) {
            getLogger().warn("ERROR WHILE DELETING PRODUCT ENTRY {}",id, e);
            return HttpStatus.BAD_REQUEST;
        }
    }
}

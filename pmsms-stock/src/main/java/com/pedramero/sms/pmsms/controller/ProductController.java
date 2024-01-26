package com.pedramero.sms.pmsms.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.pedramero.sms.pmsms.config.Logger;
import com.pedramero.sms.pmsms.exception.ProductNotFoundException;
import com.pedramero.sms.pmsms.model.ChangeAction;
import com.pedramero.sms.pmsms.model.Product;
import com.pedramero.sms.pmsms.model.dto.ProductDto;
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
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("product")
public class ProductController implements Logger {

    @Autowired
    ProductService productService;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    ObjectMapper objectMapper;


    @GetMapping("/all/paging")
    public ResponseEntity<Page<ProductDto>> getAllByPage(@RequestParam int page,
                                                         @RequestParam int pageSize) {
        var pageable = (page == -1 || pageSize == -1) ? Pageable.unpaged() :
            PageRequest.of(page, pageSize, Sort.by("name"));

        var productPage = productService.getAllByPage(pageable);
        var products = productPage.stream().map(product -> {
            var productDto = modelMapper.map(product, ProductDto.class);
            retrieveImage(product, productDto);
            return productDto;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(
            new PageImpl<>(products, pageable, productPage.getTotalElements()));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProductDto>> getAll() {
        var products = productService.getAll();
        var productDtos = products.stream().map(product -> {
            var productDto = modelMapper.map(product, ProductDto.class);
            retrieveImage(product, productDto);
            return productDto;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(productDtos);
    }

    @GetMapping("/search/{term}")
    public ResponseEntity<List<ProductDto>> search(@PathVariable String term) {
        var products = productService.searchProductsByName(term);
        var productsDtos = products.stream()
            .map(product -> {
                var productDto = modelMapper.map(product, ProductDto.class);
                retrieveImage(product, productDto);
                return productDto;
            }).collect(Collectors.toList());
        return ResponseEntity.ok(productsDtos);
    }

    @PostMapping("/save")
    public ResponseEntity<ProductDto> save(@RequestParam String product,
                                            @RequestParam(required = false)
                                                MultipartFile productImage) {
        try {
            var newProduct = new Product();
            var productObject = objectMapper.readValue(product, ProductDto.class);
            modelMapper.map(productObject,newProduct);
            var savedProduct = productService.save(newProduct, productImage);
            var productDto = modelMapper.map(savedProduct, ProductDto.class);
            retrieveImage(savedProduct, productDto);
            return ResponseEntity.ok(productDto);
        } catch (IOException exception) {
            getLogger().error("ERROR WHILE SAVING PRODUCT{}", product, exception);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> get(@PathVariable("id") String id) {
        try {
            var product = productService.findById(id);
            var productDto = modelMapper.map(product,ProductDto.class);
            retrieveImage(product,productDto);
            return ResponseEntity.ok(productDto);
        } catch (ProductNotFoundException e){
            getLogger().warn("PRODUCT NOT FOUND EXCEPTION {}",id,e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/update")
    public ResponseEntity<ProductDto> update(@RequestParam String product,
                                                      @RequestParam(required = false)
                                                          MultipartFile multipartFile,
                                                      @RequestParam() ChangeAction action) {
        try {
            var newProduct = new Product();
            var productObject = objectMapper.readValue(product, ProductDto.class);
            modelMapper.map(productObject, newProduct);
            var updatedProduct = productService.updateProduct(newProduct, multipartFile,action);
            var productDto = modelMapper.map(updatedProduct, ProductDto.class);
            retrieveImage(updatedProduct, productDto);
            return ResponseEntity.ok(productDto);
        } catch (ProductNotFoundException | IOException exception) {
            getLogger().warn("ERROR WHILE UPDATING PRODUCT {}",product,exception);
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public HttpStatus delete(@PathVariable String id) {
        try {
            productService.delete(id);
            return HttpStatus.OK;
        } catch (ProductNotFoundException e) {
            getLogger().warn("ERROR WHILE DELETING PRODUCT {}",id,e);
            return HttpStatus.BAD_REQUEST;
        }
    }

    private void retrieveImage(Product product, ProductDto productDto) {
        if (product.getImageFileObjectId() != null) {
            var resource = productService.getProductImageById(product.getImageFileObjectId());
            try {
                productDto.setProductImage(resource.getInputStream().readAllBytes());
            } catch (IOException e) {
                getLogger().warn("ERROR WHILE RETRIEVING IMAGE FOR PRODUCT {}", productDto, e);
            }
        }
    }

}

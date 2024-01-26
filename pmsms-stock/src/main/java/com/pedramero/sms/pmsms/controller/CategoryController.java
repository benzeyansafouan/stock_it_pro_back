package com.pedramero.sms.pmsms.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.pedramero.sms.pmsms.config.Logger;
import com.pedramero.sms.pmsms.exception.CategoryNotFoundException;
import com.pedramero.sms.pmsms.model.Category;
import com.pedramero.sms.pmsms.model.ChangeAction;
import com.pedramero.sms.pmsms.model.dto.CategoryDto;
import com.pedramero.sms.pmsms.service.CategoryService;
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
@RequestMapping("/category")
public class CategoryController implements Logger {

    @Autowired
    CategoryService categoryService;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    ObjectMapper objectMapper;

    @GetMapping("/all/paging")
    public ResponseEntity<Page<CategoryDto>> getAllByPage(@RequestParam int page,
                                                          @RequestParam int pageSize) {
            var pageable = (page == -1 || pageSize == -1) ? Pageable.unpaged() :
                PageRequest.of(page, pageSize, Sort.by("name"));

            var categoryPage = categoryService.getAllByPage(pageable);
            var categories = categoryPage.stream().map(category -> {
                var categoryDto = modelMapper.map(category, CategoryDto.class);
                retrieveImage(category, categoryDto);
                return categoryDto;
            }).collect(Collectors.toList());
            return ResponseEntity.ok(
                new PageImpl<>(categories, pageable, categoryPage.getTotalElements()));

    }

    @GetMapping("/all")
    public ResponseEntity<List<CategoryDto>> getAll() {
        var categories = categoryService.getAll();
        var categorieDtos = categories.stream().map(category -> {
            var categoryDto = modelMapper.map(category, CategoryDto.class);
            retrieveImage(category, categoryDto);
            return categoryDto;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(categorieDtos);
    }

    @GetMapping("/all-without-images")
    public ResponseEntity<List<CategoryDto>> getAllWithoutImages() {
        var categories = categoryService.getAll();
        var categoryDtoList = categories.stream().map(category -> {
            var categoryDto = modelMapper.map(category, CategoryDto.class);
            categoryDto.setCategoryImage(null);
            return categoryDto;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(categoryDtoList);
    }


    @GetMapping("/search/{term}")
    public ResponseEntity<List<CategoryDto>> search(@PathVariable String term) {
        var categories = categoryService.searchCategoriesByName(term);
        var categoryDtos = categories.stream()
            .map(category -> {
                var categoryDto = modelMapper.map(category, CategoryDto.class);
                retrieveImage(category, categoryDto);
                return categoryDto;
            }).collect(Collectors.toList());
        return ResponseEntity.ok(categoryDtos);
    }

    @PostMapping("/save")
    public ResponseEntity<CategoryDto> save(@RequestParam String category,
                                            @RequestParam(required = false)
                                                MultipartFile categoryImage) {
        try {
            var newCategory = new Category();
            var categoryObject = objectMapper.readValue(category, Category.class);
            modelMapper.map(categoryObject, newCategory);
            var savedCategory = categoryService.save(newCategory, categoryImage);
            var categoryDto = modelMapper.map(savedCategory, CategoryDto.class);
            retrieveImage(savedCategory, categoryDto);
            return ResponseEntity.ok(categoryDto);
        } catch (IOException exception) {
            getLogger().error("ERROR WHILE SAVING CATEGORY {}", category, exception);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> get(@PathVariable("id") String id) {
        try {
            var category = categoryService.findById(id);
            var categoryDto = modelMapper.map(category,CategoryDto.class);
            retrieveImage(category,categoryDto);
            return ResponseEntity.ok(categoryDto);
        } catch (CategoryNotFoundException e){
            getLogger().warn("CATEGORY NOT FOUND EXCEPTION {}",id,e);
            return ResponseEntity.badRequest().build();
        }
    }


    @PutMapping("/update")
    public ResponseEntity<CategoryDto> update(@RequestParam String category,
                                                      @RequestParam(required = false)
                                                          MultipartFile categoryImage,
                                                      @RequestParam() ChangeAction action) {
        try {
            var newCategory = new Category();
            var categoryObject = objectMapper.readValue(category, CategoryDto.class);
            modelMapper.map(categoryObject, newCategory);
            var updatedCategory = categoryService.updateCategory(newCategory, categoryImage,action);
            var categoryDto = modelMapper.map(updatedCategory, CategoryDto.class);
            retrieveImage(updatedCategory, categoryDto);
            return ResponseEntity.ok(categoryDto);
        } catch (CategoryNotFoundException | IOException exception) {
            getLogger().warn("ERROR WHILE UPDATING CATEGORY {}",category,exception);
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public HttpStatus deleteCategory(@PathVariable String id) {
        try {
            categoryService.delete(id);
            return HttpStatus.OK;
        } catch (CategoryNotFoundException e) {
            getLogger().warn("ERROR WHILE DELETING PRODUCT {}",id,e);
            return HttpStatus.BAD_REQUEST;
        }
    }



    private void retrieveImage(Category category, CategoryDto categoryDto) {
        if (category.getImageFileObjectId() != null) {
            var resource = categoryService.getCategoryImageById(category.getImageFileObjectId());
            try {
                categoryDto.setCategoryImage(resource.getInputStream().readAllBytes());
            } catch (IOException e) {
                getLogger().warn("ERROR WHILE RETRIEVING IMAGE FOR CATEGORY {}", categoryDto, e);
            }
        }
    }

}

package com.pedramero.sms.pmsms.service;

import com.pedramero.sms.pmsms.exception.CategoryNotFoundException;
import com.pedramero.sms.pmsms.model.Category;
import com.pedramero.sms.pmsms.model.ChangeAction;
import com.pedramero.sms.pmsms.repository.CategoryRepository;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.imageio.ImageIO;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CategoryService {

    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    GridFsService gridFsService;

    public Page<Category> getAllByPage(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    public List<Category> getAll() {
        return StreamSupport.stream(categoryRepository.findAll().spliterator(), false)
            .collect(Collectors.toList());
    }

    public Resource getCategoryImageById(String objectId) {
        return gridFsService.getFile(objectId);
    }

    public List<Category> searchCategoriesByName(String term) {
        return this.categoryRepository.findAllByNameContainingIgnoreCase(term);
    }

    public Category save(Category category, MultipartFile categoryImage) throws IOException {
        if (categoryImage != null) {
            setImageToCategory(categoryImage, category);
        }
        category.setId(new ObjectId().toString());
        categoryRepository.save(category);
        return category;
    }

    public Category findById(String id) throws CategoryNotFoundException {
        var category = categoryRepository.findById(id).orElseThrow(CategoryNotFoundException::new);
        return category;
    }

    public Category updateCategory(Category category, MultipartFile multipartFile,
                                   ChangeAction action)
        throws CategoryNotFoundException, IOException {
        var categoryToUpdate = this.categoryRepository.findById(category.getId())
            .orElseThrow(CategoryNotFoundException::new);
        switch (action) {
            case NEW:
                setImageToCategory(multipartFile, category);
                break;
            case DELETED:
                category.setImageFileObjectId(null);
                break;
            default:
            case NOT_CHANGED:
                category.setImageFileObjectId(categoryToUpdate.getImageFileObjectId());
        }
        return categoryRepository.save(category);
    }

    public void delete(String id) throws CategoryNotFoundException {
        var category = categoryRepository.findById(id).orElseThrow(CategoryNotFoundException::new);
        categoryRepository.delete(category);
    }

    private void setImageToCategory(MultipartFile multipartFile, Category category)
        throws IOException {
        var original =
            ImageIO.read(gridFsService.convertMultiPartToFile(multipartFile));
        var imageFileObjectId =
            gridFsService.saveFile(
                gridFsService.getResource(original, multipartFile),
                category.getId(),
                category.getName());
        category.setImageFileObjectId(imageFileObjectId);
    }
}

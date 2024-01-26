package com.pedramero.sms.pmsms.service;


import com.pedramero.sms.pmsms.exception.ProductNotFoundException;
import com.pedramero.sms.pmsms.model.ChangeAction;
import com.pedramero.sms.pmsms.model.Product;
import com.pedramero.sms.pmsms.repository.ProductRepository;
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
public class ProductService {

    @Autowired
    ProductRepository productRepository;
    @Autowired
    GridFsService gridFsService;

    public Page<Product> getAllByPage(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Resource getProductImageById(String objectId) {
        return gridFsService.getFile(objectId);
    }

    public List<Product> getAll() {
        return StreamSupport.stream(productRepository.findAll().spliterator(), false)
            .collect(Collectors.toList());
    }
    public List<Product> searchProductsByName(String term) {
        return productRepository.findAllByNameContainingIgnoreCase(term);
    }

    public Product save(Product product, MultipartFile productImage) throws IOException {
        if (productImage != null) {
            setImageToProduct(productImage, product);
        }
        product.setId(new ObjectId().toString());
        productRepository.save(product);
        return product;
    }

    public Product updateProduct(Product product, MultipartFile productImage,
                                   ChangeAction action)
        throws ProductNotFoundException, IOException {
        var productToUpdate = productRepository.findById(product.getId())
            .orElseThrow(ProductNotFoundException::new);
        switch (action) {
            case NEW:
                setImageToProduct(productImage, product);
                break;
            case DELETED:
                product.setImageFileObjectId(null);
                break;
            default:
            case NOT_CHANGED:
                product.setImageFileObjectId(productToUpdate.getImageFileObjectId());
        }
        return productRepository.save(product);
    }

    public Product findById(String id) throws ProductNotFoundException {
        var product = productRepository.findById(id).orElseThrow(ProductNotFoundException::new);
        return product;
    }
    public void delete(String id) throws ProductNotFoundException {
        var product = productRepository.findById(id).orElseThrow(ProductNotFoundException::new);
        productRepository.delete(product);
    }

    private void setImageToProduct(MultipartFile productImage, Product product)
        throws IOException {
        var original =
            ImageIO.read(gridFsService.convertMultiPartToFile(productImage));
        var imageFileObjectId =
            gridFsService.saveFile(
                gridFsService.getResource(original, productImage),
                product.getId(),
                product.getName());
        product.setImageFileObjectId(imageFileObjectId);
    }
}

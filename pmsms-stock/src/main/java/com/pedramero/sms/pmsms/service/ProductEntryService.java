package com.pedramero.sms.pmsms.service;

import com.pedramero.sms.pmsms.exception.ProductEntryNotFoundException;
import com.pedramero.sms.pmsms.model.ProductEntry;
import com.pedramero.sms.pmsms.repository.ProductEntryRepository;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductEntryService {

    @Autowired
    ProductEntryRepository productEntryRepository;


    public Page<ProductEntry> getAllByPage(Pageable pageable) {
        return productEntryRepository.findAll(pageable);
    }

    public List<ProductEntry> getAll() {
        return StreamSupport.stream(productEntryRepository.findAll().spliterator(), false)
            .collect(Collectors.toList());
    }

    public List<ProductEntry> searchProductsById(String productId) {
        return productEntryRepository.findByProductId(productId);
    }

    public ProductEntry save(ProductEntry productEntry) {
        if (productEntry.getId() == null) {
            productEntry.setId(new ObjectId().toString());
            productEntryRepository.save(productEntry);
        }
        if (productEntry.getId().isEmpty()) {
            productEntry.setId(new ObjectId().toString());
            productEntryRepository.save(productEntry);
        }
        return productEntry;
    }

    public ProductEntry update(ProductEntry productEntry)
        throws ProductEntryNotFoundException {
        var entry = productEntryRepository.findById(productEntry.getId())
            .orElseThrow(ProductEntryNotFoundException::new);
        entry.setQuantity(productEntry.getQuantity());
        return productEntryRepository.save(productEntry);
    }

    public ProductEntry findById(String id) throws ProductEntryNotFoundException {
        var productEntry =
            productEntryRepository.findById(id).orElseThrow(ProductEntryNotFoundException::new);
        return productEntry;
    }

    public void delete(String id) throws ProductEntryNotFoundException {
        var productEntry = productEntryRepository.findById(id).orElseThrow(ProductEntryNotFoundException::new);
        productEntryRepository.delete(productEntry);
    }

}

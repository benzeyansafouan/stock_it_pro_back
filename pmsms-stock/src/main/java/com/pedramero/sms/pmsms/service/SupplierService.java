package com.pedramero.sms.pmsms.service;

import com.pedramero.sms.pmsms.exception.SupplierNotFoundException;
import com.pedramero.sms.pmsms.model.Supplier;
import com.pedramero.sms.pmsms.repository.SupplierRepository;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SupplierService {

    @Autowired
    SupplierRepository supplierRepository;

    public Page<Supplier> getAllByPage(Pageable pageable){
        return supplierRepository.findAll(pageable);
    }

    public List<Supplier> getAll(){
        return StreamSupport.stream(supplierRepository.findAll().spliterator(), false)
            .collect(Collectors.toList());
    }

    public List<Supplier> search(String term){
        return supplierRepository.findAllByNameContainingIgnoreCase(term);
    }

    public Supplier save(Supplier supplier){
        supplier.setId(new ObjectId().toString());
        return supplierRepository.save(supplier);
    }

    public Supplier update(Supplier supplier)
        throws SupplierNotFoundException {
        var supplierToUpdate = supplierRepository.findById(supplier.getId()).orElseThrow(
            SupplierNotFoundException::new);

        supplierToUpdate.setAddress(supplier.getAddress());
        supplierToUpdate.setAccountNumber(supplier.getAccountNumber());
        supplierToUpdate.setEmail(supplier.getEmail());
        supplierToUpdate.setName(supplier.getName());
        supplierToUpdate.setPhoneNumber(supplier.getPhoneNumber());
        supplierToUpdate.setNote(supplier.getNote());
        return supplierRepository.save(supplierToUpdate);
    }

    public Supplier findById(String id) throws SupplierNotFoundException {
        var supplier = supplierRepository.findById(id).orElseThrow(SupplierNotFoundException::new);
        return supplier;
    }

    public void delete(String id) throws SupplierNotFoundException {
        var supplier = supplierRepository.findById(id).orElseThrow(SupplierNotFoundException::new);
        supplierRepository.delete(supplier);
    }

}

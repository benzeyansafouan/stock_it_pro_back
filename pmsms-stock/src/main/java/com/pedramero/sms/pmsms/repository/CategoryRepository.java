package com.pedramero.sms.pmsms.repository;

import com.pedramero.sms.pmsms.model.Category;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends MongoRepository<Category, String> {

    List<Category> findAllByNameContainingIgnoreCase(String name);
}

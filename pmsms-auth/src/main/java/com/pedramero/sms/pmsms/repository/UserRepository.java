package com.pedramero.sms.pmsms.repository;

import com.pedramero.sms.pmsms.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsernameIgnoreCase(String username);

    List<User> findByUsernameOrFirstNameOrLastNameContainingIgnoreCase(String name);
}

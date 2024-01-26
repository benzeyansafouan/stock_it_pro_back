package com.pedramero.sms.pmsms.service;

import com.pedramero.sms.pmsms.exception.UserAlreadyExistException;
import com.pedramero.sms.pmsms.exception.UserNotFoundException;
import com.pedramero.sms.pmsms.model.User;
import com.pedramero.sms.pmsms.repository.UserRepository;
import java.io.IOException;
import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    GridFsService gridFsService;

    @Autowired
    PasswordEncoder passwordEncoder;

    public Page<User> getAllWithPaging(Pageable pageable){
        return userRepository.findAll(pageable);
    }
    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User getById(String id) throws UserNotFoundException {
        return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
    }

    public User save(User user, MultipartFile multipartFile)
        throws UserAlreadyExistException, IOException {
        if (userRepository.findByUsernameIgnoreCase(user.getUsername()).isPresent()) {
            throw new UserAlreadyExistException();
        }
        user.setId(new ObjectId().toString());
        if (multipartFile != null) {
            var imageFile = gridFsService.saveFile(multipartFile.getResource(), user.getId(),
                user.getUsername());
            user.setImageFileObjectId(imageFile);
        }
        return userRepository.save(user);
    }


    public void delete(String id) throws UserNotFoundException {
        var userToDelete = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        userRepository.delete(userToDelete);
    }

    public Resource getUserImageById(String objectId) {
        return gridFsService.getFile(objectId);
    }


    public User updateUser(User user, MultipartFile multipartFile)
        throws UserNotFoundException, IOException {
        var updatedUser = userRepository.findById(user.getId());
        if (updatedUser.isEmpty()) {
            throw new UserNotFoundException();
        } else {
            if (multipartFile != null) {
                var imageFile =
                    gridFsService.saveFile(multipartFile.getResource(), user.getId(),
                        user.getUsername());
                user.setImageFileObjectId(imageFile);
            } else {
                user.setImageFileObjectId(user.getImageFileObjectId());
            }
        }
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            var password = passwordEncoder.encode(user.getPassword());
            user.setPassword(password);
        } else {
            user.setPassword(updatedUser.get().getPassword());
        }
        return userRepository.save(user);
    }

    public User findUserByUserName(String username) throws UserNotFoundException {
        var user = userRepository.findByUsernameIgnoreCase(username)
            .orElseThrow(UserNotFoundException::new);
        return user;
    }
    public List<User> search(String term){
        return userRepository.findByUsernameOrFirstNameOrLastNameContainingIgnoreCase(term);
    }
}

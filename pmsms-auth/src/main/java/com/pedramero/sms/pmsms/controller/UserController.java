package com.pedramero.sms.pmsms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pedramero.sms.pmsms.config.Logger;
import com.pedramero.sms.pmsms.exception.UserAlreadyExistException;
import com.pedramero.sms.pmsms.exception.UserNotFoundException;
import com.pedramero.sms.pmsms.model.User;
import com.pedramero.sms.pmsms.model.dto.UserDto;
import com.pedramero.sms.pmsms.service.UserService;
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
import org.springframework.security.crypto.password.PasswordEncoder;
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
@RequestMapping("/users")
public class UserController implements Logger {

    @Autowired
    UserService userService;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    PasswordEncoder passwordEncoder;

    @GetMapping("/all")
    public ResponseEntity<List<UserDto>> getAll() {
        var users = userService.getAll();
        var userDtos = users.stream()
            .map(this::mapToUserDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(userDtos);
    }

    @GetMapping("/all/paging")
    public ResponseEntity<Page<UserDto>> getAllWithPaging(@RequestParam int page,
                                                          @RequestParam int pageSize){
        Pageable pageable = (page == -1 || pageSize == -1) ?
                            Pageable.unpaged() :
                            PageRequest.of(page, pageSize, Sort.by("firstName"));
        Page<User> userPage = userService.getAllWithPaging(pageable);
        var users = userPage.stream().map(user ->{
            var userDto = modelMapper.map(user, UserDto.class);
            retrieveImage(user,userDto);
            return userDto;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(new PageImpl<>(users,pageable,userPage.getTotalElements()));
    }

    private UserDto mapToUserDto(User user) {
        var userDto = modelMapper.map(user, UserDto.class);
        userDto.setPassword(null);
        retrieveImage(user, userDto);
        return userDto;
    }

    @PostMapping("/save")
    public ResponseEntity<UserDto> save(@RequestParam String user, @RequestParam(required = false)
        MultipartFile userImage) {
        try {
            var newUser = new User();
            var userObject = objectMapper.readValue(user, UserDto.class);
            modelMapper.map(userObject, newUser);
            newUser.setPassword(passwordEncoder.encode(userObject.getPassword()));
            var savedUser = userService.save(newUser, userImage);
            var userDto = modelMapper.map(savedUser, UserDto.class);
            retrieveImage(savedUser, userDto);
            return ResponseEntity.ok(userDto);
        } catch (IOException | UserAlreadyExistException exception) {
            getLogger().warn("USER ALREADY EXISTS OR IOEXCEPTION ON IMAGE FILE");
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
    }


    private void retrieveImage(User user, UserDto userDto) {
        if (user.getImageFileObjectId() != null) {
            var resource = userService.getUserImageById(user.getImageFileObjectId());
            try {
                userDto.setUserImage(resource.getInputStream().readAllBytes());
            } catch (IOException e) {
                getLogger().warn("RETRIEVE IMAGE FAILED", e, user);
            }
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getById(@PathVariable("id") String id) {
        try {
            var user = userService.getById(id);
            var userDto = modelMapper.map(user, UserDto.class);
            retrieveImage(user, userDto);
            return ResponseEntity.ok().body(userDto);
        } catch (UserNotFoundException e) {
            getLogger().warn("USER NOT FOUND EXCEPTION FOR GETTING BY ID");
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/update")
    public ResponseEntity<UserDto> update(@RequestParam String user, @RequestParam(required = false)
        MultipartFile multipartFile) {
        try {
            var userObject = objectMapper.readValue(user, User.class);
            var result = userService.updateUser(userObject,multipartFile);
            var userDto = modelMapper.map(result,UserDto.class);
            return ResponseEntity.ok().body(userDto);
        } catch (IOException | UserNotFoundException e) {
            getLogger().warn("USER NOT FOUND EXCEPTION FOR UPDATE");
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public HttpStatus delete(@PathVariable("id") String id){
        try {
            userService.delete(id);
            return HttpStatus.OK;
        } catch (UserNotFoundException e){
            getLogger().warn("USER NOT FOUND EXCEPTION FOR DELETION");
            return HttpStatus.BAD_REQUEST;
        }
    }

    @GetMapping("/search/{term}")
    public ResponseEntity<List<UserDto>> search(@PathVariable String term) {
        var users = userService.search(term);
        var userDtos = users.stream()
            .map(user -> {
                var userDto = modelMapper.map(user, UserDto.class);
                retrieveImage(user, userDto);
                return userDto;
            }).collect(Collectors.toList());
        return ResponseEntity.ok(userDtos);
    }

}

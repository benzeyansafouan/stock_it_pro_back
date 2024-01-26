package com.pedramero.sms.pmsms.controller;

import com.pedramero.sms.pmsms.config.Logger;
import com.pedramero.sms.pmsms.exception.UserNotFoundException;
import com.pedramero.sms.pmsms.model.LoginCredentials;
import com.pedramero.sms.pmsms.model.Token;
import com.pedramero.sms.pmsms.service.UserService;
import com.pedramero.sms.pmsms.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/authenticate")
public class LoginController implements Logger {
    @Autowired
    UserService userService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    TokenUtils tokenUtils;

    @PostMapping("/login")
    public ResponseEntity<Token> login(@RequestBody LoginCredentials credentials){
        try {
            var user = userService.findUserByUserName(credentials.getUsername());
            if (passwordEncoder.matches(credentials.getPassword(),user.getPassword())){
                var jwtToken = tokenUtils.generateToken(user);
                return ResponseEntity.ok(new Token(user.getUsername(),jwtToken));
            }
        } catch (UserNotFoundException e) {
            getLogger().warn("ERROR WHILE FETCHING USER BY USERNAME FOR LOGIN", credentials.getUsername(),e);
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/check-token")
    public ResponseEntity<HttpStatus> checkToken(@RequestParam String token, @RequestParam String username){
        try {
            var user  = userService.findUserByUserName(username);
            if (Boolean.TRUE.equals(tokenUtils.validateToken(token,user))){
                return ResponseEntity.ok().build();
            }
        } catch(UserNotFoundException e) {
            getLogger().warn("ERROR WHILE FETCHING USER BY USERNAME FOR TOKEN CHECKING",username,e);
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}

package com.example._faEmail.controller;

import com.example._faEmail.dto.LoginUserDto;
import com.example._faEmail.dto.RegisterUserDto;
import com.example._faEmail.dto.VerifyCodeDto;
import com.example._faEmail.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService service;
    public UserController(UserService service) {
        this.service = service;
    }
    @PostMapping("/register")
    public ResponseEntity<String> RegisterUser(@Valid @RequestBody RegisterUserDto request){
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

        service.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("Success: You have been registred");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginUserDto loginDto) {
        return service.login(loginDto);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyCode(@RequestBody VerifyCodeDto request) {
        return service.verifyCode(request);
    }
}

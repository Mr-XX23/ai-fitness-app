package com.fitness.userservice.controllers;

import com.fitness.userservice.dto.RequestRegister;
import com.fitness.userservice.dto.UserResponse;
import com.fitness.userservice.services.UserServices;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {

    private UserServices userServices;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RequestRegister req) {
        return ResponseEntity.ok(userServices.register(req));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserProfile( @PathVariable String userId) {
        // Implementation for fetching user profile goes here
        return ResponseEntity.ok(userServices.getUserProfile(userId));
    }

}

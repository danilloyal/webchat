package com.danilloyal.webchat.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.danilloyal.webchat.dto.AuthenticateRequest;
import com.danilloyal.webchat.dto.AuthenticateResponse;
import com.danilloyal.webchat.dto.RefreshRequest;
import com.danilloyal.webchat.dto.RefreshResponse;
import com.danilloyal.webchat.dto.RegisterRequest;
import com.danilloyal.webchat.dto.RegisterResponse;
import com.danilloyal.webchat.dto.UserResponse;
import com.danilloyal.webchat.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        
        RegisterResponse response = authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticateResponse> authenticate(@Valid @RequestBody AuthenticateRequest request) {
        
        AuthenticateResponse  response = authService.authenticate(request);

        return ResponseEntity.ok().body(response);
    }
    
    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(Authentication authentication) {

        UserResponse response = authService.me(authentication.getName());

        return  ResponseEntity.ok().body(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        
        RefreshResponse response = authService.refresh(request);
        
        return ResponseEntity.ok().body(response);
    }
    
    
}
    
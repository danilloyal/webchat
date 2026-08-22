package com.danilloyal.webchat.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.danilloyal.webchat.dto.AuthenticateRequest;
import com.danilloyal.webchat.dto.AuthenticateResponse;
import com.danilloyal.webchat.dto.RefreshRequest;
import com.danilloyal.webchat.dto.RefreshResponse;
import com.danilloyal.webchat.dto.RegisterRequest;
import com.danilloyal.webchat.dto.RegisterResponse;
import com.danilloyal.webchat.dto.UserResponse;
import com.danilloyal.webchat.model.User;
import com.danilloyal.webchat.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    public RegisterResponse register(RegisterRequest request){
        
        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));

        userRepository.save(user);

        return new RegisterResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail()
        );
    }

    public AuthenticateResponse authenticate(AuthenticateRequest request) {

        User user = userRepository
                .findByUsername(request.username())
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found")
                );

        if (!passwordEncoder.matches(request.password(),user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        String jwtAcessToken = jwtService.generateAcessToken(user);
        String jwtRefreshToken = jwtService.generateRefreshToken(user);

        return new AuthenticateResponse(jwtAcessToken,jwtRefreshToken);
    }

    public UserResponse me(String username){
        
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found")
                );

        return new UserResponse(
            user.getId(), 
            user.getUsername(), 
            user.getEmail()
        );
    }

    public RefreshResponse refresh (RefreshRequest request){

        String refreshToken = request.refreshToken();

        String username = jwtService.extracUsernameFromToken(refreshToken);

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new BadCredentialsException("Invalid credentials")
                );

        if (!jwtService.isTokenValid(refreshToken,user)) {
        throw new BadCredentialsException("Invalid refresh token");
        }

        if (!jwtService.isRefreshToken(refreshToken)) {
            throw new BadCredentialsException("Invalid token type");
        }

        String accessToken = jwtService.generateAcessToken(user);

        return new RefreshResponse(accessToken);
    }
}

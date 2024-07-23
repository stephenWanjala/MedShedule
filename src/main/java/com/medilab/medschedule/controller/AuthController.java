package com.medilab.medschedule.controller;


import com.medilab.medschedule.Entity.Role;
import com.medilab.medschedule.Entity.User;
import com.medilab.medschedule.repository.UserRepository;
import com.medilab.medschedule.request.LoginRequest;
import com.medilab.medschedule.request.SignUpRequest;
import com.medilab.medschedule.response.ApiResponse;
import com.medilab.medschedule.response.JwtAuthenticationResponse;
import com.medilab.medschedule.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtTokenProvider tokenProvider;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        long startTime = System.currentTimeMillis();
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
            System.out.println("Authentication time: " + (System.currentTimeMillis() - startTime) + "ms");

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);
            System.out.println("Token generation time: " + (System.currentTimeMillis() - startTime) + "ms");

            String username = loginRequest.getUsername();
            Optional<User> user = userRepository.findByUsername(username);
            System.out.println("Database query time: " + (System.currentTimeMillis() - startTime) + "ms");

            if (user.isPresent()) {
                User nonNullUser = user.get();
                return ResponseEntity.ok(new HashMap<String, Object>() {{
                    put("token", jwt);
                    put("username", nonNullUser.getUsername());
                    put("role", nonNullUser.getRole().name());
                    put("id", nonNullUser.getId());
                    put("authorities", authentication.getAuthorities());
                }});
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
            }

        } catch (AuthenticationException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        } finally {
            System.out.println("Total login time: " + (System.currentTimeMillis() - startTime) + "ms");
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(false, "Username is already taken!"));
        }

        User user = new User(
                signUpRequest.getUsername(),
                passwordEncoder.encode(signUpRequest.getPassword()),
                Role.valueOf(signUpRequest.getRole())
        );

        userRepository.save(user);

        return ResponseEntity.ok(new ApiResponse(true, "User registered successfully"));
    }

    
    @GetMapping("/user")
    public ResponseEntity<?> getUserInfo(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7);
        String username = tokenProvider.getUsernameFromJWT(jwt);
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            User nonNullUser = user.get();
            return ResponseEntity.ok(new HashMap<String, Object>() {{
                put("username", nonNullUser.getUsername());
                put("role", nonNullUser.getRole().name());
                put("id", nonNullUser.getId());
                put("authorities", nonNullUser.getAuthorities());
            }});
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }


}
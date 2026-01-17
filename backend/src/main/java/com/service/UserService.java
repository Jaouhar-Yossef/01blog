package com.service;

import org.springframework.stereotype.Service;

import com.dto.UserResponseDTO;
import com.entity.User;
import com.entity.UserDetailsImpl;
import com.repository.UserRepository;
import com.security.JwtUtil;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.util.Response;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;


    public UserService(UserRepository userRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    public Response<UserResponseDTO> register(User user) {

        if (!isValidEmail(user.getEmail())) {
            return new Response<>(false, "Invalid email format", null, null);
        }

        if (!isValidPassword(user.getPassword())) {
            return new Response<>(false, "Password must be at least 6 characters", null, null);
        }

        user.setEmail(user.getEmail().toLowerCase());
        user.setUsername(user.getUsername().toLowerCase());
    
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return new Response<>(false, "Email already exists", null, null);
        }
    
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return new Response<>(false, "Username already exists", null, null);
        }
    
        user.setRole("USER");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    
        String token = jwtUtil.generateToken(user.getUsername());
    
        UserResponseDTO dto = new UserResponseDTO(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getRole()
        );
    
        return new Response<>(true, "User registered successfully", token, dto);
    }


    public Response<UserResponseDTO> login(String identifier, String password) {

        if (!isValidPassword(password)) {
            return new Response<>(false, "Password must be at least 6 characters", null, null);
        }

        identifier = identifier.toLowerCase();
    
        Optional<User> userOpt =
                userRepository.findByEmailOrUsername(identifier, identifier);
    
        if (userOpt.isEmpty()) {
            return new Response<>(false, "Invalid credentials", null, null);
        }
    
        User user = userOpt.get();
    
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return new Response<>(false, "Invalid credentials", null, null);
        }
    
        String token = jwtUtil.generateToken(user.getUsername());
    
        UserResponseDTO dto = new UserResponseDTO(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getRole()
        );
    
        return new Response<>(true, "Login successful", token, dto);
    }





     public UserResponseDTO getUserFromToken(String token) {
        try {
            // Use the signing key from JwtUtil
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(jwtUtil.getSigningKey()) // <- use your JwtUtil key
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String username = claims.getSubject();
            Optional<User> userOpt = userRepository.findByUsername(username);

            if (userOpt.isEmpty()) return null;

            User user = userOpt.get();

            // Return DTO just like in login
            return new UserResponseDTO(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getRole()
            );

        } catch (Exception e) {
            return null; // invalid or expired token
        }
    }



    private boolean isValidEmail(String email) {

        String emailRegex = "^[^\\s@]{3,}@[^\s@]{2,}\\.[^\\s@]{2,}$";
        return email != null && email.matches(emailRegex);
    }


    private boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }



     @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new UserDetailsImpl(user);
    }

}

package com.service;

import org.springframework.stereotype.Service;

import com.dto.UserResponseDTO;
import com.entity.Blog;
import com.entity.User;
import com.entity.UserDetailsImpl;
import com.repository.BlogRepository;
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
import jakarta.transaction.Transactional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    private final BlogRepository blogRepository; 


    public UserService(BlogRepository blogRepository , UserRepository userRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.blogRepository = blogRepository;
    }


    @Transactional
    public void deleteUser(Long userId) {
        List<Blog> blogs = blogRepository.findByCreatedById(userId);
        blogRepository.deleteAll(blogs); 
        userRepository.deleteById(userId);
    }

    public Response<UserResponseDTO> register(User user) {

        if (!isValidEmail(user.getEmail())) {
            return new Response<>(false, "Invalid email format", null);
        }

        if (!isValidPassword(user.getPassword())) {
            return new Response<>(false, "Password must be at least 6 characters",  null);
        }

        user.setEmail(user.getEmail().toLowerCase());
        user.setUsername(user.getUsername().toLowerCase());
    
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return new Response<>(false, "Email already exists",  null);
        }
    
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return new Response<>(false, "Username already exists", null);
        }
    
        user.setRole("USER");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    
        String token = jwtUtil.generateToken(user.getUsername());
    
        UserResponseDTO dto = new UserResponseDTO(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getRole(), 
            token
        );
    
        return new Response<>(true, "User registered successfully", dto);
    }


    public Response<UserResponseDTO> login(String identifier, String password) {

        if (!isValidPassword(password)) {
            return new Response<>(false, "Password must be at least 6 characters", null);
        }

        identifier = identifier.toLowerCase();
    
        Optional<User> userOpt =
                userRepository.findByEmailOrUsername(identifier, identifier);
    
        if (userOpt.isEmpty()) {
            return new Response<>(false, "Invalid credentials",  null);
        }
    
        User user = userOpt.get();
    
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return new Response<>(false, "Invalid credentials", null);
        }
    
        String token = jwtUtil.generateToken(user.getUsername());
    
        UserResponseDTO dto = new UserResponseDTO(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getRole(), 
            token
        );
    
        return new Response<>(true, "Login successful", dto);
    }





     public UserResponseDTO getUserFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(jwtUtil.getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String username = claims.getSubject();
            Optional<User> userOpt = userRepository.findByUsername(username);

            if (userOpt.isEmpty()) return null;

            User user = userOpt.get();

            return new UserResponseDTO(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getRole(),
                    token
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

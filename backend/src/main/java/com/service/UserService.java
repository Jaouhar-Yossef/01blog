package com.service;

import org.springframework.stereotype.Service;

import com.dto.UserRequestDTO;
import com.dto.UserResponseDTO;
import com.entity.User;
import com.entity.UserDetailsImpl;
import com.entity.Blogs.Blog;
import com.repository.UserRepository;
import com.repository.Blogs.BlogRepository;
import com.security.JwtUtil;

import java.util.List;
import java.util.Optional;

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

    public Response<UserResponseDTO> register(UserRequestDTO request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return new Response<>(false, "Email already exists",  null);
        }
    
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return new Response<>(false, "Username already exists", null);
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");
        user.setImageUrl("");
        userRepository.save(user);
    
        String token = jwtUtil.generateToken(request.getUsername());
    
        UserResponseDTO dto = new UserResponseDTO(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getRole(),
            user.getImageUrl(),
            token
        );
        return new Response<>(true, "User registered successfully", dto);
    }


    public Response<UserResponseDTO> login(String identifier, String password) {
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
            user.getImageUrl(),
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
                    user.getImageUrl(),
                    token
            );

        } catch (Exception e) {
            return null;
        }
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new UserDetailsImpl(user);
    }
    
}

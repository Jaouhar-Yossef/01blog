package com.service;

import org.springframework.stereotype.Service;

import com.dto.UserRequestDTO;
import com.dto.UserResponseDTO;
import com.dto.ValidationDTO;
import com.entity.User;
import com.entity.UserDetailsImpl;
import com.entity.Blogs.Blog;
import com.repository.UserRepository;
import com.repository.Blogs.BlogRepository;
import com.service.Blogs.MediaBlogService;
import com.config.JwtService;
import com.util.Response;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.transaction.Transactional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final BlogRepository blogRepository;
    private final MediaBlogService mediaBlogService;

    public UserService(BlogRepository blogRepository, UserRepository userRepository, MediaBlogService mediaBlogService,
            JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.mediaBlogService = mediaBlogService;
        this.passwordEncoder = passwordEncoder;
        this.blogRepository = blogRepository;
    }

    public ValidationDTO getDataUser(UUID user_id) {
        User user = userRepository.findById(user_id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ValidationDTO dto = new ValidationDTO(
                user.getUsername(),
                user.getImageUrl(),
                user.getRole(),
                user.getStatus());

        return dto;
    }

    @Transactional
    public void deleteUser(UUID userId) {
        List<Blog> blogs = blogRepository.findByCreatedById(userId);
        blogRepository.deleteAll(blogs);
        userRepository.deleteById(userId);
    }

    public Response<UserResponseDTO> register(UserRequestDTO request) {

        if (userRepository.findByEmail(request.getEmail().toLowerCase()).isPresent()) {
            return new Response<>(false, "Email already exists", null);
        }

        if (userRepository.findByUsername(request.getUsername().toLowerCase()).isPresent()) {
            return new Response<>(false, "Username already exists", null);
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");
        user.setImageUrl("");
        user.setStatus("ACTIVE");
        userRepository.save(user);

        String token = jwtService.generateToken(user.getUsername(), user.getEmail(), user.getId());

        UserResponseDTO dto = new UserResponseDTO(
                user.getUsername(),
                user.getImageUrl(),
                user.getRole(),
                token);
        return new Response<>(true, "User registered successfully", dto);
    }

    public Response<UserResponseDTO> login(String identifier, String password) {
        identifier = identifier.toLowerCase();
        Optional<User> userOpt = userRepository.findByEmailOrUsername(identifier, identifier);

        if (userOpt.isEmpty()) {
            return new Response<>(false, "Invalid credentials", null);
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return new Response<>(false, "Invalid credentials", null);
        }

        String token = jwtService.generateToken(user.getUsername(), user.getEmail(), user.getId());

        UserResponseDTO dto = new UserResponseDTO(
                user.getUsername(),
                user.getImageUrl(),
                user.getRole(),
                token);
        return new Response<>(true, "Login successful", dto);
    }

    // public UserResponseDTO getUserFromToken(String token) {
    // try {
    // UUID id = jwtService.extractUserId(token);
    // String username = jwtService.extractUsername(token);

    // Optional<User> userOpt = userRepository.findById(id);
    // if (userOpt.isEmpty()) return null;

    // User user = userOpt.get();

    // if (!user.getUsername().equals(username)) return null;

    // return new UserResponseDTO(
    // user.getUsername(),
    // user.getEmail(),
    // user.getImageUrl(),
    // token
    // );

    // } catch (Exception e) {
    // return null;
    // }
    // }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new UserDetailsImpl(user);
    }

    public Response<?> deleteAccount(UUID user_id) {
        try {

            User user = userRepository.findById(user_id)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<Blog> blogs = blogRepository.findByCreatedById(user.getId());

            for (Blog blog : blogs) {
                mediaBlogService.deleteBlogFiles(blog);
            }

            userRepository.delete(user);
            return new Response<>(true, "Account deleted successful!");
        } catch (Exception e) {
            return new Response<>(false, "Error deleting user: " + e.getMessage());
        }
    }
}

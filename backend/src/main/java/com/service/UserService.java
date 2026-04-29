package com.service;

import org.springframework.stereotype.Service;

import com.dto.Request.UpdateProfile;
import com.dto.Request.UserRequestDTO;
import com.dto.Response.UserResponseDTO;
import com.dto.Response.ValidationDTO;
import com.entity.User;
import com.entity.UserDetailsImpl;
import com.entity.Blogs.Blog;
import com.repository.UserRepository;
import com.repository.Blogs.BlogRepository;
import com.security.ImgProfileValidator;
import com.service.Blogs.MediaBlogService;
import com.config.JwtService;
import com.util.Response;
import com.util.UserStatus;

import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final BlogRepository blogRepository;
    private final MediaBlogService mediaBlogService;
    private final ImgProfileValidator imgProfileValidator;

    @Transactional
    public Response<UserResponseDTO> updateProfile(UUID user_id, UpdateProfile updateProfile) {

        if (user_id == null) {
            throw new IllegalArgumentException("user_id cannot be null");
        }

        User user = userRepository.findById(user_id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getStatus().equals(UserStatus.BANNED)) {
            throw new RuntimeException("You are banned from this platform.");
        }
        if (!updateProfile.getEmail().equals(user.getEmail())) {
            if (userRepository.findByEmail(updateProfile.getEmail().toLowerCase()).isPresent()) {
                return new Response<>(false, "Email already exists", null);
            }
            user.setEmail(updateProfile.getEmail());
        }

        if (!updateProfile.getUsername().equals(user.getUsername())) {
            if (userRepository.findByUsername(updateProfile.getUsername().toLowerCase()).isPresent()) {
                return new Response<>(false, "Username already exists", null);
            }
            user.setUsername(updateProfile.getUsername());
        }

        if (updateProfile.getPassword() != null && updateProfile.getPassword() != "") {

            if (updateProfile.getPassword().length() < 6) {
                throw new RuntimeException("Password must be at least 6 characters");
            }
            user.setPassword(passwordEncoder.encode(updateProfile.getPassword()));
        }

        if (updateProfile.getImage() != null) {
            try {
                updateImgProfile(updateProfile.getImage(), user);
            } catch (Exception e) {
                return new Response<>(false, e.getMessage(), null);
            }
        }

        if (updateProfile.getImage() == null && user.getImageUrl() != null) {
            try {
                this.deleteImgProfile(user);
            } catch (Exception e) {
                return new Response<>(false, e.getMessage(), null);
            }
        }

        userRepository.save(user);
        String token = jwtService.generateToken(user.getUsername(), user.getEmail(), user.getId());

        UserResponseDTO dto = new UserResponseDTO(
                user.getUsername(),
                user.getEmail(),
                user.getImageUrl(),
                user.getRole(),
                user.getStatus(),
                token);

        return new Response<>(true, null, dto);
    }

    @Transactional
    public void updateImgProfile(MultipartFile image, User user) throws Exception {

        this.deleteImgProfile(user);

        if (image.getSize() > 20 * 1024 * 1024) {
            throw new RuntimeException("File too large (max 20MB)");
        }

        imgProfileValidator.validate(image);

        String originalName = image.getOriginalFilename();
        if (originalName == null)
            originalName = "file";

        String fileName = UUID.randomUUID() + "_" + originalName;
        fileName = fileName.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");

        String uploadDir = System.getProperty("user.dir") + File.separator + "uploads";
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(fileName);
        Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        user.setImageUrl("/uploads/" + fileName);
    }

    @Transactional
    public void deleteImgProfile(User user) throws Exception {

        if (user.getImageUrl() == null || user.getImageUrl().isEmpty()) {
            return;
        }

        String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator;

        String fileName = Paths.get(user.getImageUrl()).getFileName().toString();
        File file = new File(uploadDir + fileName);

        if (file.exists() && !file.delete()) {
            throw new RuntimeException("Failed to delete Image Profile");
        }

        user.setImageUrl("");
    }

    @Transactional(readOnly = true)
    public ValidationDTO getDataUser(UUID user_id) {
        if (user_id == null) {
            throw new IllegalArgumentException("user_id cannot be null");
        }

        User user = userRepository.findById(user_id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new ValidationDTO(
                user.getUsername(),
                user.getImageUrl(),
                user.getRole(),
                user.getStatus(),
                user.getEmail());
    }

    @Transactional
    public void deleteUser(UUID userId) {

        if (userId == null) {
            throw new IllegalArgumentException("user_id cannot be null");
        }

        List<Blog> blogs = blogRepository.findByCreatedById(userId);

        if (!blogs.isEmpty()) {
            blogRepository.deleteAll(blogs);
        }

        userRepository.deleteById(userId);
    }

    @Transactional
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
        user.setStatus(UserStatus.ACTIVE);

        userRepository.save(user);

        String token = jwtService.generateToken(user.getUsername(), user.getEmail(), user.getId());

        UserResponseDTO dto = new UserResponseDTO(
                user.getUsername(),
                user.getEmail(),
                user.getImageUrl(),
                user.getRole(),
                user.getStatus(),
                token);

        return new Response<>(true, "User registered successfully", dto);
    }

    @Transactional
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
                user.getEmail(),
                user.getImageUrl(),
                user.getRole(),
                user.getStatus(),
                token);

        return new Response<>(true, "Login successful", dto);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new UserDetailsImpl(user);
    }

    @Transactional
    public Response<?> deleteAccount(UUID user_id) {

        if (user_id == null) {
            throw new IllegalArgumentException("user_id cannot be null");
        }

        User user = userRepository.findById(user_id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getStatus() == UserStatus.ADMIN) {
            throw new RuntimeException("You are the ADMIN!!");
        }

        List<Blog> blogs = blogRepository.findByCreatedById(user.getId());
        for (Blog blog : blogs) {
            mediaBlogService.deleteBlogFiles(blog);
        }

        try {
            deleteImgProfile(user);
        } catch (Exception e) {
            return new Response<>(false, e.getMessage());
        }

        userRepository.delete(user);
        return new Response<>(true, "Account deleted successful!");
    }
}
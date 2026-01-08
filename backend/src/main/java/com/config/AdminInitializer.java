package com.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.entity.User;
import com.repository.UserRepository;

@Configuration
public class AdminInitializer {

    @Bean
    CommandLineRunner createAdmin(UserRepository userRepository,
                                  PasswordEncoder passwordEncoder) {

        return args -> {
            if (userRepository.findByUsername("jaouhar").isEmpty()) {
            
                User admin = new User();
                admin.setUsername("jaouhar");
                admin.setEmail("jaouharadmin@gmail.com");
                admin.setPassword(passwordEncoder.encode("jaouhar1234"));
                admin.setRole("ADMIN");
                userRepository.save(admin);
                System.out.println("✅ Admin account created");
            }

        };
    }
}

package com.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.entity.Notifications;
import com.entity.User;
import com.repository.NotificationsRepository;
import com.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class NotificationsService {

    private final UserRepository userRepository;
    private final NotificationsRepository notificationsRepository;

    @Transactional
    public void getNotifications(UUID userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        List<Notifications> dataNotifications =  notificationsRepository.findByIntendedUser_id(userId , pageable);

        dataNotifications.stream()
              .forEach(n -> {
                System.out.println( "==> " + n.getMessage());
              });
    }

}

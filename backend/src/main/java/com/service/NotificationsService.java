package com.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.dto.Response.NotificationResponseDTO;
import com.entity.Notifications;
import com.repository.NotificationsRepository;
import com.repository.UserRepository;
import com.util.BlogStatus;
import com.util.Response;
import com.util.TypeNotifications;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class NotificationsService {

  private final UserRepository userRepository;
  private final NotificationsRepository notificationsRepository;

  @Transactional
  public Response<?> deleteNotification(Long Id, UUID userId) {
    userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));
        
    Optional<Notifications> existing = notificationsRepository.findByIdAndIntendedUser_Id(Id, userId);
    if (existing.isEmpty()) {
      throw new RuntimeException("This notification not found");
    }

    notificationsRepository.deleteById(Id);

    return new Response<>(true, "notification deleted");
  }

  @Transactional
  public Response<?> getNotifications(UUID userId, int page, int size) {
    userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));

    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

    List<Notifications> dataNotifications = notificationsRepository.findByIntendedUser_id(userId, pageable);

    List<NotificationResponseDTO> data = dataNotifications.stream()
        .filter(f -> !((f.getType() == TypeNotifications.LIKE || f.getType() == TypeNotifications.COMMENT ||
            f.getType().equals(TypeNotifications.REPORTEDBLOG))
            && f.getIntendedBlog().getStatus() == BlogStatus.HIDDEN))
        .map(f -> {
          UUID blogid = null;
          String Blog_title = "";
          String causeUser = "";

          if (f.getType().equals(TypeNotifications.FOLLOW)) {
            causeUser = f.getCreatorNf().getUsername();
          }

          if (f.getType().equals(TypeNotifications.REPORTEDBLOG) || f.getType().equals(TypeNotifications.BLOGHIDDEN)) {
            blogid = f.getIntendedBlog().getId();
            Blog_title = f.getIntendedBlog().getTitle();
          }

          if (f.getType().equals(TypeNotifications.LIKE) || f.getType().equals(TypeNotifications.COMMENT)) {
            causeUser = f.getCreatorNf().getUsername();
            blogid = f.getIntendedBlog().getId();
            Blog_title = f.getIntendedBlog().getTitle();
          }
          return new NotificationResponseDTO(
              f.getId(),
              causeUser,
              f.getMessage(),
              f.getCreatedAt(),
              f.getType(),
              blogid,
              Blog_title,
              f.getIntendedUser().getUsername());
        })
        .toList();
    return new Response<>(true, "", data);
  }

}

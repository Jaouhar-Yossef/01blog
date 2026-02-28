package com.dto.Response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.util.TypeNotifications;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDTO {
    private Long id;
    private String causativeUser_id;
    private String message;
    private LocalDateTime creat_at;
    private TypeNotifications type;
    private UUID Intended_Blog_id;
    private String Intended_Blog_title;
    private String intendedUser_id;
}

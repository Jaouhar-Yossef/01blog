package com.dto.Request;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NotificationRequest {
    @NotNull(message = "id cannot be null")
    private Long id_Notification;
}

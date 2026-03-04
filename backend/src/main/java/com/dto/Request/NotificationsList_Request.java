package com.dto.Request;

import java.util.List;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class NotificationsList_Request {
    @NotEmpty(message = "Notification list cannot be empty")
    private List<Long> notificationIds;
}

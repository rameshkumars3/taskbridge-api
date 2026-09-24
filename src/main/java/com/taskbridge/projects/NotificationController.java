package com.taskbridge.projects;

import java.time.Instant;
import java.util.List;
import jakarta.validation.constraints.Positive;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@Validated
@RequestMapping("/api/notifications")
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/{userId}")
    public List<NotificationResponse> list(@PathVariable @Positive Long userId,
            @RequestParam(required = false) Instant from, @RequestParam(required = false) Instant to,
            @RequestParam(required = false) ProjectEventType eventType) {
        return notificationService.findAll(userId, from, to, eventType);
    }

    @PatchMapping("/{id}/read")
    public NotificationResponse markRead(@PathVariable @Positive Long id) {
        return notificationService.markRead(id);
    }
}

package com.swapit.controller;

import com.swapit.dto.SwapRequestResponse;
import com.swapit.service.SwapRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class NotificationController {
    private final SwapRequestService swapRequestService;

    @GetMapping("/users/{userId}/notifications")
    public List<SwapRequestResponse.Notification> getNotifications(@PathVariable long userId) {
        return swapRequestService.getNotifications(userId);
    }

    @PatchMapping("/notifications/{notificationId}/read")
    public Map<String, Object> readNotification(@PathVariable long notificationId) {
        return Map.of(
                "notificationId", notificationId,
                "read", true,
                "message", "데모 알림을 읽음 처리했습니다."
        );
    }
}

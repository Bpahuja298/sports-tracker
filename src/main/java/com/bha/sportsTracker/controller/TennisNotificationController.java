package com.bha.sportsTracker.controller;

import com.bha.sportsTracker.service.TennisNotificationService;
import com.bha.sportsTracker.service.TennisNotificationService.TennisNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tennis/notifications")
@CrossOrigin(origins = "http://localhost:3000")
public class TennisNotificationController {
    
    @Autowired
    private TennisNotificationService notificationService;
    
    @GetMapping("/{userId}")
    public ResponseEntity<List<TennisNotification>> getNotifications(@PathVariable String userId) {
        List<TennisNotification> notifications = notificationService.getNotificationsForUser(userId);
        return ResponseEntity.ok(notifications);
    }
    
    @GetMapping("/{userId}/count")
    public ResponseEntity<Map<String, Integer>> getUnreadCount(@PathVariable String userId) {
        int count = notificationService.getUnreadNotificationCount(userId);
        return ResponseEntity.ok(Map.of("unreadCount", count));
    }
    
    @PostMapping("/{userId}/mark-read/{notificationId}")
    public ResponseEntity<Void> markAsRead(@PathVariable String userId, @PathVariable String notificationId) {
        notificationService.markNotificationAsRead(userId, notificationId);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{userId}/generate-sample")
    public ResponseEntity<Void> generateSampleNotifications(@PathVariable String userId) {
        notificationService.generateSampleNotifications(userId);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<Void> clearAllNotifications(@PathVariable String userId) {
        notificationService.clearAllNotifications(userId);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{userId}/subscribe/{matchId}")
    public ResponseEntity<Map<String, String>> subscribeToMatch(@PathVariable String userId, @PathVariable String matchId) {
        // In a real implementation, this would set up notifications for a specific match
        return ResponseEntity.ok(Map.of("message", "Subscribed to match notifications"));
    }
}
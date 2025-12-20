package com.akerugen.notificationservice.controller;

import com.akerugen.notificationservice.dto.response.NotificationResponseDto;
import com.akerugen.notificationservice.dto.response.UnreadCountResponse;
import com.akerugen.notificationservice.service.NotificationService;
import com.akerugen.notificationservice.util.JwtTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notification Management", description = "Operations related to notification management")
public class NotificationController {

    private static final Logger logger = LogManager.getLogger(NotificationController.class);
    private final NotificationService notificationService;
    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public NotificationController(NotificationService notificationService, JwtTokenUtil jwtTokenUtil) {
        this.notificationService = notificationService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    /**
     * Получить все уведомления текущего пользователя
     */
    @GetMapping
    @Operation(summary = "Get user notifications", description = "Retrieves all notifications for the current authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token")
    })
    public ResponseEntity<List<NotificationResponseDto>> getUserNotifications(HttpServletRequest request) {
        try {
            Long userId = extractUserId(request);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            logger.info("GET /api/notifications - retrieving notifications for user: {}", userId);
            List<NotificationResponseDto> notifications = notificationService.getUserNotifications(userId);
            return new ResponseEntity<>(notifications, HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("Error getting user notifications: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Получить количество непрочитанных уведомлений
     */
    @GetMapping("/unread/count")
    @Operation(summary = "Get unread count", description = "Retrieves the count of unread notifications for the current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Count retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token")
    })
    public ResponseEntity<UnreadCountResponse> getUnreadCount(HttpServletRequest request) {
        try {
            Long userId = extractUserId(request);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            logger.debug("GET /api/notifications/unread/count - getting count for user: {}", userId);
            UnreadCountResponse count = notificationService.getUnreadCount(userId);
            return new ResponseEntity<>(count, HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("Error getting unread count: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Отметить уведомление как прочитанное
     */
    @PatchMapping("/{id}/read")
    @Operation(summary = "Mark notification as read", description = "Marks a notification as read for the current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification marked as read"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
            @ApiResponse(responseCode = "404", description = "Notification not found")
    })
    public ResponseEntity<NotificationResponseDto> markAsRead(
            @PathVariable Long id,
            HttpServletRequest request) {
        try {
            Long userId = extractUserId(request);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            logger.info("PATCH /api/notifications/{}/read - marking as read for user: {}", id, userId);
            NotificationResponseDto notification = notificationService.markAsRead(id, userId);
            return new ResponseEntity<>(notification, HttpStatus.OK);
        } catch (RuntimeException ex) {
            logger.error("Error marking notification as read: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception ex) {
            logger.error("Unexpected error: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Извлекает userId из JWT токена в Authorization заголовке
     */
    private Long extractUserId(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.warn("Missing or invalid Authorization header");
                return null;
            }

            String token = authHeader.substring(7);
            return jwtTokenUtil.getUserIdFromToken(token);
        } catch (Exception ex) {
            logger.error("Error extracting userId from token: {}", ex.getMessage());
            return null;
        }
    }
}


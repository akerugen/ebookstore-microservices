package com.akerugen.notificationservice.controller;

import com.akerugen.notificationservice.dto.request.CreateNotificationRequest;
import com.akerugen.notificationservice.dto.response.NotificationResponseDto;
import com.akerugen.notificationservice.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications/internal")
@Tag(name = "Internal Notification Management", description = "Internal endpoints for notification creation (used by other services)")
public class InternalNotificationController {

    private static final Logger logger = LogManager.getLogger(InternalNotificationController.class);
    private final NotificationService notificationService;

    @Autowired
    public InternalNotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Создать уведомление (internal endpoint для других сервисов)
     * Используется catalog-service для создания уведомлений при изменении книг
     */
    @PostMapping("/create")
    @Operation(summary = "Create notification (internal)", description = "Internal endpoint used by other services to create notifications")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Notification created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<NotificationResponseDto> createNotification(@Valid @RequestBody CreateNotificationRequest request) {
        logger.info("POST /api/notifications/internal/create - creating notification for user: {} with type: {}", 
                request.getUserId(), request.getType());
        NotificationResponseDto notification = notificationService.createNotification(request);
        return new ResponseEntity<>(notification, HttpStatus.CREATED);
    }
}


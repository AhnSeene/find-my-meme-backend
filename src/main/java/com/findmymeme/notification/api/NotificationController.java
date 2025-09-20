package com.findmymeme.notification.api;

import com.findmymeme.common.resolver.CurrentUserId;
import com.findmymeme.notification.domain.Notification;
import com.findmymeme.notification.dto.NotificationResponse;
import com.findmymeme.notification.service.NotificationService;
import com.findmymeme.notification.service.SseConnectionManager;
import com.findmymeme.response.ApiResult;
import com.findmymeme.response.ResponseUtil;
import com.findmymeme.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Tag(name = "9. Notifications")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class NotificationController {
    private final NotificationService notificationService;
    private final SseConnectionManager connectionManager;

    @Operation(summary = "SSE 실시간 알림 구독", description = "서버로부터 실시간 알림을 받기 위해 SSE 연결을 수립합니다. 연결 후에는 'notification' 이벤트를 수신 대기합니다.") // 2. Swagger 문서 추가
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SSE 연결 성공. Content-Type은 text/event-stream 입니다.",
                    content = @Content(mediaType = "text/event-stream")),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(schema = @Schema(implementation = ApiResult.class)))
    })
    @GetMapping(value = "/sse/notifications", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@Parameter(hidden = true) @CurrentUserId Long userId) {
        return connectionManager.addConnection(userId);
    }

    @Operation(summary = "내 알림 목록 조회", description = "현재 로그인한 사용자의 모든 알림을 최신순으로 조회합니다.") // 3. 새로운 API에 대한 Swagger 문서 추가
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "알림 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(schema = @Schema(implementation = ApiResult.class)))
    })
    @GetMapping("/notifications")
    public ResponseEntity<ApiResult<List<NotificationResponse>>> getNotifications(@Parameter(hidden = true) @CurrentUserId Long userId) {
        return ResponseUtil.success(notificationService.getNotifications(userId), SuccessCode.NOTIFICATION_LIST);
    }


}

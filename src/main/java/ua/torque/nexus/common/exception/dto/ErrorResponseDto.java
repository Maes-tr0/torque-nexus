package ua.torque.nexus.common.exception.dto;


import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;

@Builder
public record ErrorResponseDto(
        String type,
        String message,
        Map<String, Object> details,
        LocalDateTime timestamp) {
}

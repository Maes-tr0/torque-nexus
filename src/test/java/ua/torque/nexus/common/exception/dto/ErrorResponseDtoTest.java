package ua.torque.nexus.common.exception.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorResponseDtoTest {

    @Test
    void builderBuildsCorrectly() {
        String type = "ErrorType";
        String message = "An error occurred";
        Map<String, Object> details = Map.of("field", "value");
        LocalDateTime timestamp = LocalDateTime.now();
        ErrorResponseDto dto = ErrorResponseDto.builder()
                .type(type)
                .message(message)
                .details(details)
                .timestamp(timestamp)
                .build();
        assertThat(dto.type()).isEqualTo(type);
        assertThat(dto.message()).isEqualTo(message);
        assertThat(dto.details()).isEqualTo(details);
        assertThat(dto.timestamp()).isEqualTo(timestamp);
    }

    @Test
    void equalsAndHashCodeWork() {
        String type = "ErrorType";
        String message = "An error occurred";
        Map<String, Object> details = Map.of("field", "value");
        LocalDateTime timestamp = LocalDateTime.now();
        ErrorResponseDto dto1 = ErrorResponseDto.builder()
                .type(type)
                .message(message)
                .details(details)
                .timestamp(timestamp)
                .build();
        ErrorResponseDto dto2 = ErrorResponseDto.builder()
                .type(type)
                .message(message)
                .details(details)
                .timestamp(timestamp)
                .build();
        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    void toStringContainsValues() {
        ErrorResponseDto dto = ErrorResponseDto.builder()
                .type("Test")
                .message("Test message")
                .details(Map.of("key", "val"))
                .timestamp(LocalDateTime.of(2025, 3, 24, 12, 0))
                .build();
        String str = dto.toString();
        assertThat(str).contains("Test", "Test message", "key=val", "2025-03-24");
    }
}

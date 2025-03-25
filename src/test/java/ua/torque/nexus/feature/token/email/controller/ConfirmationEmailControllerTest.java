package ua.torque.nexus.feature.token.email.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ua.torque.nexus.feature.token.email.dto.ConfirmationEmailResponse;
import ua.torque.nexus.feature.token.email.mapper.ConfirmationEmailMapper;
import ua.torque.nexus.feature.token.email.model.ConfirmationToken;
import ua.torque.nexus.feature.token.email.service.ConfirmationTokenService;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = ConfirmationEmailController.class, properties = {
        "server.port=0"
})
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("ConfirmationEmailController Integration Tests")
class ConfirmationEmailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ConfirmationTokenService tokenService;

    @MockitoBean
    private ConfirmationEmailMapper confirmationEmailMapper;

    @Test
    @DisplayName("GET /api/v1/auth/confirm — Success")
    void confirmEmail_success() throws Exception {
        ConfirmationToken token = ConfirmationToken.builder().token("abc123").build();
        ConfirmationEmailResponse response = ConfirmationEmailResponse.builder()
                .token("abc123")
                .message("Email confirmed")
                .build();

        when(tokenService.confirmToken(anyString())).thenReturn(token);
        when(confirmationEmailMapper.tokenToResponse(token)).thenReturn(response);

        mockMvc.perform(get("/api/v1/auth/confirm")
                        .param("token", "abc123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.token").value("abc123"))
                .andExpect(jsonPath("$.message").value("Email confirmed"));
    }

    @Test
    @DisplayName("GET /api/v1/auth/confirm — Validation Error (missing token)")
    void confirmEmail_validationError() throws Exception {
        mockMvc.perform(get("/api/v1/auth/confirm")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details").exists());
    }
}

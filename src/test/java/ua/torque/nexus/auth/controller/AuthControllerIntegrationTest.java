package ua.torque.nexus.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ua.torque.nexus.auth.dto.RegistrationRequest;
import ua.torque.nexus.auth.dto.RegistrationResponse;
import ua.torque.nexus.auth.dto.ResetPasswordRequest;
import ua.torque.nexus.auth.dto.ResetPasswordResponse;
import ua.torque.nexus.auth.service.AuthService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(value = AuthController.class, properties = {
        "server.port=0"
})
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("AuthController Integration Tests")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @Test
    @DisplayName("POST /api/v1/auth/register - Success")
    void register_success() throws Exception {
        RegistrationRequest request = RegistrationRequest.builder()
                .fullName("John Doe")
                .email("john.doe@example.com")
                .password("Secret123")
                .phoneNumber("+1234567890")
                .build();

        RegistrationResponse response = RegistrationResponse.builder()
                .email("john.doe@example.com")
                .message("Registration successful")
                .token("dummy-token")
                .build();

        when(authService.registerUser(any(RegistrationRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.message").value("Registration successful"))
                .andExpect(jsonPath("$.token").value("dummy-token"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/register - Validation Error")
    void register_validationError() throws Exception {
        RegistrationRequest request = RegistrationRequest.builder()
                .fullName("john")
                .email("")
                .password("Secret123")
                .phoneNumber("+1234567890")
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details").exists());
    }

    @Test
    @DisplayName("POST /api/v1/auth/reset-password - Success")
    void resetPassword_success() throws Exception {
        ResetPasswordRequest request = ResetPasswordRequest.builder()
                .email("john.doe@example.com")
                .newPassword("NewSecret123")
                .build();

        ResetPasswordResponse response = ResetPasswordResponse.builder()
                .email("john.doe@example.com")
                .message("Password reset successful")
                .build();

        when(authService.resetPassword(any(ResetPasswordRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.message").value("Password reset successful"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/reset-password - Validation Error")
    void resetPassword_validationError() throws Exception {
        ResetPasswordRequest request = ResetPasswordRequest.builder()
                .email("")
                .newPassword("NewSecret123")
                .build();

        mockMvc.perform(post("/api/v1/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details").exists());
    }
}

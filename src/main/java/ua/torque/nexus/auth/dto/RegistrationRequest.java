package ua.torque.nexus.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode
public class RegistrationRequest {
    @NotBlank(message = "Full name cannot be blank")
    @NotNull(message = "Full name cannot be null")
    @Pattern(
            regexp = "[A-Z][a-z]+ [A-Z][a-z]+",
            message = "Full name must be like this the format 'John Doe'"
    )
    private String fullName;

    @NotBlank(message = "Email cannot be blank")
    @NotNull(message = "Email cannot be null")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @NotNull(message = "Password cannot be null")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$",
            message = "Password must be at least 8 characters long and contain at least one digit, one lowercase and one uppercase letter"
    )
    private String password;

    @Size(min = 10, max = 15,
            message = "The phone number must contain 10 to 15 characters")
    @Pattern(
            regexp = "\\+?[0-9]+",
            message = "Phone number must contain only digits and optional '+' prefix"
    )
    private String phoneNumber;
}

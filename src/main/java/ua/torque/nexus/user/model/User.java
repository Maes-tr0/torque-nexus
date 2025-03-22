package ua.torque.nexus.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import ua.torque.nexus.access.model.Role;
import ua.torque.nexus.access.model.RoleType;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
@NoArgsConstructor
@ToString(exclude = "password")
@Entity
@Table(name = "users")
public class User {
    @Id
    @SequenceGenerator(
            name = "seq_user",
            sequenceName = "seq_user_id",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "seq_user"
    )
    @Setter(AccessLevel.PRIVATE)
    @Column(updatable = false, nullable = false)
    private Long id;

    @Column(name = "full_name", nullable = false)
    @NotBlank(message = "Full name cannot be blank")
    @Pattern(regexp = "[A-Z][a-z]+ [A-Z][a-z]+",
            message = "Full name must be in the format 'Firstname Lastname'")
    private String fullName;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    private String email;

    @Column(name = "email_confirmed")
    private boolean emailConfirmed = false;

    @Column(nullable = false)
    @NotBlank(message = "Password cannot be blank")
    private String password;

    @Column(name = "phone_number")
    @Size(min = 10, max = 15,
            message = "The phone number must contain 10 to 15 characters")
    @Pattern(
            regexp = "\\+?\\d+",
            message = "Phone number must contain only digits and optional '+' prefix"
    )
    private String phoneNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "created_at", updatable = false, nullable = false)
    @CreationTimestamp
    private LocalDateTime created;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updated;

    @Builder
    public User(String fullName, String email, String password, String phoneNumber) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return email != null && email.equals(user.getEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}

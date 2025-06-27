package ua.torque.nexus.user.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ua.torque.nexus.access.model.role.Role;
import ua.torque.nexus.vehicle.model.Vehicle;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.ListResourceBundle;
import java.util.Objects;
import java.util.Set;

@Data
@NoArgsConstructor
@ToString(exclude = "password")
@Entity
@Table(name = "users")
public class User implements UserDetails {
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

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<Vehicle> vehicles = new HashSet<>();

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

    public void addVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
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


    //TODO: Доробити
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        List<GrantedAuthority> auths = new ArrayList<>();

        auths.add(new SimpleGrantedAuthority("ROLE_" + role.getType()));

        role.getPermissions().forEach(permission -> {
            auths.add(new SimpleGrantedAuthority(permission.getType().name()));
        });

        return auths;
    }

    @Override
    public String getUsername() {
        return getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return emailConfirmed;
    }
}

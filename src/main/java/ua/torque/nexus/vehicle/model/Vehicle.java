package ua.torque.nexus.vehicle.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import ua.torque.nexus.user.model.User;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity
@ToString(exclude = "users")
@EqualsAndHashCode(exclude = "users")
@Table(name = "vehicles")
public class Vehicle {
    @Id
    @SequenceGenerator(
            name = "seq_vehicle",
            sequenceName = "seq_vehicle_id",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "seq_vehicle"
    )
    @Setter(AccessLevel.PRIVATE)
    @Column(updatable = false, nullable = false)
    private Long id;

    @NotBlank(message = "VIN code cannot be blank")
    @Column(nullable = false, unique = true, name = "vin_code")
    private String vinCode;

    @NotBlank(message = "Mark cannot be blank")
    @Column(nullable = false)
    private String mark;

    @NotBlank(message = "Model cannot be blank")
    @Column(nullable = false)
    private String model;

    @NotNull(message = "Year cannot be null")
    private Integer year;

    @NotBlank(message = "License plate cannot be blank")
    @Column(nullable = false, unique = true, name = "license_plate")
    private String licensePlate;

    @ManyToMany(mappedBy = "vehicles")
    private Set<User> users = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
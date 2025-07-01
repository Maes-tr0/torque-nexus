package ua.torque.nexus.vehicle.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import ua.torque.nexus.user.model.User;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "user")
@Entity
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
    @Column(nullable = false)
    private Integer year;

    @NotBlank(message = "License plate cannot be blank")
    @Column(nullable = false, unique = true, name = "license_plate")
    private String licensePlate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehicle vehicle = (Vehicle) o;
        return vinCode != null && vinCode.equals(vehicle.vinCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vinCode);
    }
}
package ua.torque.nexus.vehicle.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode
public class UpdateVehicleRequest {
    @Size(min = 17, max = 17, message = "VIN code must be exactly 17 characters long")
    private String vinCode;

    private String mark;

    private String model;

    @Min(value = 1900, message = "Year must be greater than 1900")
    private Integer year;

    private String licensePlate;
}

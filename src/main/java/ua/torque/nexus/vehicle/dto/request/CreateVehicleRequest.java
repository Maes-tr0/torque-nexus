package ua.torque.nexus.vehicle.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode
public class CreateVehicleRequest {
    @NotBlank(message = "VIN code cannot be blank")
    @Size(min = 17, max = 17, message = "VIN code must be exactly 17 characters long")
    private String vinCode;

    @NotBlank(message = "Mark cannot be blank")
    private String mark;

    @NotBlank(message = "Model cannot be blank")
    private String model;

    @NotNull(message = "Year cannot be null")
    @Min(value = 1900, message = "Year must be greater than 1900")
    private Integer year;

    @NotBlank(message = "License plate cannot be blank")
    private String licensePlate;
}

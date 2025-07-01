package ua.torque.nexus.vehicle.dto.response;

import lombok.Builder;

@Builder
public record VehicleResponse(
        Long id,
        String vinCode,
        String mark,
        String model,
        int year,
        String licensePlate,
        String message
) {
}

package ua.torque.nexus.vehicle.dto.response;

import lombok.Builder;

@Builder
public record VehicleResponse(String mark, String vinCode, String message) {
}

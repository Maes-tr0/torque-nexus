package ua.torque.nexus.vehicle.mapper;

import ua.torque.nexus.vehicle.dto.response.VehicleResponse;
import ua.torque.nexus.vehicle.model.Vehicle;

public final class VehicleResponseFactory {

    private VehicleResponseFactory() {}


    public static VehicleResponse buildOnCreate(Vehicle vehicle) {
        return toResponseBuilder(vehicle)
                .message("Vehicle was successfully added.")
                .build();
    }

    public static VehicleResponse buildOnUpdate(Vehicle vehicle) {
        return toResponseBuilder(vehicle)
                .message("Vehicle information was successfully updated.")
                .build();
    }

    private static VehicleResponse.VehicleResponseBuilder toResponseBuilder(Vehicle vehicle) {
        return VehicleResponse.builder()
                .id(vehicle.getId())
                .vinCode(vehicle.getVinCode())
                .mark(vehicle.getMark())
                .model(vehicle.getModel())
                .year(vehicle.getYear())
                .licensePlate(vehicle.getLicensePlate());
    }
}
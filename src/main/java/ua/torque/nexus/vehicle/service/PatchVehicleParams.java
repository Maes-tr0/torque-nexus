package ua.torque.nexus.vehicle.service;

public record PatchVehicleParams(
        String vinCode,
        String mark,
        String model,
        Integer year,
        String licensePlate
) {
}
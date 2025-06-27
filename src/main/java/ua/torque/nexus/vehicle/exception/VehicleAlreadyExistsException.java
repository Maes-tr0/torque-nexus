package ua.torque.nexus.vehicle.exception;

public class VehicleAlreadyExistsException extends RuntimeException {
    public VehicleAlreadyExistsException(String message) {
        super(message);
    }
}

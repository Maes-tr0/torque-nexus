package ua.torque.nexus.vehicle.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.torque.nexus.common.exception.AccessDeniedException;
import ua.torque.nexus.common.exception.DataConflictException;
import ua.torque.nexus.common.exception.DataNotFoundException;
import ua.torque.nexus.common.exception.ExceptionType;
import ua.torque.nexus.user.model.User;
import ua.torque.nexus.user.service.UserService;
import ua.torque.nexus.vehicle.model.Vehicle;
import ua.torque.nexus.vehicle.repository.VehicleRepository;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final UserService userService;


    @Transactional
    public Vehicle saveNewVehicle(Vehicle newVehicle, User detachedCurrentUser) {
        log.info("Starting to save new vehicle with VIN {} for user '{}'",
                newVehicle.getVinCode(), detachedCurrentUser.getEmail());

        assertVinCodeIsUnique(newVehicle);

        User managedUser = userService.getUserByEmail(detachedCurrentUser.getEmail());

        managedUser.addVehicle(newVehicle);
        log.debug("Bidirectional relationship set for user '{}' and vehicle VIN {}",
                managedUser.getEmail(), newVehicle.getVinCode());

        Vehicle savedVehicle = vehicleRepository.save(newVehicle);
        log.info("<-Vehicle-Creation-> completed. Saved vehicle with id {} for user '{}'",
                savedVehicle.getId(), managedUser.getEmail());

        return savedVehicle;
    }

    private void assertVinCodeIsUnique(Vehicle vehicle) {
        log.debug("Checking for uniqueness with VIN: {}", vehicle.getVinCode());
        vehicleRepository.findByVinCode(vehicle.getVinCode()).ifPresent(existingVehicle -> {
            log.warn("Attempted to create a vehicle with an existing VIN: {}", vehicle.getVinCode());
            throw new DataConflictException(
                    ExceptionType.VEHICLE_VIN_ALREADY_EXISTS,
                    Map.of("VIN", vehicle.getVinCode())
            );
        });
    }

    @Transactional
    public Vehicle patchVehicle(Long vehicleId, PatchVehicleParams params, User currentUser) {
        log.info("Starting to patch vehicle with id {} for user '{}'", vehicleId, currentUser.getEmail());

        Vehicle vehicleToPatch = getVehicleByIdForUser(vehicleId, currentUser);

        applyPatch(vehicleToPatch, params);
        log.info("<-Vehicle-Patch-> completed for vehicle with id {}", vehicleId);

        return vehicleToPatch;
    }

    private void applyPatch(Vehicle vehicle, PatchVehicleParams params) {
        log.debug("Applying patch params to vehicle id {}", vehicle.getId());

        if (params.vinCode() != null) {
            vehicle.setVinCode(params.vinCode());
        }

        if (params.mark() != null) {
            vehicle.setMark(params.mark());
        }
        if (params.model() != null) {
            vehicle.setModel(params.model());
        }
        if (params.year() != null) {
            vehicle.setYear(params.year());
        }
        if (params.licensePlate() != null) {
            vehicle.setLicensePlate(params.licensePlate());
        }
    }

    @Transactional
    public void deleteVehicle(Long vehicleId, User currentUser) {
        log.info("Starting vehicle deletion for id {} by user '{}'", vehicleId, currentUser.getEmail());

        Vehicle vehicleById = getVehicleByIdForUser(vehicleId, currentUser);

        vehicleRepository.delete(vehicleById);

        log.info("<-Vehicle-Deletion-> completed for vehicle with id {}", vehicleId);
    }

    public Vehicle getVehicleByIdForUser(Long vehicleId, User currentUser) {
        log.info("-> Fetching vehicle with id {} for user '{}'", vehicleId, currentUser.getEmail());

        Vehicle vehicle = findVehicleById(vehicleId);

        if (!Objects.equals(vehicle.getUser().getId(), currentUser.getId())) {
            log.warn("User '{}' attempted to access vehicle '{}' owned by another user.",
                    currentUser.getEmail(), vehicleId);
            throw new AccessDeniedException(ExceptionType.ACCESS_DENIED);
        }

        log.info("<-Vehicle-Fetch-> completed. Found vehicle id {}", vehicle.getId());

        return vehicle;
    }

    private Vehicle findVehicleById(Long vehicleId) {
        log.debug("Searching for vehicle with id: {}", vehicleId);
        return vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> {
                    log.warn("Vehicle with id {} not found.", vehicleId);
                    return new DataNotFoundException(
                            ExceptionType.VEHICLE_NOT_FOUND,
                            Map.of("id", vehicleId));
                });
    }

    public List<Vehicle> getAllVehiclesForUser(User currentUser) {
        log.info("Fetching all vehicles for user '{}'", currentUser.getEmail());

        List<Vehicle> vehicles = vehicleRepository.findVehiclesByUser(currentUser).stream().toList();
        log.info("<-Found-All-Vehicles-> completed. Found {} vehicles for user '{}'", vehicles.size(), currentUser.getEmail());

        return vehicles;
    }
}

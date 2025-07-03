package ua.torque.nexus.vehicle.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.torque.nexus.common.exception.DataConflictException;
import ua.torque.nexus.common.exception.DataNotFoundException;
import ua.torque.nexus.common.exception.ExceptionType;
import ua.torque.nexus.user.model.User;
import ua.torque.nexus.user.service.UserService;
import ua.torque.nexus.vehicle.model.Vehicle;
import ua.torque.nexus.vehicle.repository.VehicleRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final UserService userService;


    @Transactional
    public Vehicle saveNewVehicle(Vehicle newVehicle, User detachedCurrentUser) {
        log.info("event=vehicle_creation_started userId={} vin={}",
                detachedCurrentUser.getEmail(), newVehicle.getVinCode());

        assertVinCodeIsUnique(newVehicle.getVinCode());

        User managedUser = userService.getUserByEmail(detachedCurrentUser.getEmail());
        managedUser.addVehicle(newVehicle);

        Vehicle savedVehicle = vehicleRepository.save(newVehicle);
        log.info("event=vehicle_creation_finished status=success userId={} vehicleId={} vin={}",
                savedVehicle.getUser().getEmail(), savedVehicle.getId(), savedVehicle.getVinCode());

        return savedVehicle;
    }

    public Vehicle getVehicleByIdForUser(Long vehicleId, User currentUser) {
        log.info("event=vehicle_fetch_started userId={} vehicleId={}",
                currentUser.getEmail(), vehicleId);

        return vehicleRepository.findByIdAndUser(vehicleId, currentUser)
                .orElseThrow(() -> {
                    log.warn("event=vehicle_fetch_failed status=failure reason=\"Not Found or Access Denied\" userId={} vehicleId={}",
                            currentUser.getEmail(), vehicleId);
                    return new DataNotFoundException(
                            ExceptionType.VEHICLE_NOT_FOUND,
                            Map.of("id", vehicleId));
                });
    }

    public List<Vehicle> getAllVehiclesForUser(User currentUser) {
        log.info("event=all_vehicles_fetch_started userId={}", currentUser.getEmail());
        List<Vehicle> vehicles = vehicleRepository.findAllByUser(currentUser);
        log.info("event=all_vehicles_fetch_finished status=success userId={} count={}",
                currentUser.getEmail(), vehicles.size());
        return vehicles;
    }

    @Transactional
    public Vehicle patchVehicle(Long vehicleId, PatchVehicleParams params, User currentUser) {
        log.info("event=vehicle_patch_started userId={} vehicleId={}",
                currentUser.getEmail(), vehicleId);

        Vehicle vehicleToPatch = getVehicleByIdForUser(vehicleId, currentUser);

        if (params.vinCode() != null && !params.vinCode().equals(vehicleToPatch.getVinCode())) {
            assertVinCodeIsUnique(params.vinCode());
        }

        applyPatch(vehicleToPatch, params);
        Vehicle updatedVehicle = vehicleRepository.save(vehicleToPatch);

        log.info("event=vehicle_patch_finished status=success userId={} vehicleId={}",
                currentUser.getEmail(), vehicleId);
        return updatedVehicle;
    }

    @Transactional
    public void deleteVehicle(Long vehicleId, User currentUser) {
        log.info("event=vehicle_deletion_started userId={} vehicleId={}",
                currentUser.getEmail(), vehicleId);
        Vehicle vehicleToDelete = getVehicleByIdForUser(vehicleId, currentUser);
        vehicleRepository.delete(vehicleToDelete);
        log.info("event=vehicle_deletion_finished status=success userId={} vehicleId={}",
                currentUser.getEmail(), vehicleId);
    }


    private void assertVinCodeIsUnique(String vinCode) {
        log.debug("event=vin_uniqueness_check_started vin={}", vinCode);
        vehicleRepository.findByVinCode(vinCode).ifPresent(existingVehicle -> {
            log.warn("event=vin_uniqueness_check_failed status=failure reason=\"VIN already exists\" vin={}",
                    vinCode);
            throw new DataConflictException(
                    ExceptionType.VEHICLE_VIN_ALREADY_EXISTS,
                    Map.of("VIN", vinCode)
            );
        });
    }

    private void applyPatch(Vehicle vehicle, PatchVehicleParams params) {
        log.debug("event=apply_patch_started vehicleId={}", vehicle.getId());

        final List<String> updatedFields = new ArrayList<>();

        if (params.vinCode() != null) {
            vehicle.setVinCode(params.vinCode());
            updatedFields.add("vinCode");
        }
        if (params.mark() != null) {
            vehicle.setMark(params.mark());
            updatedFields.add("mark");
        }
        if (params.model() != null) {
            vehicle.setModel(params.model());
            updatedFields.add("model");
        }
        if (params.year() != null) {
            vehicle.setYear(params.year());
            updatedFields.add("year");
        }
        if (params.licensePlate() != null) {
            vehicle.setLicensePlate(params.licensePlate());
            updatedFields.add("licensePlate");
        }

        if (!updatedFields.isEmpty()) {
            log.debug("event=apply_patch_finished vehicleId={} updatedFields={}",
                    vehicle.getId(), updatedFields);
        } else {
            log.debug("event=apply_patch_finished vehicleId={} message=\"No fields were updated\"",
                    vehicle.getId());
        }
    }
}
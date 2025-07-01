package ua.torque.nexus.vehicle.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.torque.nexus.user.model.User;
import ua.torque.nexus.vehicle.dto.request.CreateVehicleRequest;
import ua.torque.nexus.vehicle.dto.request.UpdateVehicleRequest;
import ua.torque.nexus.vehicle.dto.response.VehicleResponse;
import ua.torque.nexus.vehicle.mapper.VehicleMapper;
import ua.torque.nexus.vehicle.mapper.VehicleResponseFactory;
import ua.torque.nexus.vehicle.model.Vehicle;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VehicleFacade {

    private final VehicleService vehicleService;
    private final VehicleMapper vehicleMapper;


    public VehicleResponse createVehicle(@Valid CreateVehicleRequest request, User currentUser) {
        log.debug("Vehicle creation process started for user '{}'", currentUser.getEmail());

        Vehicle newVehicle = vehicleMapper.toVehicle(request);
        log.debug("Mapped CreateVehicleRequest -> Vehicle for VIN '{}'", newVehicle.getVinCode());

        Vehicle savedVehicle = vehicleService.saveNewVehicle(newVehicle, currentUser);

        return VehicleResponseFactory.buildOnCreate(savedVehicle);
    }

    public VehicleResponse updateVehicle(Long vehicleId, @Valid UpdateVehicleRequest request, User currentUser) {
        log.debug("Vehicle update process started for vehicleId={} and user '{}'", vehicleId, currentUser.getEmail());

        PatchVehicleParams params = vehicleMapper.toParams(request);
        log.debug("Mapped DTO → PatchVehicleParams for vehicleId={}", vehicleId);

        Vehicle updatedVehicle = vehicleService.patchVehicle(vehicleId, params, currentUser);

        return VehicleResponseFactory.buildOnUpdate(updatedVehicle);
    }

    public void deleteVehicle(Long vehicleId, User currentUser) {
        log.debug("Vehicle deletion process started for vehicleId={} and user '{}'", vehicleId, currentUser.getEmail());

        vehicleService.deleteVehicle(vehicleId, currentUser);
    }

    public VehicleResponse getVehicleById(Long vehicleId, User currentUser) {
        log.debug("Fetching vehicle by id={} for user '{}'", vehicleId, currentUser.getEmail());

        Vehicle vehicle = vehicleService.getVehicleByIdForUser(vehicleId, currentUser);

        return vehicleMapper.toVehicleResponse(vehicle);
    }

    public List<VehicleResponse> getAllVehiclesForUser(User currentUser) {
        log.debug("Fetching all vehicles for user '{}'", currentUser.getEmail());

        List<Vehicle> vehicles = vehicleService.getAllVehiclesForUser(currentUser);

        return vehicleMapper.toVehicleResponseList(vehicles);
    }
}

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
        log.info("event=facade_create_vehicle_started userId={} vin={}",
                currentUser.getEmail(), request.getVinCode());

        Vehicle newVehicle = vehicleMapper.toVehicle(request);
        Vehicle savedVehicle = vehicleService.saveNewVehicle(newVehicle, currentUser);

        log.info("event=facade_create_vehicle_finished status=success userId={} vehicleId={}",
                currentUser.getEmail(), savedVehicle.getId());
        return VehicleResponseFactory.buildOnCreate(savedVehicle);
    }

    public VehicleResponse updateVehicle(Long vehicleId, @Valid UpdateVehicleRequest request, User currentUser) {
        log.info("event=facade_update_vehicle_started userId={} vehicleId={}",
                currentUser.getEmail(), vehicleId);

        PatchVehicleParams params = vehicleMapper.toParams(request);
        Vehicle updatedVehicle = vehicleService.patchVehicle(vehicleId, params, currentUser);

        log.info("event=facade_update_vehicle_finished status=success userId={} vehicleId={}",
                currentUser.getEmail(), updatedVehicle.getId());
        return VehicleResponseFactory.buildOnUpdate(updatedVehicle);
    }

    public void deleteVehicle(Long vehicleId, User currentUser) {
        log.info("event=facade_delete_vehicle_started userId={} vehicleId={}",
                currentUser.getEmail(), vehicleId);

        vehicleService.deleteVehicle(vehicleId, currentUser);

        log.info("event=facade_delete_vehicle_finished status=success userId={} vehicleId={}",
                currentUser.getEmail(), vehicleId);
    }

    public VehicleResponse getVehicleById(Long vehicleId, User currentUser) {
        log.info("event=facade_get_vehicle_by_id_started userId={} vehicleId={}",
                currentUser.getEmail(), vehicleId);

        Vehicle vehicle = vehicleService.getVehicleByIdForUser(vehicleId, currentUser);

        log.info("event=facade_get_vehicle_by_id_finished status=success userId={} vehicleId={}",
                currentUser.getEmail(), vehicleId);
        return vehicleMapper.toVehicleResponse(vehicle);
    }

    public List<VehicleResponse> getAllVehiclesForUser(User currentUser) {
        log.info("event=facade_get_all_vehicles_started userId={}", currentUser.getEmail());

        List<Vehicle> vehicles = vehicleService.getAllVehiclesForUser(currentUser);
        List<VehicleResponse> response = vehicleMapper.toVehicleResponseList(vehicles);

        log.info("event=facade_get_all_vehicles_finished status=success userId={} count={}",
                currentUser.getEmail(), response.size());
        return response;
    }
}
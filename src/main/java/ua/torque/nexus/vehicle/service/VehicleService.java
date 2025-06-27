package ua.torque.nexus.vehicle.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.torque.nexus.user.model.User;
import ua.torque.nexus.user.service.UserService;
import ua.torque.nexus.vehicle.dto.request.CreateVehicleRequest;
import ua.torque.nexus.vehicle.dto.request.UpdateVehicleRequest;
import ua.torque.nexus.vehicle.dto.response.VehicleResponse;
import ua.torque.nexus.vehicle.mapper.VehicleMapper;
import ua.torque.nexus.vehicle.model.Vehicle;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VehicleService {

    private final VehicleMapper vehicleMapper;
    private final UserService userService;


    public VehicleResponse createVehicle(@Valid CreateVehicleRequest request, String userEmail) {

        User user = userService.getUserByEmail(userEmail);

        Vehicle vehicle = vehicleMapper.createVehicleRequestToVehicle(request);

        saveVehicle(user, vehicle);

        VehicleResponse response = VehicleResponse.builder()
                .mark(vehicle.getMark())
                .vinCode(vehicle.getVinCode())
                .message("Vihicle successful — added")
                .build();

        log.info("Vehicle created: {}", vehicle);

        return response;
    }

    @Transactional
    public void saveVehicle(User user, Vehicle vehicle) {

    }



    public VehicleResponse updateVehicle(Long vehicleId, @Valid UpdateVehicleRequest updateRequest, String userEmail) {
        return null;
    }

    public void deleteVehicle(Long vehicleId, String userEmail) {

    }
}

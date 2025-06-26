package ua.torque.nexus.vehicle.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.torque.nexus.user.repository.UserRepository;
import ua.torque.nexus.user.service.UserService;
import ua.torque.nexus.vehicle.dto.request.CreateVehicleRequest;
import ua.torque.nexus.vehicle.dto.request.UpdateVehicleRequest;
import ua.torque.nexus.vehicle.dto.response.VehicleResponse;
import ua.torque.nexus.vehicle.mapper.VehicleMapper;
import ua.torque.nexus.vehicle.repository.VehicleRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VehicleService {

    private final VehicleMapper vehicleMapper;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final UserService userService;


    @Transactional
    public VehicleResponse createVehicle(@Valid CreateVehicleRequest request, String userEmail) {
        return null;
    }

    public VehicleResponse updateVehicle(Long vehicleId, @Valid UpdateVehicleRequest updateRequest, String userEmail) {
        return null;
    }

    public void deleteVehicle(Long vehicleId, String userEmail) {

    }
}

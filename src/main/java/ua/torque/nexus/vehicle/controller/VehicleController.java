package ua.torque.nexus.vehicle.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.torque.nexus.vehicle.dto.request.CreateVehicleRequest;
import ua.torque.nexus.vehicle.dto.request.UpdateVehicleRequest;
import ua.torque.nexus.vehicle.dto.response.VehicleResponse;
import ua.torque.nexus.vehicle.service.VehicleService;

@Slf4j
@RestController
@RequestMapping("/api/v1/customer/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;


    @PostMapping
    //@PreAuthorize("hasAuthority('CREATE_VEHICLE')")
    public ResponseEntity<VehicleResponse> createVehicle(
            @RequestBody @Valid CreateVehicleRequest createRequest,
            @AuthenticationPrincipal UserDetails userDetails) {

        String userEmail = userDetails.getUsername();

        log.info("Received request to create vehicle for user: {}", userEmail);

        VehicleResponse response = vehicleService.createVehicle(createRequest, userEmail);

        log.info("Successfully created vehicle with id {} for user {}", response.vinCode(), userEmail);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    @PatchMapping("/{vehicleId}")
    @PreAuthorize("hasAuthority('UPDATE_VEHICLE')")
    public ResponseEntity<VehicleResponse> updateVehicle(
            @PathVariable Long vehicleId,
            @RequestBody @Valid UpdateVehicleRequest updateRequest,
            @AuthenticationPrincipal UserDetails userDetails) {

        String userEmail = userDetails.getUsername();
        log.info("Received request to update vehicle id {} for user {}", vehicleId, userEmail);

        VehicleResponse response = vehicleService.updateVehicle(vehicleId, updateRequest, userEmail);

        log.info("Successfully updated vehicle id {} for user {}", vehicleId, userEmail);
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(response);
    }


    @DeleteMapping("/{vehicleId}")
    @PreAuthorize("hasAuthority('DELETE_VEHICLE')")
    public ResponseEntity<Void> deleteVehicle(
            @PathVariable Long vehicleId,
            @AuthenticationPrincipal UserDetails userDetails) {

        String userEmail = userDetails.getUsername();
        log.info("Received request to unlink vehicle id {} from user {}", vehicleId, userEmail);

        vehicleService.deleteVehicle(vehicleId, userEmail);

        log.info("Successfully unlinked vehicle id {} from user {}", vehicleId, userEmail);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
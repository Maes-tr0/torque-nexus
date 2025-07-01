package ua.torque.nexus.vehicle.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.torque.nexus.user.model.User;
import ua.torque.nexus.util.LocationUriBuilder;
import ua.torque.nexus.vehicle.dto.request.CreateVehicleRequest;
import ua.torque.nexus.vehicle.dto.request.UpdateVehicleRequest;
import ua.torque.nexus.vehicle.dto.response.VehicleResponse;
import ua.torque.nexus.vehicle.service.VehicleFacade;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleFacade vehicleFacade;


    @PostMapping
    public ResponseEntity<VehicleResponse> createVehicle(
            @RequestBody @Valid CreateVehicleRequest request,
            @AuthenticationPrincipal User currentUser) {
        log.info("POST /vehicles for user '{}'", currentUser.getEmail());

        VehicleResponse response = vehicleFacade.createVehicle(request, currentUser);

        URI uriLocation = LocationUriBuilder.buildLocationUriById(response.id());
        log.info("<-Vehicle-creation-> completed for user '{}' with vehicleId {}", currentUser.getEmail(), response.id());

        return ResponseEntity
                .created(uriLocation)
                .body(response);
    }
    
    @PatchMapping("/{vehicleId}")
    public ResponseEntity<VehicleResponse> updateVehicle(
            @PathVariable Long vehicleId,
            @RequestBody @Valid UpdateVehicleRequest request,
            @AuthenticationPrincipal User currentUser) {
        log.info("PATCH /vehicles/{} for user '{}'", vehicleId, currentUser.getEmail());

        VehicleResponse response = vehicleFacade.updateVehicle(vehicleId, request, currentUser);
        log.info("<-Vehicle-update-> completed for user '{}' with vehicleId {}", currentUser.getEmail(), vehicleId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/{vehicleId}")
    public ResponseEntity<VehicleResponse> getVehicle(
            @PathVariable Long vehicleId,
            @AuthenticationPrincipal User currentUser) {
        log.info("GET /vehicles/{} for user '{}'", vehicleId, currentUser.getEmail());

        VehicleResponse response = vehicleFacade.getVehicleById(vehicleId, currentUser);
        log.info("<-Vehicle-fetch-> completed for user '{}' with vehicleId {}", currentUser.getEmail(), vehicleId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<VehicleResponse>> getAllUserVehicles(
            @AuthenticationPrincipal User currentUser) {
        log.info("GET /vehicles for user '{}'", currentUser.getEmail());

        List<VehicleResponse> response = vehicleFacade.getAllVehiclesForUser(currentUser);
        log.info("<-Found {} vehicles-> for user '{}'", response.size(), currentUser.getEmail());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @DeleteMapping("/{vehicleId}")
    public ResponseEntity<Void> deleteVehicle(
            @PathVariable Long vehicleId,
            @AuthenticationPrincipal User currentUser) {
        log.info("DELETE /vehicles/{} for user '{}'", vehicleId, currentUser.getEmail());

        vehicleFacade.deleteVehicle(vehicleId, currentUser);
        log.info("<-Vehicle-deletion-> completed for user '{}' with vehicleId {}", currentUser.getEmail(), vehicleId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
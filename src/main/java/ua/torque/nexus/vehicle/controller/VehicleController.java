package ua.torque.nexus.vehicle.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        log.info("event=request_received httpMethod=POST path=/api/v1/vehicles userId={}",
                currentUser.getEmail());

        VehicleResponse response = vehicleFacade.createVehicle(request, currentUser);
        URI uriLocation = LocationUriBuilder.buildLocationUriById(response.id());

        log.info("event=request_completed httpMethod=POST path=/api/v1/vehicles status=success httpStatus=201 userId={} vehicleId={}",
                currentUser.getEmail(), response.id());
        return ResponseEntity.created(uriLocation).body(response);
    }

    @PatchMapping("/{vehicleId}")
    public ResponseEntity<VehicleResponse> updateVehicle(
            @PathVariable Long vehicleId,
            @RequestBody @Valid UpdateVehicleRequest request,
            @AuthenticationPrincipal User currentUser) {
        log.info("event=request_received httpMethod=PATCH path=/api/v1/vehicles/{} userId={}",
                vehicleId, currentUser.getEmail());

        VehicleResponse response = vehicleFacade.updateVehicle(vehicleId, request, currentUser);

        log.info("event=request_completed httpMethod=PATCH path=/api/v1/vehicles/{} status=success httpStatus=200 userId={}",
                vehicleId, currentUser.getEmail());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{vehicleId}")
    public ResponseEntity<VehicleResponse> getVehicle(
            @PathVariable Long vehicleId,
            @AuthenticationPrincipal User currentUser) {
        log.info("event=request_received httpMethod=GET path=/api/v1/vehicles/{} userId={}",
                vehicleId, currentUser.getEmail());

        VehicleResponse response = vehicleFacade.getVehicleById(vehicleId, currentUser);

        log.info("event=request_completed httpMethod=GET path=/api/v1/vehicles/{} status=success httpStatus=200 userId={}",
                vehicleId, currentUser.getEmail());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<VehicleResponse>> getAllUserVehicles(
            @AuthenticationPrincipal User currentUser) {
        log.info("event=request_received httpMethod=GET path=/api/v1/vehicles userId={}",
                currentUser.getEmail());

        List<VehicleResponse> response = vehicleFacade.getAllVehiclesForUser(currentUser);

        log.info("event=request_completed httpMethod=GET path=/api/v1/vehicles status=success httpStatus=200 userId={} count={}",
                currentUser.getEmail(), response.size());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{vehicleId}")
    public ResponseEntity<Void> deleteVehicle(
            @PathVariable Long vehicleId,
            @AuthenticationPrincipal User currentUser) {
        log.info("event=request_received httpMethod=DELETE path=/api/v1/vehicles/{} userId={}",
                vehicleId, currentUser.getEmail());

        vehicleFacade.deleteVehicle(vehicleId, currentUser);

        log.info("event=request_completed httpMethod=DELETE path=/api/v1/vehicles/{} status=success httpStatus=204 userId={}",
                vehicleId, currentUser.getEmail());
        return ResponseEntity.noContent().build();
    }
}
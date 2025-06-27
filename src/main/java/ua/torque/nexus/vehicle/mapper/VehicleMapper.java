package ua.torque.nexus.vehicle.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ua.torque.nexus.vehicle.dto.request.CreateVehicleRequest;
import ua.torque.nexus.vehicle.dto.response.VehicleResponse;
import ua.torque.nexus.vehicle.model.Vehicle;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface VehicleMapper {

    Vehicle createVehicleRequestToVehicle(CreateVehicleRequest request);

    VehicleResponse vehicleToVehicleDetailsResponse(Vehicle vehicle);
}

package ua.torque.nexus.vehicle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import ua.torque.nexus.vehicle.model.Vehicle;

@Transactional(readOnly = true)
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
}

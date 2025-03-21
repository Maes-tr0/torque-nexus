package ua.torque.nexus.feature.registration.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ua.torque.nexus.feature.registration.model.User;
import ua.torque.nexus.feature.registration.model.dto.RegistrationRequest;
import ua.torque.nexus.feature.registration.model.dto.RegistrationResponse;

import java.util.Optional;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RegistrationMapper {
    User toUser(RegistrationRequest request);

    RegistrationResponse toUserRegistrationResponse(User user);
}

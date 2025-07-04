package ua.torque.nexus.auth.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ua.torque.nexus.auth.dto.RegistrationRequest;
import ua.torque.nexus.user.model.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AuthMapper {

    User registrationRequestToUser(RegistrationRequest registrationRequest);
}

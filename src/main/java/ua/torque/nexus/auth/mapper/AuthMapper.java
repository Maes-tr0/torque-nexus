package ua.torque.nexus.auth.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ua.torque.nexus.auth.dto.RegistrationRequest;
import ua.torque.nexus.auth.dto.RegistrationResponse;
import ua.torque.nexus.auth.dto.ResetPasswordResponse;
import ua.torque.nexus.user.model.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AuthMapper {

    User registrationRequestToUser(RegistrationRequest registrationRequest);

    @Mapping(target = "message", constant = "Registration successful — please confirm your email")
    RegistrationResponse toRegistrationResponse(User user);

    @Mapping(target = "message", constant="Password successful changed")
    ResetPasswordResponse userToResetPasswordResponse(User user);
}

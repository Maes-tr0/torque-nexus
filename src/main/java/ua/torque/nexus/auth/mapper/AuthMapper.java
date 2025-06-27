package ua.torque.nexus.auth.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ua.torque.nexus.auth.dto.request.RegistrationRequest;
import ua.torque.nexus.user.model.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AuthMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "emailConfirmed", constant = "false")
    User toUser(RegistrationRequest request);
}


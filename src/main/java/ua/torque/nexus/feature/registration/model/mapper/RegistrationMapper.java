package ua.torque.nexus.feature.registration.model.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ua.torque.nexus.feature.registration.model.User;
import ua.torque.nexus.feature.registration.model.dto.RegistrationRequest;
import ua.torque.nexus.feature.registration.model.dto.RegistrationResponse;
import ua.torque.nexus.feature.registration.service.AccessControlService;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RegistrationMapper {

    @Mapping(target = "role", expression = "java(accessControlService.mapRole(request.getRole()).iterator().next())")
    User toUser(RegistrationRequest request, @Context AccessControlService accessControlService);

    RegistrationResponse toUserRegistrationResponse(User user);
}

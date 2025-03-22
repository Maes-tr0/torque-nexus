package ua.torque.nexus.feature.token.email.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ua.torque.nexus.feature.token.email.dto.ConfirmationEmailResponse;
import ua.torque.nexus.feature.token.email.model.ConfirmationToken;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ConfirmationEmailMapper {

    ConfirmationEmailResponse tokenToResponse(ConfirmationToken confirmedToken);
}

package ua.torque.nexus.feature.token.email.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ua.torque.nexus.feature.token.email.dto.ConfirmationEmailResponse;
import ua.torque.nexus.feature.token.email.model.ConfirmationToken;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ConfirmationEmailMapperTest {

    private final ConfirmationEmailMapper mapper = Mappers.getMapper(ConfirmationEmailMapper.class);

    @Test
    void tokenToResponse_mapsFieldsCorrectly() {
        LocalDateTime now = LocalDateTime.now();
        ConfirmationToken token = new ConfirmationToken();
        token.setToken("testToken");
        token.setConfirmedAt(now);

        ConfirmationEmailResponse response = mapper.tokenToResponse(token);

        assertThat(response.token()).isEqualTo("testToken");
        assertThat(response.confirmedAt()).isEqualTo(now);
        assertThat(response.message()).isEqualTo("Email successful confirmed");
    }
}

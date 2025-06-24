package ua.torque.nexus.access.config;

import lombok.Getter;
import org.springframework.stereotype.Component;
import ua.torque.nexus.access.model.permission.PermissionType;

import java.util.Set;

@Component
@Getter
public class PermissionConfig {

    private final Set<PermissionType> defaultAdminPermissionTypes = Set.of(
            PermissionType.LOGIN,
            PermissionType.LOGOUT,
            PermissionType.VERIFY_EMAIL,
            PermissionType.VIEW_PROFILE,
            PermissionType.UPDATE_PROFILE,
            PermissionType.CHANGE_PASSWORD,
            PermissionType.RESET_PASSWORD,
            PermissionType.DELETE_ACCOUNT,
            PermissionType.DELETE_ACCOUNTS
    );

    private final Set<PermissionType> defaultUserPermissionTypes = Set.of(
            PermissionType.REGISTER,
            PermissionType.LOGIN,
            PermissionType.VERIFY_EMAIL,
            PermissionType.VIEW_PROFILE,
            PermissionType.UPDATE_PROFILE,
            PermissionType.CHANGE_PASSWORD,
            PermissionType.RESET_PASSWORD,
            PermissionType.LOGOUT,
            PermissionType.DELETE_ACCOUNT
    );
}

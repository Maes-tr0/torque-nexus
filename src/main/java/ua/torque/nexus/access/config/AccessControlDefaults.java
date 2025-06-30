package ua.torque.nexus.access.config;

import ua.torque.nexus.access.model.permission.PermissionType;
import ua.torque.nexus.access.model.role.RoleType;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ua.torque.nexus.access.model.permission.PermissionType.*;

public class AccessControlDefaults {

    private static final Set<PermissionType> CAR_OWNER_PERMISSIONS = Set.of(
            PROFILE_VIEW_OWN,
            PROFILE_UPDATE_OWN,
            PASSWORD_CHANGE_OWN,
            ACCOUNT_DELETE_OWN,
            VEHICLE_CREATE_OWN,
            VEHICLE_UPDATE_OWN,
            VEHICLE_DELETE_OWN
    );
    private static final Set<PermissionType> ADMIN_PERMISSIONS = Stream.concat(
            CAR_OWNER_PERMISSIONS.stream(),
            Stream.of(
                    USER_VIEW_ANY,
                    USER_MANAGE_ROLES
            )
    ).collect(Collectors.toUnmodifiableSet());

    private AccessControlDefaults() {
    }

    public static Set<PermissionType> getPermissionsFor(RoleType roleType) {
        return switch (roleType) {
            case CAR_OWNER -> CAR_OWNER_PERMISSIONS;
            case ADMIN -> ADMIN_PERMISSIONS;
        };
    }
}

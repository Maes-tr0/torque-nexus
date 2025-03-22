package ua.torque.nexus.access.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@RequiredArgsConstructor
public enum RoleType {
    CUSTOMER("Customer"),
    ADMIN("Admin");

    private final String displayName;

    public static RoleType fromString(String roleName){
        if(roleName == null){
            log.warn("Role name is null, defaulting to 'CUSTOMER'");
            return CUSTOMER;
        }

        for(RoleType role : RoleType.values()){
            if(role.displayName.equals(roleName)){
                log.debug("Matched roleName '{}' to enum Role '{}'", roleName, role);
                return role;
            }
        }

        log.error("Unknown role '{}' passed to fromString()", roleName);
        throw new IllegalArgumentException("Invalid roleName '" + roleName + "'");
    }
}
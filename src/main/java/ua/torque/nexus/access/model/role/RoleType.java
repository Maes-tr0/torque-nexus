package ua.torque.nexus.access.model.role;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@RequiredArgsConstructor
public enum RoleType {
    CAR_OWNER("Car Owner"),
    ADMIN("Admin");

    private final String displayName;
}
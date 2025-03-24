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
}
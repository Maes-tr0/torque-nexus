package ua.torque.nexus.access.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PermissionType {
    CHANGE_PASSWORD("The user can change his password"),
    DELETE_ACCOUNT("The user can delete his account"),
    DELETE_ACCOUNTS("The user can delete accounts"),
    UPDATE_PASSWORD("The user can update his password"),;

    private final String description;
}

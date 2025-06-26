package ua.torque.nexus.access.model.permission;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PermissionType {
    REGISTER("The user can register an account"),
    LOGIN("The user can log in"),
    LOGOUT("The user can log out"),
    VERIFY_EMAIL("The user can verify their email address"),
    VIEW_PROFILE("The user can view their profile"),
    UPDATE_PROFILE("The user can update their profile"),
    CHANGE_PASSWORD("The user can change his password"),
    RESET_PASSWORD("The user can reset his password"),
    DELETE_ACCOUNT("The user can delete his account"),
    DELETE_ACCOUNTS("The user can delete accounts"),

    CREATE_VEHICLE("The user can add a new vehicle"),
    UPDATE_VEHICLE("The user can update their vehicle information"),
    DELETE_VEHICLE("The user can delete (unlink) their vehicle");

    private final String description;
}

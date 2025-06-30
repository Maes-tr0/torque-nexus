package ua.torque.nexus.access.model.permission;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PermissionType {
    PROFILE_VIEW_OWN("Allows viewing one's own profile"),
    PROFILE_UPDATE_OWN("Allows updating one's own profile"),
    PASSWORD_CHANGE_OWN("Allows changing one's own password"),
    ACCOUNT_DELETE_OWN("Allows deleting one's own account"),

    VEHICLE_CREATE_OWN("Allows adding a vehicle to one's own profile"),
    VEHICLE_UPDATE_OWN("Allows updating one's own vehicles"),
    VEHICLE_DELETE_OWN("Allows deleting one's own vehicles"),

    USER_VIEW_ANY("Allows viewing any user's profile"),
    USER_MANAGE_ROLES("Allows assigning and revoking user roles");


    private final String description;
}
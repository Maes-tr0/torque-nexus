package ua.torque.nexus.feature.registration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.torque.nexus.feature.registration.model.Permission;
import ua.torque.nexus.feature.registration.model.Role;
import ua.torque.nexus.feature.registration.model.User;
import ua.torque.nexus.feature.registration.model.PermissionType;
import ua.torque.nexus.feature.registration.model.RoleType;
import ua.torque.nexus.feature.registration.repository.PermissionRepository;
import ua.torque.nexus.feature.registration.repository.RoleRepository;
import ua.torque.nexus.feature.registration.repository.UserRepository;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccessControlService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;

    public Role createRole(String name) {
        Optional<Role> existingRole = roleRepository.findByName(name);
        if (existingRole.isPresent()) {
            log.warn("Role with name {} already exists", name);
            return existingRole.get();
        }
        Role role = new Role();
        role.setName(name);
        Role savedRole = roleRepository.save(role);
        log.info("Created new role: {}", savedRole);
        return savedRole;
    }

    public Permission createPermission(String name, String description) {
        Optional<Permission> existingPermission = permissionRepository.findByName(name);
        if (existingPermission.isPresent()) {
            log.warn("Permission with name {} already exists", name);
            return existingPermission.get();
        }
        Permission permission = new Permission();
        permission.setName(name);
        permission.setDescription(description);
        Permission savedPermission = permissionRepository.save(permission);
        log.info("Created new permission: {}", savedPermission);
        return savedPermission;
    }

    public void assignPermissionToRole(String roleName, String permissionName) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
        Permission permission = permissionRepository.findByName(permissionName)
                .orElseThrow(() -> new RuntimeException("Permission not found: " + permissionName));
        role.getPermissions().add(permission);
        roleRepository.save(role);
        log.info("Assigned permission {} to role {}", permissionName, roleName);
    }

    public void assignRoleToUser(Long userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
        user.getRoles().add(role);
        userRepository.save(user);
        log.info("Assigned role {} to user with id {}", roleName, userId);
    }

    public Set<Role> mapRole(String roleString) {
        RoleType roleType;
        if (roleString == null || roleString.trim().isEmpty()) {
            roleType = RoleType.CUSTOMER;
        } else {
            try {
                roleType = RoleType.valueOf(roleString.toUpperCase());
            } catch (IllegalArgumentException e) {
                roleType = RoleType.CUSTOMER;
            }
        }

        RoleType finalRoleType = roleType;
        Role role = roleRepository.findByName(roleType.name()).orElseGet(() -> {
            Role newRole = new Role();
            newRole.setName(finalRoleType.name());

            if (finalRoleType == RoleType.CUSTOMER) {
                Permission permission = permissionRepository.findByName(PermissionType.READ.name())
                        .orElseGet(() ->
                                createPermission(PermissionType.READ.name(),
                                        "Default read permission"));
                newRole.getPermissions().add(permission);
            } else if (finalRoleType == RoleType.ADMIN) {
                Permission readPermission = permissionRepository.findByName(PermissionType.READ.name())
                        .orElseGet(() ->
                                createPermission(PermissionType.READ.name(),
                                        "Default read permission"));
                Permission writePermission = permissionRepository.findByName(PermissionType.WRITE.name())
                        .orElseGet(() ->
                                createPermission(PermissionType.WRITE.name(),
                                        "Default write permission"));
                Permission deletePermission = permissionRepository.findByName(PermissionType.DELETE.name())
                        .orElseGet(() ->
                                createPermission(PermissionType.DELETE.name(),
                                        "Default delete permission"));
                newRole.getPermissions().add(readPermission);
                newRole.getPermissions().add(writePermission);
                newRole.getPermissions().add(deletePermission);
            }
            return roleRepository.save(newRole);
        });

        return new HashSet<>(Collections.singleton(role));
    }
}

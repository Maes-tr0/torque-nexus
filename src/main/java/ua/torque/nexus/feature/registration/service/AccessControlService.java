package ua.torque.nexus.feature.registration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.torque.nexus.feature.registration.model.Permission;
import ua.torque.nexus.feature.registration.model.Role;
import ua.torque.nexus.feature.registration.model.User;
import ua.torque.nexus.feature.registration.repository.PermissionRepository;
import ua.torque.nexus.feature.registration.repository.RoleRepository;
import ua.torque.nexus.feature.registration.repository.UserRepository;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccessControlService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;

    public Role createRole(String name, String description) {
        Optional<Role> existingRole = roleRepository.findByName(name);
        if (existingRole.isPresent()) {
            log.warn("Роль з назвою {} вже існує", name);
            return existingRole.get();
        }
        Role role = new Role();
        role.setName(name);
        Role savedRole = roleRepository.save(role);
        log.info("Створено нову роль: {}", savedRole);
        return savedRole;
    }

    public Permission createPermission(String name, String description) {
        Optional<Permission> existingPermission = permissionRepository.findByName(name);
        if (existingPermission.isPresent()) {
            log.warn("Дозвіл з назвою {} вже існує", name);
            return existingPermission.get();
        }
        Permission permission = new Permission();
        permission.setName(name);
        permission.setDescription(description);
        Permission savedPermission = permissionRepository.save(permission);
        log.info("Створено новий дозвіл: {}", savedPermission);
        return savedPermission;
    }

    public void assignPermissionToRole(String roleName, String permissionName) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Роль не знайдена: " + roleName));
        Permission permission = permissionRepository.findByName(permissionName)
                .orElseThrow(() -> new RuntimeException("Дозвіл не знайдено: " + permissionName));
        role.getPermissions().add(permission);
        roleRepository.save(role);
        log.info("Призначено дозвіл {} ролі {}", permissionName, roleName);
    }

    public void assignRoleToUser(Long userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Користувача не знайдено з id: " + userId));
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Роль не знайдена: " + roleName));
        user.getRoles().add(role);
        userRepository.save(user);
        log.info("Призначено роль {} користувачу з id {}", roleName, userId);
    }
}
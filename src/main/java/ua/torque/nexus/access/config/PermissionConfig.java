package ua.torque.nexus.access.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ua.torque.nexus.access.model.Permission;
import ua.torque.nexus.access.model.PermissionType;
import ua.torque.nexus.access.repository.PermissionRepository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class PermissionConfig {

    private final PermissionRepository permissionRepository;

    @Bean
    public Set<Permission> defaultAdminPermissions() {
        List<Permission> permissions = permissionRepository.findByTypeIn(
                Arrays.asList(
                        PermissionType.LOGIN,
                        PermissionType.LOGOUT,
                        PermissionType.VERIFY_EMAIL,
                        PermissionType.VIEW_PROFILE,
                        PermissionType.UPDATE_PROFILE,
                        PermissionType.CHANGE_PASSWORD,
                        PermissionType.RESET_PASSWORD,
                        PermissionType.DELETE_ACCOUNT,
                        PermissionType.DELETE_ACCOUNTS
                )
        );
        return new HashSet<>(permissions);
    }

    @Bean
    public Set<Permission> defaultUserPermissions() {
        List<Permission> permissions = permissionRepository.findByTypeIn(
                Arrays.asList(
                        PermissionType.REGISTER,
                        PermissionType.LOGIN,
                        PermissionType.VERIFY_EMAIL,
                        PermissionType.VIEW_PROFILE,
                        PermissionType.UPDATE_PROFILE,
                        PermissionType.CHANGE_PASSWORD,
                        PermissionType.RESET_PASSWORD,
                        PermissionType.LOGOUT,
                        PermissionType.DELETE_ACCOUNT
                )
        );
        return new HashSet<>(permissions);
    }

}

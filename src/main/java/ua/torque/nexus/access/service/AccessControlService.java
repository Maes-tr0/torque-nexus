package ua.torque.nexus.access.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.torque.nexus.access.exception.RoleNotFoundException;
import ua.torque.nexus.access.model.role.Role;
import ua.torque.nexus.access.model.role.RoleType;
import ua.torque.nexus.access.repository.RoleRepository;
import ua.torque.nexus.common.exception.ExceptionType;
import ua.torque.nexus.user.model.User;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccessControlService {

    private final RoleRepository roleRepository;

    @Transactional
    public User assignDefaultRoleToUser(User user) {
        return assignRole(user, RoleType.CAR_OWNER);
    }

    @Transactional
    public User assignRoleToUser(User user, RoleType roleType) {
        return assignRole(user, roleType);
    }

    public Role findRoleByType(RoleType roleType) {
        log.debug("Fetching role by type: {}", roleType);
        return roleRepository.findByType(roleType)
                .orElseThrow(() -> new RoleNotFoundException(ExceptionType.UNSUPPORTED_ROLE_TYPE));
    }

    private User assignRole(User user, RoleType roleType) {
        Role roleToAssign = findRoleByType(roleType);
        user.setRole(roleToAssign);
        log.info("Assigned role '{}' to user {}", roleType.name(), user.getEmail());
        return user;
    }
}

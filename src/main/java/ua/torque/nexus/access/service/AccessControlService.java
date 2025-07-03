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
        log.info("event=assign_default_role_started email={}", user.getEmail());
        return assignRole(user, RoleType.CAR_OWNER);
    }

    @Transactional
    public User assignRoleToUser(User user, RoleType roleType) {
        log.info("event=assign_specific_role_started email={} roleType={}", user.getEmail(), roleType);
        return assignRole(user, roleType);
    }

    public Role findRoleByType(RoleType roleType) {
        log.debug("event=role_fetch_by_type_started roleType={}", roleType);
        return roleRepository.findByType(roleType)
                .orElseThrow(() -> {
                    log.error("event=role_fetch_by_type_failed status=failure reason=\"Role not found in DB\" roleType={}", roleType);
                    return new RoleNotFoundException(ExceptionType.UNSUPPORTED_ROLE_TYPE);
                });
    }

    private User assignRole(User user, RoleType roleType) {
        Role roleToAssign = findRoleByType(roleType);
        user.setRole(roleToAssign);
        log.info("event=role_assignment_finished status=success email={} roleType={}", user.getEmail(), roleType);
        return user;
    }
}
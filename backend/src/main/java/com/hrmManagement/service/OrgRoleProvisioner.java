package com.hrmManagement.service;

import com.hrmManagement.entity.Organization;
import com.hrmManagement.entity.Permission;
import com.hrmManagement.entity.Role;
import com.hrmManagement.entity.User;
import com.hrmManagement.entity.enums.DataScope;
import com.hrmManagement.entity.enums.PermissionAction;
import com.hrmManagement.entity.enums.RoleScope;
import com.hrmManagement.repository.PermissionRepository;
import com.hrmManagement.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.hrmManagement.entity.enums.PermissionAction.*;

/**
 * Creates the built-in roles every organization gets: Owner, HR Admin, Branch Manager, Team Lead
 * and Employee. A role pairs permissions (what the user may do, per HR module) with a
 * {@link DataScope} (which employees' rows they may see). Provisioning is idempotent and never
 * overwrites a role that already exists, so an organization's edits to a system role survive.
 */
@Service
@RequiredArgsConstructor
public class OrgRoleProvisioner {

    public static final String OWNER = "ORG_OWNER";
    public static final String HR_ADMIN = "HR_ADMIN";
    public static final String BRANCH_MANAGER = "BRANCH_MANAGER";
    public static final String TEAM_LEAD = "TEAM_LEAD";
    public static final String EMPLOYEE = "EMPLOYEE";

    /** HR permission modules; authority strings are {@code <MODULE>_<ACTION>}. */
    public static final List<String> MODULES = List.of(
            "BRANCH", "DEPARTMENT", "EMPLOYEE", "ATTENDANCE", "SHIFT", "LEAVE",
            "PAYROLL", "PAYSLIP", "ASSET", "DOCUMENT", "REQUEST");

    private record RoleDef(String name, String description, DataScope dataScope, Map<String, Set<PermissionAction>> grants) {
    }

    private static Set<PermissionAction> actions(PermissionAction... a) {
        return a.length == 0 ? EnumSet.noneOf(PermissionAction.class) : EnumSet.copyOf(Arrays.asList(a));
    }

    private static Map<String, Set<PermissionAction>> allExcept(String module, PermissionAction... excluded) {
        Map<String, Set<PermissionAction>> grants = new LinkedHashMap<>();
        for (String m : MODULES) {
            grants.put(m, EnumSet.allOf(PermissionAction.class));
        }
        grants.get(module).removeAll(Arrays.asList(excluded));
        return grants;
    }

    private static Map<String, Set<PermissionAction>> grants(Object... pairs) {
        Map<String, Set<PermissionAction>> grants = new LinkedHashMap<>();
        for (int i = 0; i < pairs.length; i += 2) {
            grants.put((String) pairs[i], (Set<PermissionAction>) pairs[i + 1]);
        }
        return grants;
    }

    private static final List<RoleDef> DEFINITIONS = List.of(
            new RoleDef(OWNER, "Organization owner: full access to the organization", DataScope.ORGANIZATION,
                    allExcept("BRANCH")),
            new RoleDef(HR_ADMIN, "HR administrator: manages employees, leave, attendance and payroll", DataScope.ORGANIZATION,
                    allExcept("PAYROLL", APPROVE)),
            new RoleDef(BRANCH_MANAGER, "Manages the employees of their own branch", DataScope.BRANCH,
                    grants("BRANCH", actions(VIEW), "DEPARTMENT", actions(VIEW), "EMPLOYEE", actions(VIEW),
                            "ATTENDANCE", actions(VIEW, CREATE, MANAGE), "SHIFT", actions(VIEW, CREATE, UPDATE, DELETE),
                            "LEAVE", actions(VIEW, CREATE, APPROVE), "ASSET", actions(VIEW), "DOCUMENT", actions(VIEW),
                            "REQUEST", actions(VIEW, CREATE, APPROVE), "PAYSLIP", actions(VIEW))),
            new RoleDef(TEAM_LEAD, "Manages their direct reports", DataScope.TEAM,
                    grants("EMPLOYEE", actions(VIEW), "ATTENDANCE", actions(VIEW, CREATE), "SHIFT", actions(VIEW),
                            "LEAVE", actions(VIEW, CREATE, APPROVE), "REQUEST", actions(VIEW, CREATE, APPROVE),
                            "PAYSLIP", actions(VIEW), "ASSET", actions(VIEW), "DOCUMENT", actions(VIEW))),
            new RoleDef(EMPLOYEE, "Regular employee: own attendance, leave, payslips and requests", DataScope.SELF,
                    grants("EMPLOYEE", actions(VIEW), "BRANCH", actions(VIEW), "DEPARTMENT", actions(VIEW),
                            "ATTENDANCE", actions(VIEW, CREATE), "SHIFT", actions(VIEW), "LEAVE", actions(VIEW, CREATE),
                            "PAYSLIP", actions(VIEW), "ASSET", actions(VIEW), "DOCUMENT", actions(VIEW),
                            "REQUEST", actions(VIEW, CREATE))));

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Transactional
    public void provision(Organization organization) {
        for (RoleDef def : DEFINITIONS) {
            findOrCreate(organization, def);
        }
    }

    /** The organization's EMPLOYEE role, created on demand. */
    @Transactional
    public Role employeeRole(Organization organization) {
        return findOrCreate(organization, DEFINITIONS.stream().filter(d -> d.name().equals(EMPLOYEE)).findFirst().orElseThrow());
    }

    @Transactional
    public Role ownerRole(Organization organization) {
        return findOrCreate(organization, DEFINITIONS.stream().filter(d -> d.name().equals(OWNER)).findFirst().orElseThrow());
    }

    /** Gives the owner user the ORG_OWNER role if they do not have it yet. */
    @Transactional
    public void grantOwnerRole(Organization organization, User owner) {
        Role role = ownerRole(organization);
        if (owner.getRoles().stream().noneMatch(r -> r.getId().equals(role.getId()))) {
            owner.getRoles().add(role);
        }
    }

    private Role findOrCreate(Organization organization, RoleDef def) {
        return roleRepository.findByOrganizationIdAndNameIgnoreCase(organization.getId(), def.name())
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setOrganization(organization);
                    role.setName(def.name());
                    role.setDescription(def.description());
                    role.setScope(RoleScope.ORGANIZATION);
                    role.setDataScope(def.dataScope());
                    role.setSystem(true);
                    Set<Permission> permissions = new HashSet<>();
                    def.grants().forEach((module, acts) -> acts.forEach(a -> permissions.add(permission(module, a))));
                    role.setPermissions(permissions);
                    return roleRepository.save(role);
                });
    }

    private Permission permission(String module, PermissionAction action) {
        return permissionRepository.findByModuleIgnoreCaseAndAction(module, action).orElseGet(() -> {
            Permission permission = new Permission();
            permission.setModule(module);
            permission.setAction(action);
            return permissionRepository.save(permission);
        });
    }
}

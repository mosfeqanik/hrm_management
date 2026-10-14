package com.hrmManagement.repository;

import com.hrmManagement.entity.Permission;
import com.hrmManagement.entity.enums.PermissionAction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

    java.util.Optional<Permission> findByModuleIgnoreCaseAndAction(String module, PermissionAction action);

    boolean existsByModuleIgnoreCaseAndAction(String module, PermissionAction action);

    boolean existsByModuleIgnoreCaseAndActionAndIdNot(String module, PermissionAction action, Long id);
}

package com.sassmodel.repository;

import com.sassmodel.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long> {

    List<Role> findByOrganizationIdIsNull();

    List<Role> findByOrganizationIdOrOrganizationIdIsNull(Long organizationId);

    boolean existsByOrganizationIdAndNameIgnoreCase(Long organizationId, String name);

    boolean existsByOrganizationIdAndNameIgnoreCaseAndIdNot(Long organizationId, String name, Long id);

    boolean existsByOrganizationIdIsNullAndNameIgnoreCase(String name);

    boolean existsByOrganizationIdIsNullAndNameIgnoreCaseAndIdNot(String name, Long id);
}

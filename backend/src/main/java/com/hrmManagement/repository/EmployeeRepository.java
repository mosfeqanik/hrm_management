package com.hrmManagement.repository;

import com.hrmManagement.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findByOrganizationId(Long organizationId);

    long countByOrganizationId(Long organizationId);

    Optional<Employee> findByUserId(Long userId);

    List<Employee> findByOrganizationIdAndBranchId(Long organizationId, Long branchId);

    List<Employee> findByManagerId(Long managerId);

    boolean existsByOrganizationIdAndEmployeeCodeIgnoreCase(Long organizationId, String employeeCode);

    boolean existsByOrganizationIdAndEmployeeCodeIgnoreCaseAndIdNot(Long organizationId, String employeeCode, Long id);
}

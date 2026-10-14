package com.sassmodel.repository;

import com.sassmodel.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findByOrganizationId(Long organizationId);
}

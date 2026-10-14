package com.hrmManagement.repository;

import com.hrmManagement.entity.*;
import com.hrmManagement.entity.enums.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.*;
import java.util.*;

public interface PayrollRunRepository extends JpaRepository<PayrollRun, Long> {

    List<PayrollRun> findByOrganizationId(Long organizationId);

    long countByOrganizationId(Long organizationId);

    boolean existsByOrganizationIdAndYearAndMonthAndBranchId(Long organizationId, int year, int month, Long branchId);

    boolean existsByOrganizationIdAndYearAndMonthAndBranchIsNull(Long organizationId, int year, int month);
}

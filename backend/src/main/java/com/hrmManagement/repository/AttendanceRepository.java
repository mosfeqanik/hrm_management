package com.hrmManagement.repository;

import com.hrmManagement.entity.*;
import com.hrmManagement.entity.enums.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.*;
import java.util.*;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> findByOrganizationId(Long organizationId);

    long countByOrganizationId(Long organizationId);

    Optional<Attendance> findByEmployeeIdAndWorkDate(Long employeeId, LocalDate workDate);

    List<Attendance> findByEmployeeIdAndWorkDateBetween(Long employeeId, LocalDate from, LocalDate to);
}

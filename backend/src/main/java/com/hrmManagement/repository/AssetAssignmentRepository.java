package com.hrmManagement.repository;

import com.hrmManagement.entity.*;
import com.hrmManagement.entity.enums.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.*;
import java.util.*;

public interface AssetAssignmentRepository extends JpaRepository<AssetAssignment, Long> {

    List<AssetAssignment> findByOrganizationId(Long organizationId);

    long countByOrganizationId(Long organizationId);

    Optional<AssetAssignment> findByAssetIdAndReturnedAtIsNull(Long assetId);
}

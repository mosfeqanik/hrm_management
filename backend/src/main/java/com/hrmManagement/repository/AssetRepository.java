package com.hrmManagement.repository;

import com.hrmManagement.entity.*;
import com.hrmManagement.entity.enums.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.*;
import java.util.*;

public interface AssetRepository extends JpaRepository<Asset, Long> {

    List<Asset> findByOrganizationId(Long organizationId);

    long countByOrganizationId(Long organizationId);
}

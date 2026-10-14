package com.sassmodel.repository;

import com.sassmodel.entity.MenuImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuImageRepository extends JpaRepository<MenuImage, Long> {

    List<MenuImage> findByOrganizationIdOrderBySortOrderAsc(Long organizationId);
}

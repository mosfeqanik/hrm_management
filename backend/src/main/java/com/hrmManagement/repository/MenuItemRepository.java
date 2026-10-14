package com.hrmManagement.repository;

import com.hrmManagement.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    List<MenuItem> findByOrganizationId(Long organizationId);

    List<MenuItem> findByCategoryId(Long categoryId);
}

package com.sassmodel.repository;

import com.sassmodel.entity.SalesActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SalesActivityRepository extends JpaRepository<SalesActivity, Long> {

    List<SalesActivity> findByLeadIdOrderByIdDesc(Long leadId);
}

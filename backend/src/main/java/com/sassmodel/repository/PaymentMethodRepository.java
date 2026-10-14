package com.sassmodel.repository;

import com.sassmodel.entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {

    List<PaymentMethod> findByOrganizationId(Long organizationId);

    List<PaymentMethod> findByOrganizationIdAndDefaultMethodTrue(Long organizationId);
}

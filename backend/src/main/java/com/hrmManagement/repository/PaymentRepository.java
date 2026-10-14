package com.hrmManagement.repository;

import com.hrmManagement.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByOrganizationId(Long organizationId);

    List<Payment> findByInvoiceId(Long invoiceId);
}

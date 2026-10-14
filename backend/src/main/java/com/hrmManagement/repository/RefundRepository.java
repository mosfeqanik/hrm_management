package com.hrmManagement.repository;

import com.hrmManagement.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RefundRepository extends JpaRepository<Refund, Long> {

    List<Refund> findByPaymentId(Long paymentId);

    List<Refund> findByPayment_Organization_Id(Long organizationId);
}

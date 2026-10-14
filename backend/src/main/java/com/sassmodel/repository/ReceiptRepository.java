package com.sassmodel.repository;

import com.sassmodel.entity.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {

    List<Receipt> findByOrganizationId(Long organizationId);

    Optional<Receipt> findByPaymentId(Long paymentId);
}

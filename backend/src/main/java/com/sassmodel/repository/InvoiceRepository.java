package com.sassmodel.repository;

import com.sassmodel.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    List<Invoice> findByOrganizationId(Long organizationId);

    boolean existsByInvoiceNumberIgnoreCase(String invoiceNumber);

    boolean existsByInvoiceNumberIgnoreCaseAndIdNot(String invoiceNumber, Long id);
}

package com.sassmodel.dto;

import com.sassmodel.entity.enums.InvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record InvoiceResponse(
        Long id,
        Long organizationId,
        Long subscriptionId,
        String invoiceNumber,
        BigDecimal amount,
        String currency,
        InvoiceStatus status,
        LocalDate issueDate,
        LocalDate dueDate,
        LocalDateTime paidAt,
        String notes
) {
}

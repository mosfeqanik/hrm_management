package com.sassmodel.dto;

import com.sassmodel.entity.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(
        Long id,
        Long organizationId,
        Long invoiceId,
        Long paymentMethodId,
        BigDecimal amount,
        String currency,
        PaymentStatus status,
        String gateway,
        String gatewayTransactionId,
        LocalDateTime paidAt,
        boolean hasReceipt
) {
}

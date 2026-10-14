package com.sassmodel.mapper;

import com.sassmodel.dto.PaymentResponse;
import com.sassmodel.entity.Payment;

public final class PaymentMapper {

    private PaymentMapper() {
    }

    public static PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getOrganization().getId(),
                payment.getInvoice().getId(),
                payment.getPaymentMethod() != null ? payment.getPaymentMethod().getId() : null,
                payment.getAmount(),
                payment.getCurrency(),
                payment.getStatus(),
                payment.getGateway(),
                payment.getGatewayTransactionId(),
                payment.getPaidAt(),
                payment.getReceipt() != null
        );
    }
}

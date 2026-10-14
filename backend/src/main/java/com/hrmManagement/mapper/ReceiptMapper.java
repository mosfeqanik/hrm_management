package com.hrmManagement.mapper;

import com.hrmManagement.dto.ReceiptResponse;
import com.hrmManagement.entity.Receipt;

public final class ReceiptMapper {

    private ReceiptMapper() {
    }

    public static ReceiptResponse toResponse(Receipt receipt) {
        return new ReceiptResponse(
                receipt.getId(),
                receipt.getOrganization().getId(),
                receipt.getPayment().getId(),
                receipt.getReceiptNumber(),
                receipt.getAmount(),
                receipt.getIssuedAt()
        );
    }
}

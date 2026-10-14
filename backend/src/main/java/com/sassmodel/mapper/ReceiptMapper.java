package com.sassmodel.mapper;

import com.sassmodel.dto.ReceiptResponse;
import com.sassmodel.entity.Receipt;

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

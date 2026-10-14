package com.sassmodel.mapper;

import com.sassmodel.dto.InvoiceResponse;
import com.sassmodel.entity.Invoice;

public final class InvoiceMapper {

    private InvoiceMapper() {
    }

    public static InvoiceResponse toResponse(Invoice invoice) {
        return new InvoiceResponse(
                invoice.getId(),
                invoice.getOrganization().getId(),
                invoice.getSubscription() != null ? invoice.getSubscription().getId() : null,
                invoice.getInvoiceNumber(),
                invoice.getAmount(),
                invoice.getCurrency(),
                invoice.getStatus(),
                invoice.getIssueDate(),
                invoice.getDueDate(),
                invoice.getPaidAt(),
                invoice.getNotes()
        );
    }
}

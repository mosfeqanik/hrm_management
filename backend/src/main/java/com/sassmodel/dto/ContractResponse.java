package com.sassmodel.dto;

import com.sassmodel.entity.enums.ContractStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ContractResponse(
        Long id,
        Long organizationId,
        String title,
        String counterpartyName,
        ContractStatus status,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal value,
        String currency,
        String terms
) {
}

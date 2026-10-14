package com.sassmodel.dto;

import com.sassmodel.entity.enums.ContractStatus;

import java.time.LocalDateTime;

public record ContractHistoryResponse(
        Long id,
        Long contractId,
        ContractStatus fromStatus,
        ContractStatus toStatus,
        Long changedByUserId,
        String changedByName,
        String note,
        LocalDateTime changedAt
) {
}

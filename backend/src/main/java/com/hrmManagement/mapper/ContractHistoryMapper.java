package com.hrmManagement.mapper;

import com.hrmManagement.dto.ContractHistoryResponse;
import com.hrmManagement.entity.ContractHistory;

public final class ContractHistoryMapper {

    private ContractHistoryMapper() {
    }

    public static ContractHistoryResponse toResponse(ContractHistory history) {
        return new ContractHistoryResponse(
                history.getId(),
                history.getContract().getId(),
                history.getFromStatus(),
                history.getToStatus(),
                history.getChangedBy() != null ? history.getChangedBy().getId() : null,
                history.getChangedBy() != null ? history.getChangedBy().getFullName() : null,
                history.getNote(),
                history.getChangedAt()
        );
    }
}

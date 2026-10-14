package com.hrmManagement.mapper;

import com.hrmManagement.dto.ContractResponse;
import com.hrmManagement.entity.Contract;

public final class ContractMapper {

    private ContractMapper() {
    }

    public static ContractResponse toResponse(Contract contract) {
        return new ContractResponse(
                contract.getId(),
                contract.getOrganization().getId(),
                contract.getTitle(),
                contract.getCounterpartyName(),
                contract.getStatus(),
                contract.getStartDate(),
                contract.getEndDate(),
                contract.getValue(),
                contract.getCurrency(),
                contract.getTerms()
        );
    }
}

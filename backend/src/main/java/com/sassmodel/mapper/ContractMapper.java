package com.sassmodel.mapper;

import com.sassmodel.dto.ContractResponse;
import com.sassmodel.entity.Contract;

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

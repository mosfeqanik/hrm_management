package com.hrmManagement.mapper;

import com.hrmManagement.dto.EmployeePaymentAccountResponse;
import com.hrmManagement.entity.EmployeePaymentAccount;

public final class EmployeePaymentAccountMapper {

    private EmployeePaymentAccountMapper() {
    }

    public static EmployeePaymentAccountResponse toResponse(EmployeePaymentAccount entity) {
        return new EmployeePaymentAccountResponse(
                entity.getId(),
                entity.getOrganization().getId(),
                entity.getEmployee().getId(),
                entity.getType(),
                entity.getProvider(),
                entity.getAccountName(),
                entity.getAccountNumber(),
                entity.isPrimaryAccount()
        );
    }
}

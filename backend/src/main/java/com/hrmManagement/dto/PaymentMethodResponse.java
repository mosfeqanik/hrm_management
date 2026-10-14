package com.hrmManagement.dto;

import com.hrmManagement.entity.enums.PaymentMethodType;

public record PaymentMethodResponse(
        Long id,
        Long organizationId,
        PaymentMethodType type,
        String maskedDetails,
        boolean defaultMethod
) {
}

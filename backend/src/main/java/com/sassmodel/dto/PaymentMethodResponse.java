package com.sassmodel.dto;

import com.sassmodel.entity.enums.PaymentMethodType;

public record PaymentMethodResponse(
        Long id,
        Long organizationId,
        PaymentMethodType type,
        String maskedDetails,
        boolean defaultMethod
) {
}

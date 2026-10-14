package com.sassmodel.entity;

import com.sassmodel.entity.base.TenantEntity;
import com.sassmodel.entity.enums.PaymentMethodType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "payment_methods")
public class PaymentMethod extends TenantEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentMethodType type;

    /** Masked display value such as **** 4242 */
    @Column(name = "masked_details", length = 50)
    private String maskedDetails;

    @Column(name = "gateway_token", length = 255)
    private String gatewayToken;

    @Column(name = "is_default", nullable = false)
    private boolean defaultMethod = false;
}

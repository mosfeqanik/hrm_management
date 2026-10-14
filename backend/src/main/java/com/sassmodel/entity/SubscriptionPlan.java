package com.sassmodel.entity;

import com.sassmodel.entity.base.BaseEntity;
import com.sassmodel.entity.enums.BillingCycle;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "subscription_plans")
public class SubscriptionPlan extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(nullable = false, length = 3)
    private String currency = "USD";

    @Enumerated(EnumType.STRING)
    @Column(name = "billing_cycle", nullable = false, length = 30)
    private BillingCycle billingCycle;

    /** Null means unlimited users. */
    @Column(name = "max_users")
    private Integer maxUsers;

    /** JSON or comma-separated feature list. */
    @Column(columnDefinition = "text")
    private String features;

    @Column(name = "trial_days", nullable = false)
    private int trialDays = 0;

    @Column(nullable = false)
    private boolean active = true;
}

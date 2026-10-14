package com.sassmodel.entity;

import com.sassmodel.entity.base.TenantEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "employees", indexes = @Index(name = "idx_employees_organization", columnList = "organization_id"))
public class Employee extends TenantEntity {

    /** Optional login account; not every employee needs platform access. */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(length = 255)
    private String email;

    @Column(length = 30)
    private String phone;

    @Column(length = 100)
    private String position;

    @Column(name = "joined_at")
    private LocalDate joinedAt;

    @Column(nullable = false)
    private boolean active = true;
}

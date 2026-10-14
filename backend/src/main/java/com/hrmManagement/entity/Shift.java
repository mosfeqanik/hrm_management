package com.hrmManagement.entity;

import com.hrmManagement.entity.base.Tenant;
import com.hrmManagement.entity.enums.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "shifts", indexes = @Index(name = "idx_shifts_organization", columnList = "organization_id"))
public class Shift extends Tenant {

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    /** May be earlier than startTime for shifts that end after midnight. */
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "active", nullable = false)
    private boolean active = true;

}

package com.sassmodel.entity;

import com.sassmodel.entity.base.BaseEntity;
import com.sassmodel.entity.enums.RoleScope;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "roles", uniqueConstraints = @UniqueConstraint(columnNames = {"organization_id", "name"}))
public class Role extends BaseEntity {

    /** Null for built-in platform roles; set for organization custom roles. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 255)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RoleScope scope;

    @Column(name = "is_system", nullable = false)
    private boolean system = false;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "role_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<Permission> permissions = new HashSet<>();
}

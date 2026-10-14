package com.sassmodel.security;

import com.sassmodel.entity.Permission;
import com.sassmodel.entity.Role;
import com.sassmodel.entity.User;
import com.sassmodel.entity.enums.UserType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Spring Security principal wrapping a {@link User}. Authorities are derived from the
 * user's {@link Role}s ({@code ROLE_<name>}) and each role's {@link Permission}s
 * ({@code <MODULE>_<ACTION>}), matching the CLAUDE.md permission model
 * ({@code Role -> Permission(module, action)}).
 *
 * <p>Must be built from a {@link User} whose {@code roles} and {@code roles.permissions}
 * collections are already initialized (e.g. via
 * {@code UserRepository#findByEmailFetchingRolesAndPermissions}) — both are lazy
 * {@code @ManyToMany} collections and this class does not trigger their loading.
 */
public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String email;
    private final String passwordHash;
    private final String fullName;
    private final UserType userType;
    private final Long organizationId;
    private final boolean active;
    private final Set<GrantedAuthority> authorities;

    private CustomUserDetails(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.passwordHash = user.getPasswordHash();
        this.fullName = user.getFullName();
        this.userType = user.getUserType();
        // getId() on a lazy @ManyToOne proxy never hits the database, so this is safe
        // even once the loading transaction has closed.
        this.organizationId = user.getOrganization() != null ? user.getOrganization().getId() : null;
        this.active = user.isActive();
        this.authorities = buildAuthorities(user.getRoles());
    }

    public static CustomUserDetails fromUser(User user) {
        return new CustomUserDetails(user);
    }

    private static Set<GrantedAuthority> buildAuthorities(Set<Role> roles) {
        Stream<String> roleAuthorities = roles.stream()
                .map(role -> "ROLE_" + role.getName().toUpperCase().replace(' ', '_'));

        Stream<String> permissionAuthorities = roles.stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(permission -> permission.getModule().toUpperCase() + "_" + permission.getAction().name());

        return Stream.concat(roleAuthorities, permissionAuthorities)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toUnmodifiableSet());
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public UserType getUserType() {
        return userType;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}

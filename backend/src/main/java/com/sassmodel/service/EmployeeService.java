package com.sassmodel.service;

import com.sassmodel.dto.EmployeeRequest;
import com.sassmodel.dto.EmployeeResponse;
import com.sassmodel.entity.Employee;
import com.sassmodel.entity.Organization;
import com.sassmodel.entity.User;
import com.sassmodel.exception.ResourceNotFoundException;
import com.sassmodel.mapper.EmployeeMapper;
import com.sassmodel.repository.EmployeeRepository;
import com.sassmodel.repository.OrganizationRepository;
import com.sassmodel.repository.UserRepository;
import com.sassmodel.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;

    public List<EmployeeResponse> list(Long requestedOrganizationId) {
        Long organizationId = TenantContext.resolveOrganizationId(requestedOrganizationId);
        return employeeRepository.findByOrganizationId(organizationId).stream()
                .map(EmployeeMapper::toResponse)
                .toList();
    }

    public EmployeeResponse getById(Long id) {
        return EmployeeMapper.toResponse(findAccessibleEntity(id));
    }

    @Transactional
    public EmployeeResponse create(EmployeeRequest request) {
        Long organizationId = TenantContext.resolveOrganizationId(request.organizationId());
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found: " + organizationId));

        Employee employee = new Employee();
        employee.setOrganization(organization);
        applyRequest(employee, request);
        return EmployeeMapper.toResponse(employeeRepository.save(employee));
    }

    @Transactional
    public EmployeeResponse update(Long id, EmployeeRequest request) {
        Employee employee = findAccessibleEntity(id);
        applyRequest(employee, request);
        return EmployeeMapper.toResponse(employee);
    }

    @Transactional
    public void delete(Long id) {
        Employee employee = findAccessibleEntity(id);
        employeeRepository.delete(employee);
    }

    private void applyRequest(Employee employee, EmployeeRequest request) {
        employee.setUser(resolveUser(request.userId()));
        employee.setFullName(request.fullName());
        employee.setEmail(request.email());
        employee.setPhone(request.phone());
        employee.setPosition(request.position());
        employee.setJoinedAt(request.joinedAt());
        employee.setActive(request.active() == null || request.active());
    }

    private User resolveUser(Long userId) {
        if (userId == null) {
            return null;
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
    }

    private Employee findAccessibleEntity(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + id));
        if (!TenantContext.canAccess(employee.getOrganization().getId())) {
            throw new ResourceNotFoundException("Employee not found: " + id);
        }
        return employee;
    }
}

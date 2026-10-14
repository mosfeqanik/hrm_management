package com.hrmManagement.service;

import com.hrmManagement.dto.OrganizationSettingRequest;
import com.hrmManagement.dto.OrganizationSettingResponse;
import com.hrmManagement.entity.Organization;
import com.hrmManagement.entity.OrganizationSetting;
import com.hrmManagement.exception.DuplicateResourceException;
import com.hrmManagement.exception.ResourceNotFoundException;
import com.hrmManagement.mapper.OrganizationSettingMapper;
import com.hrmManagement.repository.OrganizationRepository;
import com.hrmManagement.repository.OrganizationSettingRepository;
import com.hrmManagement.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrganizationSettingService {

    private final OrganizationSettingRepository organizationSettingRepository;
    private final OrganizationRepository organizationRepository;

    public List<OrganizationSettingResponse> list(Long requestedOrganizationId) {
        Long organizationId = TenantContext.resolveOrganizationId(requestedOrganizationId);
        return organizationSettingRepository.findByOrganizationId(organizationId).stream()
                .map(OrganizationSettingMapper::toResponse)
                .toList();
    }

    public OrganizationSettingResponse getById(Long id) {
        return OrganizationSettingMapper.toResponse(findAccessibleEntity(id));
    }

    public OrganizationSettingResponse getByKey(String key, Long requestedOrganizationId) {
        Long organizationId = TenantContext.resolveOrganizationId(requestedOrganizationId);
        OrganizationSetting setting = organizationSettingRepository
                .findByOrganizationIdAndKeyIgnoreCase(organizationId, key)
                .orElseThrow(() -> new ResourceNotFoundException("Organization setting not found: " + key));
        return OrganizationSettingMapper.toResponse(setting);
    }

    @Transactional
    public OrganizationSettingResponse create(OrganizationSettingRequest request) {
        Long organizationId = TenantContext.resolveOrganizationId(request.organizationId());

        if (organizationSettingRepository.existsByOrganizationIdAndKeyIgnoreCase(organizationId, request.key())) {
            throw new DuplicateResourceException("A setting with key '" + request.key() + "' already exists for this organization");
        }

        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found: " + organizationId));

        OrganizationSetting setting = new OrganizationSetting();
        setting.setOrganization(organization);
        setting.setKey(request.key());
        setting.setValue(request.value());
        return OrganizationSettingMapper.toResponse(organizationSettingRepository.save(setting));
    }

    @Transactional
    public OrganizationSettingResponse update(Long id, OrganizationSettingRequest request) {
        OrganizationSetting setting = findAccessibleEntity(id);

        if (organizationSettingRepository.existsByOrganizationIdAndKeyIgnoreCaseAndIdNot(
                setting.getOrganization().getId(), request.key(), id)) {
            throw new DuplicateResourceException("A setting with key '" + request.key() + "' already exists for this organization");
        }

        setting.setKey(request.key());
        setting.setValue(request.value());
        return OrganizationSettingMapper.toResponse(setting);
    }

    @Transactional
    public void delete(Long id) {
        OrganizationSetting setting = findAccessibleEntity(id);
        organizationSettingRepository.delete(setting);
    }

    private OrganizationSetting findAccessibleEntity(Long id) {
        OrganizationSetting setting = organizationSettingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organization setting not found: " + id));
        if (!TenantContext.canAccess(setting.getOrganization().getId())) {
            throw new ResourceNotFoundException("Organization setting not found: " + id);
        }
        return setting;
    }
}

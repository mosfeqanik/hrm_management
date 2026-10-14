package com.hrmManagement.service;

import com.hrmManagement.dto.MenuImageRequest;
import com.hrmManagement.dto.MenuImageResponse;
import com.hrmManagement.entity.MenuImage;
import com.hrmManagement.entity.Organization;
import com.hrmManagement.exception.ResourceNotFoundException;
import com.hrmManagement.mapper.MenuImageMapper;
import com.hrmManagement.repository.MenuImageRepository;
import com.hrmManagement.repository.OrganizationRepository;
import com.hrmManagement.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuImageService {

    private final MenuImageRepository menuImageRepository;
    private final OrganizationRepository organizationRepository;

    public List<MenuImageResponse> list(Long requestedOrganizationId) {
        Long organizationId = TenantContext.resolveOrganizationId(requestedOrganizationId);
        return menuImageRepository.findByOrganizationIdOrderBySortOrderAsc(organizationId).stream()
                .map(MenuImageMapper::toResponse)
                .toList();
    }

    public MenuImageResponse getById(Long id) {
        return MenuImageMapper.toResponse(findAccessibleEntity(id));
    }

    @Transactional
    public MenuImageResponse create(MenuImageRequest request) {
        Long organizationId = TenantContext.resolveOrganizationId(request.organizationId());
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found: " + organizationId));

        MenuImage menuImage = new MenuImage();
        menuImage.setOrganization(organization);
        applyRequest(menuImage, request);
        return MenuImageMapper.toResponse(menuImageRepository.save(menuImage));
    }

    @Transactional
    public MenuImageResponse update(Long id, MenuImageRequest request) {
        MenuImage menuImage = findAccessibleEntity(id);
        applyRequest(menuImage, request);
        return MenuImageMapper.toResponse(menuImage);
    }

    @Transactional
    public void delete(Long id) {
        MenuImage menuImage = findAccessibleEntity(id);
        menuImageRepository.delete(menuImage);
    }

    private void applyRequest(MenuImage menuImage, MenuImageRequest request) {
        menuImage.setImageUrl(request.imageUrl());
        menuImage.setCaption(request.caption());
        menuImage.setSortOrder(request.sortOrder() != null ? request.sortOrder() : 0);
    }

    private MenuImage findAccessibleEntity(Long id) {
        MenuImage menuImage = menuImageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu image not found: " + id));
        if (!TenantContext.canAccess(menuImage.getOrganization().getId())) {
            throw new ResourceNotFoundException("Menu image not found: " + id);
        }
        return menuImage;
    }
}

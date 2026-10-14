package com.sassmodel.service;

import com.sassmodel.dto.PaymentMethodRequest;
import com.sassmodel.dto.PaymentMethodResponse;
import com.sassmodel.entity.Organization;
import com.sassmodel.entity.PaymentMethod;
import com.sassmodel.exception.ResourceNotFoundException;
import com.sassmodel.mapper.PaymentMethodMapper;
import com.sassmodel.repository.OrganizationRepository;
import com.sassmodel.repository.PaymentMethodRepository;
import com.sassmodel.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;
    private final OrganizationRepository organizationRepository;

    public List<PaymentMethodResponse> list(Long requestedOrganizationId) {
        Long organizationId = TenantContext.resolveOrganizationId(requestedOrganizationId);
        return paymentMethodRepository.findByOrganizationId(organizationId).stream()
                .map(PaymentMethodMapper::toResponse)
                .toList();
    }

    public PaymentMethodResponse getById(Long id) {
        return PaymentMethodMapper.toResponse(findAccessibleEntity(id));
    }

    @Transactional
    public PaymentMethodResponse create(PaymentMethodRequest request) {
        Long organizationId = TenantContext.resolveOrganizationId(request.organizationId());
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found: " + organizationId));

        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setOrganization(organization);
        applyRequest(paymentMethod, request);
        return PaymentMethodMapper.toResponse(paymentMethodRepository.save(paymentMethod));
    }

    @Transactional
    public PaymentMethodResponse update(Long id, PaymentMethodRequest request) {
        PaymentMethod paymentMethod = findAccessibleEntity(id);
        applyRequest(paymentMethod, request);
        return PaymentMethodMapper.toResponse(paymentMethod);
    }

    @Transactional
    public void delete(Long id) {
        PaymentMethod paymentMethod = findAccessibleEntity(id);
        paymentMethodRepository.delete(paymentMethod);
    }

    private void applyRequest(PaymentMethod paymentMethod, PaymentMethodRequest request) {
        paymentMethod.setType(request.type());
        paymentMethod.setMaskedDetails(request.maskedDetails());
        paymentMethod.setGatewayToken(request.gatewayToken());

        boolean makeDefault = Boolean.TRUE.equals(request.defaultMethod());
        if (makeDefault) {
            clearExistingDefault(paymentMethod.getOrganization().getId(), paymentMethod.getId());
        }
        paymentMethod.setDefaultMethod(makeDefault);
    }

    /** Only one payment method per organization may be the default. */
    private void clearExistingDefault(Long organizationId, Long exceptId) {
        paymentMethodRepository.findByOrganizationIdAndDefaultMethodTrue(organizationId).stream()
                .filter(existing -> !existing.getId().equals(exceptId))
                .forEach(existing -> existing.setDefaultMethod(false));
    }

    private PaymentMethod findAccessibleEntity(Long id) {
        PaymentMethod paymentMethod = paymentMethodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment method not found: " + id));
        if (!TenantContext.canAccess(paymentMethod.getOrganization().getId())) {
            throw new ResourceNotFoundException("Payment method not found: " + id);
        }
        return paymentMethod;
    }
}

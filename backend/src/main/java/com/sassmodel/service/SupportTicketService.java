package com.sassmodel.service;

import com.sassmodel.dto.SupportTicketAssignRequest;
import com.sassmodel.dto.SupportTicketRequest;
import com.sassmodel.dto.SupportTicketResponse;
import com.sassmodel.dto.SupportTicketStatusChangeRequest;
import com.sassmodel.entity.Organization;
import com.sassmodel.entity.SupportTicket;
import com.sassmodel.entity.User;
import com.sassmodel.entity.enums.TicketStatus;
import com.sassmodel.exception.ResourceNotFoundException;
import com.sassmodel.mapper.SupportTicketMapper;
import com.sassmodel.repository.OrganizationRepository;
import com.sassmodel.repository.SupportTicketRepository;
import com.sassmodel.repository.UserRepository;
import com.sassmodel.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SupportTicketService {

    private final SupportTicketRepository supportTicketRepository;
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;

    public List<SupportTicketResponse> list(Long requestedOrganizationId) {
        Long organizationId = TenantContext.resolveOrganizationId(requestedOrganizationId);
        return supportTicketRepository.findByOrganizationId(organizationId).stream()
                .map(SupportTicketMapper::toResponse)
                .toList();
    }

    public SupportTicketResponse getById(Long id) {
        return SupportTicketMapper.toResponse(findAccessibleEntity(id));
    }

    @Transactional
    public SupportTicketResponse create(SupportTicketRequest request) {
        Long organizationId = TenantContext.resolveOrganizationId(request.organizationId());
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found: " + organizationId));
        User createdBy = userRepository.findById(TenantContext.currentPrincipal().userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + TenantContext.currentPrincipal().userId()));

        SupportTicket ticket = new SupportTicket();
        ticket.setOrganization(organization);
        ticket.setCreatedBy(createdBy);
        ticket.setTicketNumber("TCK-" + System.currentTimeMillis());
        ticket.setSubject(request.subject());
        ticket.setDescription(request.description());
        ticket.setType(request.type());
        ticket.setPriority(request.priority());
        ticket.setStatus(TicketStatus.OPEN);

        return SupportTicketMapper.toResponse(supportTicketRepository.save(ticket));
    }

    @Transactional
    public SupportTicketResponse update(Long id, SupportTicketRequest request) {
        SupportTicket ticket = findAccessibleEntity(id);
        ticket.setSubject(request.subject());
        ticket.setDescription(request.description());
        ticket.setType(request.type());
        ticket.setPriority(request.priority());
        return SupportTicketMapper.toResponse(ticket);
    }

    @Transactional
    public SupportTicketResponse changeStatus(Long id, SupportTicketStatusChangeRequest request) {
        SupportTicket ticket = findAccessibleEntity(id);
        ticket.setStatus(request.status());
        LocalDateTime now = LocalDateTime.now();
        if (request.status() == TicketStatus.RESOLVED) {
            ticket.setResolvedAt(now);
        } else if (request.status() == TicketStatus.CLOSED) {
            ticket.setClosedAt(now);
        }
        return SupportTicketMapper.toResponse(ticket);
    }

    @Transactional
    public SupportTicketResponse assign(Long id, SupportTicketAssignRequest request) {
        TenantContext.requirePlatformStaff();
        SupportTicket ticket = findAccessibleEntity(id);
        User assignee = userRepository.findById(request.assignedToUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + request.assignedToUserId()));
        ticket.setAssignedTo(assignee);
        return SupportTicketMapper.toResponse(ticket);
    }

    @Transactional
    public void delete(Long id) {
        TenantContext.requirePlatformStaff();
        SupportTicket ticket = findAccessibleEntity(id);
        supportTicketRepository.delete(ticket);
    }

    private SupportTicket findAccessibleEntity(Long id) {
        SupportTicket ticket = supportTicketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Support ticket not found: " + id));
        if (!TenantContext.canAccess(ticket.getOrganization().getId())) {
            throw new ResourceNotFoundException("Support ticket not found: " + id);
        }
        return ticket;
    }
}

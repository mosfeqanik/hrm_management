package com.sassmodel.service;

import com.sassmodel.dto.TicketCommentRequest;
import com.sassmodel.dto.TicketCommentResponse;
import com.sassmodel.entity.SupportTicket;
import com.sassmodel.entity.TicketComment;
import com.sassmodel.entity.User;
import com.sassmodel.exception.ResourceNotFoundException;
import com.sassmodel.mapper.TicketCommentMapper;
import com.sassmodel.repository.SupportTicketRepository;
import com.sassmodel.repository.TicketCommentRepository;
import com.sassmodel.repository.UserRepository;
import com.sassmodel.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** {@code internal} comments are staff-only and filtered out of the list for an org user. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TicketCommentService {

    private final TicketCommentRepository ticketCommentRepository;
    private final SupportTicketRepository supportTicketRepository;
    private final UserRepository userRepository;

    public List<TicketCommentResponse> list(Long ticketId) {
        SupportTicket ticket = findAccessibleTicket(ticketId);
        boolean canSeeInternal = TenantContext.isPlatformStaff();
        return ticketCommentRepository.findByTicketIdOrderByIdAsc(ticket.getId()).stream()
                .filter(comment -> canSeeInternal || !comment.isInternal())
                .map(TicketCommentMapper::toResponse)
                .toList();
    }

    @Transactional
    public TicketCommentResponse create(Long ticketId, TicketCommentRequest request) {
        SupportTicket ticket = findAccessibleTicket(ticketId);
        User author = userRepository.findById(TenantContext.currentPrincipal().userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + TenantContext.currentPrincipal().userId()));

        boolean internal = Boolean.TRUE.equals(request.internal()) && TenantContext.isPlatformStaff();

        TicketComment comment = new TicketComment();
        comment.setTicket(ticket);
        comment.setAuthor(author);
        comment.setBody(request.body());
        comment.setInternal(internal);

        return TicketCommentMapper.toResponse(ticketCommentRepository.save(comment));
    }

    private SupportTicket findAccessibleTicket(Long ticketId) {
        SupportTicket ticket = supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Support ticket not found: " + ticketId));
        if (!TenantContext.canAccess(ticket.getOrganization().getId())) {
            throw new ResourceNotFoundException("Support ticket not found: " + ticketId);
        }
        return ticket;
    }
}

package com.sassmodel.controller;

import com.sassmodel.dto.SupportTicketAssignRequest;
import com.sassmodel.dto.SupportTicketRequest;
import com.sassmodel.dto.SupportTicketResponse;
import com.sassmodel.dto.SupportTicketStatusChangeRequest;
import com.sassmodel.dto.TicketCommentRequest;
import com.sassmodel.dto.TicketCommentResponse;
import com.sassmodel.service.SupportTicketService;
import com.sassmodel.service.TicketCommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/support-tickets")
@RequiredArgsConstructor
public class SupportTicketController {

    private final SupportTicketService supportTicketService;
    private final TicketCommentService ticketCommentService;

    @GetMapping
    public ResponseEntity<List<SupportTicketResponse>> list(
            @RequestParam(name = "organizationId", required = false) Long organizationId) {
        return ResponseEntity.ok(supportTicketService.list(organizationId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupportTicketResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(supportTicketService.getById(id));
    }

    @PostMapping
    public ResponseEntity<SupportTicketResponse> create(@Valid @RequestBody SupportTicketRequest request) {
        return ResponseEntity.status(201).body(supportTicketService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SupportTicketResponse> update(@PathVariable Long id, @Valid @RequestBody SupportTicketRequest request) {
        return ResponseEntity.ok(supportTicketService.update(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<SupportTicketResponse> changeStatus(
            @PathVariable Long id, @Valid @RequestBody SupportTicketStatusChangeRequest request) {
        return ResponseEntity.ok(supportTicketService.changeStatus(id, request));
    }

    @PatchMapping("/{id}/assign")
    public ResponseEntity<SupportTicketResponse> assign(
            @PathVariable Long id, @Valid @RequestBody SupportTicketAssignRequest request) {
        return ResponseEntity.ok(supportTicketService.assign(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        supportTicketService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<TicketCommentResponse>> listComments(@PathVariable Long id) {
        return ResponseEntity.ok(ticketCommentService.list(id));
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<TicketCommentResponse> addComment(
            @PathVariable Long id, @Valid @RequestBody TicketCommentRequest request) {
        return ResponseEntity.status(201).body(ticketCommentService.create(id, request));
    }
}

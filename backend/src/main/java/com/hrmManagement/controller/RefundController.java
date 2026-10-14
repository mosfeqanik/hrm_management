package com.hrmManagement.controller;

import com.hrmManagement.dto.RefundRequest;
import com.hrmManagement.dto.RefundResponse;
import com.hrmManagement.dto.RefundStatusChangeRequest;
import com.hrmManagement.service.RefundService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/refunds")
@RequiredArgsConstructor
public class RefundController {

    private final RefundService refundService;

    @GetMapping
    public ResponseEntity<List<RefundResponse>> list(
            @RequestParam(name = "organizationId", required = false) Long organizationId) {
        return ResponseEntity.ok(refundService.list(organizationId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RefundResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(refundService.getById(id));
    }

    @PostMapping
    public ResponseEntity<RefundResponse> create(@Valid @RequestBody RefundRequest request) {
        return ResponseEntity.status(201).body(refundService.create(request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<RefundResponse> changeStatus(
            @PathVariable Long id, @Valid @RequestBody RefundStatusChangeRequest request) {
        return ResponseEntity.ok(refundService.changeStatus(id, request));
    }
}

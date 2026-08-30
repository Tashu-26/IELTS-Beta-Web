package com.ieltsbeta.controller;

import com.ieltsbeta.dto.SupportTicketCreateRequest;
import com.ieltsbeta.dto.SupportTicketResponse;
import com.ieltsbeta.dto.SupportTicketUpdateRequest;
import com.ieltsbeta.service.SupportTicketService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/support-tickets")
public class SupportTicketController {

    private final SupportTicketService supportTicketService;

    public SupportTicketController(SupportTicketService supportTicketService) {
        this.supportTicketService = supportTicketService;
    }

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public SupportTicketResponse create(@AuthenticationPrincipal Jwt jwt,
                                         @Valid @RequestBody SupportTicketCreateRequest request) {
        return supportTicketService.create(jwt, request);
    }

    @GetMapping("/mine")
    @PreAuthorize("hasRole('STUDENT')")
    public List<SupportTicketResponse> mine(@AuthenticationPrincipal Jwt jwt) {
        return supportTicketService.myTickets(jwt);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<SupportTicketResponse> listAll(@AuthenticationPrincipal Jwt jwt) {
        return supportTicketService.listAll(jwt);
    }

    @PutMapping("/{ticketId}")
    @PreAuthorize("hasRole('ADMIN')")
    public SupportTicketResponse update(@AuthenticationPrincipal Jwt jwt,
                                         @PathVariable Long ticketId,
                                         @Valid @RequestBody SupportTicketUpdateRequest request) {
        return supportTicketService.updateStatus(jwt, ticketId, request);
    }
}

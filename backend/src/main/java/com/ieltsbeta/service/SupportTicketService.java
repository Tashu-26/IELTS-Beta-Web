package com.ieltsbeta.service;

import com.ieltsbeta.dto.SupportTicketCreateRequest;
import com.ieltsbeta.dto.SupportTicketResponse;
import com.ieltsbeta.dto.SupportTicketUpdateRequest;
import com.ieltsbeta.entity.Admin;
import com.ieltsbeta.entity.Student;
import com.ieltsbeta.entity.SupportTicket;
import com.ieltsbeta.repository.SupportTicketRepository;
import com.ieltsbeta.security.CurrentUserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@Service
public class SupportTicketService {

    private static final Set<String> VALID_STATUSES = Set.of("Open", "In Progress", "Resolved");

    private final SupportTicketRepository supportTicketRepository;
    private final CurrentUserService currentUserService;

    public SupportTicketService(SupportTicketRepository supportTicketRepository,
                                 CurrentUserService currentUserService) {
        this.supportTicketRepository = supportTicketRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public SupportTicketResponse create(Jwt jwt, SupportTicketCreateRequest request) {
        Student student = currentUserService.requireStudent(jwt);

        SupportTicket ticket = new SupportTicket();
        ticket.setStudent(student);
        ticket.setSubject(request.subject());
        ticket.setMessage(request.message());
        ticket.setStatus("Open");
        ticket = supportTicketRepository.save(ticket);

        return toResponse(ticket);
    }

    @Transactional(readOnly = true)
    public List<SupportTicketResponse> myTickets(Jwt jwt) {
        Student student = currentUserService.requireStudent(jwt);
        return supportTicketRepository.findByStudent_StudentId(student.getStudentId())
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<SupportTicketResponse> listAll(Jwt jwt) {
        currentUserService.requireAdmin(jwt);
        return supportTicketRepository.findAllByOrderByCreatedAtDesc().stream().map(this::toResponse).toList();
    }

    /** Admin updates status and is auto-assigned to the ticket if not already assigned. */
    @Transactional
    public SupportTicketResponse updateStatus(Jwt jwt, Long ticketId, SupportTicketUpdateRequest request) {
        Admin admin = currentUserService.requireAdmin(jwt);

        if (!VALID_STATUSES.contains(request.status())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status.");
        }

        SupportTicket ticket = supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found."));

        ticket.setStatus(request.status());
        if (ticket.getAdmin() == null) {
            ticket.setAdmin(admin);
        }
        ticket = supportTicketRepository.save(ticket);

        return toResponse(ticket);
    }

    private SupportTicketResponse toResponse(SupportTicket ticket) {
        return new SupportTicketResponse(
                ticket.getTicketId(),
                ticket.getSubject(),
                ticket.getMessage(),
                ticket.getStatus(),
                ticket.getCreatedAt(),
                ticket.getStudent().getStudentId(),
                ticket.getStudent().getUser().getPerson().getFirstName() + " "
                        + ticket.getStudent().getUser().getPerson().getLastName(),
                ticket.getAdmin() != null ? ticket.getAdmin().getAdminId() : null
        );
    }
}

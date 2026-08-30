package com.ieltsbeta.service;

import com.ieltsbeta.dto.AnnouncementCreateRequest;
import com.ieltsbeta.dto.AnnouncementResponse;
import com.ieltsbeta.entity.Admin;
import com.ieltsbeta.entity.Announcement;
import com.ieltsbeta.repository.AnnouncementRepository;
import com.ieltsbeta.security.CurrentUserService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final CurrentUserService currentUserService;
    private final AdminLogService adminLogService;

    public AnnouncementService(AnnouncementRepository announcementRepository,
                                CurrentUserService currentUserService,
                                AdminLogService adminLogService) {
        this.announcementRepository = announcementRepository;
        this.currentUserService = currentUserService;
        this.adminLogService = adminLogService;
    }

    @Transactional(readOnly = true)
    public List<AnnouncementResponse> listAll() {
        return announcementRepository.findAllByOrderByCreatedAtDesc().stream().map(this::toResponse).toList();
    }

    @Transactional
    public AnnouncementResponse create(Jwt jwt, AnnouncementCreateRequest request) {
        Admin admin = currentUserService.requireAdmin(jwt);

        Announcement announcement = new Announcement();
        announcement.setAdmin(admin);
        announcement.setTitle(request.title());
        announcement.setMessage(request.message());
        announcement = announcementRepository.save(announcement);

        adminLogService.log(admin, "Posted announcement", request.title());

        return toResponse(announcement);
    }

    private AnnouncementResponse toResponse(Announcement announcement) {
        return new AnnouncementResponse(
                announcement.getAnnouncementId(),
                announcement.getTitle(),
                announcement.getMessage(),
                announcement.getCreatedAt(),
                announcement.getAdmin().getUser().getPerson().getFirstName() + " "
                        + announcement.getAdmin().getUser().getPerson().getLastName()
        );
    }
}

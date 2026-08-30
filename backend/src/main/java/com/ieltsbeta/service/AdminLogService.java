package com.ieltsbeta.service;

import com.ieltsbeta.dto.AdminLogResponse;
import com.ieltsbeta.entity.Admin;
import com.ieltsbeta.entity.AdminLog;
import com.ieltsbeta.repository.AdminLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Small helper other admin-facing services call to record an action.
 * Kept intentionally lightweight -- logs a handful of key admin actions
 * (e.g. posting an announcement) rather than instrumenting every write.
 */
@Service
public class AdminLogService {

    private final AdminLogRepository adminLogRepository;

    public AdminLogService(AdminLogRepository adminLogRepository) {
        this.adminLogRepository = adminLogRepository;
    }

    @Transactional
    public void log(Admin admin, String action, String details) {
        AdminLog entry = new AdminLog();
        entry.setAdmin(admin);
        entry.setAction(action);
        entry.setDetails(details);
        adminLogRepository.save(entry);
    }

    @Transactional(readOnly = true)
    public List<AdminLogResponse> listAll() {
        return adminLogRepository.findAllByOrderByLoggedAtDesc().stream().map(this::toResponse).toList();
    }

    private AdminLogResponse toResponse(AdminLog log) {
        return new AdminLogResponse(
                log.getLogId(),
                log.getAction(),
                log.getDetails(),
                log.getLoggedAt(),
                log.getAdmin().getUser().getPerson().getFirstName() + " "
                        + log.getAdmin().getUser().getPerson().getLastName()
        );
    }
}

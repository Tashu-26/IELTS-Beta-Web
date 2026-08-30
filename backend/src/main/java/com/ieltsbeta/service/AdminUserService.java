package com.ieltsbeta.service;

import com.ieltsbeta.dto.AdminReportResponse;
import com.ieltsbeta.dto.AdminUserResponse;
import com.ieltsbeta.dto.UserRoleUpdateRequest;
import com.ieltsbeta.dto.UserStatusUpdateRequest;
import com.ieltsbeta.entity.Admin;
import com.ieltsbeta.entity.AppUser;
import com.ieltsbeta.entity.Student;
import com.ieltsbeta.entity.Teacher;
import com.ieltsbeta.repository.*;
import com.ieltsbeta.security.CurrentUserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@Service
public class AdminUserService {

    private static final Set<String> VALID_ROLES = Set.of("Student", "Teacher", "Admin");

    private final AppUserRepository appUserRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final AdminRepository adminRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final SupportTicketRepository supportTicketRepository;
    private final CurrentUserService currentUserService;
    private final AdminLogService adminLogService;

    public AdminUserService(AppUserRepository appUserRepository,
                             StudentRepository studentRepository,
                             TeacherRepository teacherRepository,
                             AdminRepository adminRepository,
                             CourseRepository courseRepository,
                             EnrollmentRepository enrollmentRepository,
                             SupportTicketRepository supportTicketRepository,
                             CurrentUserService currentUserService,
                             AdminLogService adminLogService) {
        this.appUserRepository = appUserRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.adminRepository = adminRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.supportTicketRepository = supportTicketRepository;
        this.currentUserService = currentUserService;
        this.adminLogService = adminLogService;
    }

    @Transactional(readOnly = true)
    public List<AdminUserResponse> listUsers() {
        return appUserRepository.findAll().stream().map(this::toResponse).toList();
    }

    /**
     * Changes a user's role. Idempotent role-specific-row creation: if the
     * new role's table (students/teachers/admins) doesn't have a row for
     * this user yet, one is created. Existing rows for a *previous* role are
     * left in place rather than deleted, to avoid silently destroying data
     * (e.g. a demoted Teacher's courses stay intact and re-promotable).
     */
    @Transactional
    public AdminUserResponse updateRole(Jwt jwt, Long userId, UserRoleUpdateRequest request) {
        Admin actingAdmin = currentUserService.requireAdmin(jwt);

        if (!VALID_ROLES.contains(request.role())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role must be Student, Teacher or Admin.");
        }

        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));

        String previousRole = user.getRole();
        user.setRole(request.role());
        user = appUserRepository.save(user);

        switch (request.role()) {
            case "Student" -> {
                if (studentRepository.findByUser_UserId(user.getUserId()).isEmpty()) {
                    Student student = new Student();
                    student.setUser(user);
                    student.setDaysActive(0);
                    studentRepository.save(student);
                }
            }
            case "Teacher" -> {
                if (teacherRepository.findByUser_UserId(user.getUserId()).isEmpty()) {
                    Teacher teacher = new Teacher();
                    teacher.setUser(user);
                    teacherRepository.save(teacher);
                }
            }
            case "Admin" -> {
                if (adminRepository.findByUser_UserId(user.getUserId()).isEmpty()) {
                    Admin admin = new Admin();
                    admin.setUser(user);
                    adminRepository.save(admin);
                }
            }
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown role.");
        }

        adminLogService.log(actingAdmin, "Changed user role",
                user.getEmail() + ": " + previousRole + " -> " + request.role());

        return toResponse(user);
    }

    /**
     * Activates or suspends a user. A suspended user's Supabase JWT still
     * authenticates (they're still a real Supabase Auth account), but
     * SupabaseJwtAuthenticationConverter grants them no role authorities,
     * so every @PreAuthorize-protected endpoint rejects them with 403.
     * GET /api/me still works (it only requires authentication, no role) --
     * a suspended user can see their own status but can't act as their role
     * anywhere. Admins cannot suspend themselves (would lock them out).
     */
    @Transactional
    public AdminUserResponse updateStatus(Jwt jwt, Long userId, UserStatusUpdateRequest request) {
        Admin actingAdmin = currentUserService.requireAdmin(jwt);
        AppUser callingUser = currentUserService.requireUser(jwt);

        if (callingUser.getUserId().equals(userId) && !request.isActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot suspend your own account.");
        }

        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));

        user.setIsActive(request.isActive());
        user = appUserRepository.save(user);

        adminLogService.log(actingAdmin, request.isActive() ? "Reactivated user" : "Suspended user",
                user.getEmail());

        return toResponse(user);
    }

    @Transactional(readOnly = true)
    public AdminReportResponse getReport() {
        return new AdminReportResponse(
                appUserRepository.count(),
                studentRepository.count(),
                teacherRepository.count(),
                courseRepository.count(),
                enrollmentRepository.count(),
                supportTicketRepository.countByStatus("Open")
        );
    }

    private AdminUserResponse toResponse(AppUser user) {
        return new AdminUserResponse(
                user.getUserId(),
                user.getPerson().getFirstName(),
                user.getPerson().getLastName(),
                user.getEmail(),
                user.getRole(),
                Boolean.TRUE.equals(user.getIsActive()),
                user.getCreatedAt()
        );
    }
}

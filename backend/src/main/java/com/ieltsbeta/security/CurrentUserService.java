package com.ieltsbeta.security;

import com.ieltsbeta.entity.AppUser;
import com.ieltsbeta.entity.Student;
import com.ieltsbeta.entity.Teacher;
import com.ieltsbeta.entity.Admin;
import com.ieltsbeta.repository.AdminRepository;
import com.ieltsbeta.repository.AppUserRepository;
import com.ieltsbeta.repository.StudentRepository;
import com.ieltsbeta.repository.TeacherRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

/**
 * Resolves the authenticated principal (from the verified Supabase JWT) down
 * to our own database rows. Used by every service that needs to know
 * "who is calling this, and are they allowed to do this" -- keeps that
 * lookup in one place instead of duplicated across services.
 */
@Component
public class CurrentUserService {

    private final AppUserRepository appUserRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final AdminRepository adminRepository;

    public CurrentUserService(AppUserRepository appUserRepository,
                               StudentRepository studentRepository,
                               TeacherRepository teacherRepository,
                               AdminRepository adminRepository) {
        this.appUserRepository = appUserRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.adminRepository = adminRepository;
    }

    public AppUser requireUser(Jwt jwt) {
        UUID authUserId = UUID.fromString(jwt.getSubject());
        AppUser user = appUserRepository.findByAuthUserId(authUserId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No profile yet for this account. Call POST /api/auth/complete-profile first."));
        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Your account has been suspended.");
        }
        return user;
    }

    public Student requireStudent(Jwt jwt) {
        AppUser user = requireUser(jwt);
        return studentRepository.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Student access required."));
    }

    public Teacher requireTeacher(Jwt jwt) {
        AppUser user = requireUser(jwt);
        return teacherRepository.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Teacher access required."));
    }

    public Admin requireAdmin(Jwt jwt) {
        AppUser user = requireUser(jwt);
        return adminRepository.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin access required."));
    }
}

package com.ieltsbeta.service;

import com.ieltsbeta.dto.CompleteProfileRequest;
import com.ieltsbeta.dto.GoalUpdateRequest;
import com.ieltsbeta.dto.MeResponse;
import com.ieltsbeta.dto.UpdateProfileRequest;
import com.ieltsbeta.entity.AppUser;
import com.ieltsbeta.entity.Person;
import com.ieltsbeta.entity.Student;
import com.ieltsbeta.repository.AdminRepository;
import com.ieltsbeta.repository.AppUserRepository;
import com.ieltsbeta.repository.PersonRepository;
import com.ieltsbeta.repository.StudentRepository;
import com.ieltsbeta.event.BandCheckEvent;
import com.ieltsbeta.event.MissionCompletedEvent;
import com.ieltsbeta.repository.TeacherRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class AuthService {

    private final PersonRepository personRepository;
    private final AppUserRepository appUserRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final AdminRepository adminRepository;
    private final ApplicationEventPublisher eventPublisher;

    public AuthService(PersonRepository personRepository,
                        AppUserRepository appUserRepository,
                        StudentRepository studentRepository,
                        TeacherRepository teacherRepository,
                        AdminRepository adminRepository,
                        ApplicationEventPublisher eventPublisher) {
        this.personRepository = personRepository;
        this.appUserRepository = appUserRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.adminRepository = adminRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Creates person/users/students rows for a freshly-authenticated Supabase
     * user. Idempotent: if a profile already exists for this auth_user_id,
     * it's returned as-is instead of erroring or duplicating.
     *
     * Self-registration is always role "Student" (see Phase 1 decision --
     * Teacher/Admin accounts are provisioned separately).
     */
    @Transactional
    public MeResponse completeProfile(UUID authUserId, String email, CompleteProfileRequest request) {
        AppUser existing = appUserRepository.findByAuthUserId(authUserId).orElse(null);
        if (existing != null) {
            return toMeResponse(existing);
        }

        Person person = new Person();
        person.setFirstName(request.getFirstName());
        person.setLastName(request.getLastName());
        person = personRepository.save(person);

        AppUser user = new AppUser();
        user.setPerson(person);
        user.setAuthUserId(authUserId);
        user.setEmail(email);
        user.setRole("Student");
        user = appUserRepository.save(user);

        Student student = new Student();
        student.setUser(user);
        student.setDaysActive(0);
        studentRepository.save(student);

        return toMeResponse(user);
    }

    @Transactional(readOnly = true)
    public MeResponse getMe(UUID authUserId) {
        AppUser user = appUserRepository.findByAuthUserId(authUserId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No profile yet for this account. Call POST /api/auth/complete-profile first."));
        return toMeResponse(user);
    }

    /**
     * Updates the logged-in account's display name (Person row shared by
     * every role). This is the "profile settings" action -- previously
     * missing, which is why editing a profile did nothing.
     */
    @Transactional
    public MeResponse updateProfile(UUID authUserId, UpdateProfileRequest request) {
        AppUser user = appUserRepository.findByAuthUserId(authUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found."));
        Person person = user.getPerson();
        person.setFirstName(request.firstName());
        person.setLastName(request.lastName());
        personRepository.save(person);
        return toMeResponse(user);
    }

    @Transactional
    public MeResponse updateGoal(UUID authUserId, GoalUpdateRequest request) {
        AppUser user = appUserRepository.findByAuthUserId(authUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found."));
        Student student = studentRepository.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Student access required."));

        if (request.targetBand() != null) {
            student.setTargetBand(request.targetBand());
        }
        if (request.currentBand() != null) {
            student.setCurrentBand(request.currentBand());
        }
        studentRepository.save(student);
        eventPublisher.publishEvent(new MissionCompletedEvent(student.getStudentId(), "set_goal"));
        eventPublisher.publishEvent(new BandCheckEvent(student));

        return toMeResponse(user);
    }

    private MeResponse toMeResponse(AppUser user) {
        MeResponse dto = new MeResponse();
        dto.setUserId(user.getUserId());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setFirstName(user.getPerson().getFirstName());
        dto.setLastName(user.getPerson().getLastName());

        if ("Student".equals(user.getRole())) {
            studentRepository.findByUser_UserId(user.getUserId()).ifPresent(student -> {
                dto.setStudentId(student.getStudentId());
                dto.setTargetBand(student.getTargetBand());
                dto.setCurrentBand(student.getCurrentBand());
            });
        } else if ("Teacher".equals(user.getRole())) {
            teacherRepository.findByUser_UserId(user.getUserId()).ifPresent(teacher -> {
                dto.setTeacherId(teacher.getTeacherId());
                dto.setSpecialization(teacher.getSpecialization());
            });
        } else if ("Admin".equals(user.getRole())) {
            adminRepository.findByUser_UserId(user.getUserId()).ifPresent(admin ->
                    dto.setAdminId(admin.getAdminId()));
        }

        return dto;
    }
}

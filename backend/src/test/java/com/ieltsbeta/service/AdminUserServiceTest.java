package com.ieltsbeta.service;

import com.ieltsbeta.dto.AdminUserResponse;
import com.ieltsbeta.dto.UserRoleUpdateRequest;
import com.ieltsbeta.dto.UserStatusUpdateRequest;
import com.ieltsbeta.entity.Admin;
import com.ieltsbeta.entity.AppUser;
import com.ieltsbeta.entity.Person;
import com.ieltsbeta.repository.AdminLogRepository;
import com.ieltsbeta.repository.AdminRepository;
import com.ieltsbeta.repository.AppUserRepository;
import com.ieltsbeta.repository.CourseRepository;
import com.ieltsbeta.repository.EnrollmentRepository;
import com.ieltsbeta.repository.StudentRepository;
import com.ieltsbeta.repository.SupportTicketRepository;
import com.ieltsbeta.repository.TeacherRepository;
import com.ieltsbeta.security.CurrentUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * AdminUserController/Service is how the "chicken-and-egg" admin bootstrap
 * problem is solved after the FIRST admin exists (see
 * supabase/bootstrap_admin.sql for how that first one is created). These
 * tests cover the promotion logic that every subsequent Teacher/Admin goes
 * through.
 */
@ExtendWith(MockitoExtension.class)
class AdminUserServiceTest {

    @Mock private AppUserRepository appUserRepository;
    @Mock private StudentRepository studentRepository;
    @Mock private TeacherRepository teacherRepository;
    @Mock private AdminRepository adminRepository;
    @Mock private CourseRepository courseRepository;
    @Mock private EnrollmentRepository enrollmentRepository;
    @Mock private SupportTicketRepository supportTicketRepository;
    @Mock private CurrentUserService currentUserService;
    @Mock private AdminLogRepository adminLogRepository;

    private AdminUserService adminUserService;
    private Jwt jwt;
    private Admin actingAdmin;

    @BeforeEach
    void setUp() {
        AdminLogService adminLogService = new AdminLogService(adminLogRepository);
        adminUserService = new AdminUserService(appUserRepository, studentRepository, teacherRepository,
                adminRepository, courseRepository, enrollmentRepository, supportTicketRepository,
                currentUserService, adminLogService);
        jwt = mock(Jwt.class);
        actingAdmin = new Admin();
        actingAdmin.setAdminId(1L);
    }

    private AppUser targetUser(String role) {
        Person person = new Person();
        person.setFirstName("Ada");
        person.setLastName("Lovelace");
        AppUser user = new AppUser();
        user.setUserId(2L);
        user.setEmail("ada@example.com");
        user.setRole(role);
        user.setIsActive(true);
        user.setPerson(person);
        return user;
    }

    @Test
    void updateRole_promotesStudentToTeacher_andCreatesTeacherRow() {
        when(currentUserService.requireAdmin(jwt)).thenReturn(actingAdmin);
        AppUser user = targetUser("Student");
        when(appUserRepository.findById(2L)).thenReturn(Optional.of(user));
        when(appUserRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(teacherRepository.findByUser_UserId(2L)).thenReturn(Optional.empty());

        AdminUserResponse response = adminUserService.updateRole(jwt, 2L, new UserRoleUpdateRequest("Teacher"));

        assertThat(response.role()).isEqualTo("Teacher");
        verify(teacherRepository).save(any());
        verify(studentRepository, never()).save(any());
    }

    @Test
    void updateRole_doesNotDuplicateRoleRow_ifAlreadyPromotedBefore() {
        when(currentUserService.requireAdmin(jwt)).thenReturn(actingAdmin);
        AppUser user = targetUser("Student");
        when(appUserRepository.findById(2L)).thenReturn(Optional.of(user));
        when(appUserRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(teacherRepository.findByUser_UserId(2L)).thenReturn(Optional.of(new com.ieltsbeta.entity.Teacher()));

        adminUserService.updateRole(jwt, 2L, new UserRoleUpdateRequest("Teacher"));

        verify(teacherRepository, never()).save(any());
    }

    @Test
    void updateRole_rejectsInvalidRole() {
        when(currentUserService.requireAdmin(jwt)).thenReturn(actingAdmin);

        assertThatThrownBy(() -> adminUserService.updateRole(jwt, 2L, new UserRoleUpdateRequest("SuperUser")))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Student, Teacher or Admin");
    }

    @Test
    void updateRole_throwsNotFound_whenUserMissing() {
        when(currentUserService.requireAdmin(jwt)).thenReturn(actingAdmin);
        when(appUserRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminUserService.updateRole(jwt, 2L, new UserRoleUpdateRequest("Admin")))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void updateStatus_suspendsAnotherUser() {
        AppUser callingUser = targetUser("Admin");
        callingUser.setUserId(1L);
        when(currentUserService.requireAdmin(jwt)).thenReturn(actingAdmin);
        when(currentUserService.requireUser(jwt)).thenReturn(callingUser);
        AppUser target = targetUser("Student");
        when(appUserRepository.findById(2L)).thenReturn(Optional.of(target));
        when(appUserRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AdminUserResponse response = adminUserService.updateStatus(jwt, 2L, new UserStatusUpdateRequest(false));

        assertThat(response.isActive()).isFalse();
    }

    @Test
    void updateStatus_preventsSelfSuspension() {
        AppUser callingUser = targetUser("Admin");
        callingUser.setUserId(1L);
        when(currentUserService.requireAdmin(jwt)).thenReturn(actingAdmin);
        when(currentUserService.requireUser(jwt)).thenReturn(callingUser);

        assertThatThrownBy(() -> adminUserService.updateStatus(jwt, 1L, new UserStatusUpdateRequest(false)))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("cannot suspend your own account");
    }
}

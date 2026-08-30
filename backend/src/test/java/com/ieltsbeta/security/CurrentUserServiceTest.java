package com.ieltsbeta.security;

import com.ieltsbeta.entity.Admin;
import com.ieltsbeta.entity.AppUser;
import com.ieltsbeta.entity.Student;
import com.ieltsbeta.entity.Teacher;
import com.ieltsbeta.repository.AdminRepository;
import com.ieltsbeta.repository.AppUserRepository;
import com.ieltsbeta.repository.StudentRepository;
import com.ieltsbeta.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * FACADE PATTERN under test: CurrentUserService hides the coordination of
 * four repositories + JWT parsing + suspension checks behind three simple
 * calls (requireStudent/requireTeacher/requireAdmin). These tests drive it
 * through every branch: happy path, wrong role, suspended, and no profile.
 */
@ExtendWith(MockitoExtension.class)
class CurrentUserServiceTest {

    @Mock
    private AppUserRepository appUserRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private TeacherRepository teacherRepository;
    @Mock
    private AdminRepository adminRepository;

    private CurrentUserService currentUserService;
    private UUID authUserId;
    private Jwt jwt;

    @BeforeEach
    void setUp() {
        currentUserService = new CurrentUserService(appUserRepository, studentRepository, teacherRepository, adminRepository);
        authUserId = UUID.randomUUID();
        jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn(authUserId.toString());
    }

    private AppUser activeUser() {
        AppUser user = new AppUser();
        user.setUserId(1L);
        user.setAuthUserId(authUserId);
        user.setIsActive(true);
        return user;
    }

    @Test
    void requireUser_returnsUser_whenActiveProfileExists() {
        AppUser user = activeUser();
        when(appUserRepository.findByAuthUserId(authUserId)).thenReturn(Optional.of(user));

        assertThat(currentUserService.requireUser(jwt)).isEqualTo(user);
    }

    @Test
    void requireUser_throws404_whenNoProfileYet() {
        when(appUserRepository.findByAuthUserId(authUserId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> currentUserService.requireUser(jwt))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("complete-profile");
    }

    @Test
    void requireUser_throws403_whenSuspended() {
        AppUser user = activeUser();
        user.setIsActive(false);
        when(appUserRepository.findByAuthUserId(authUserId)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> currentUserService.requireUser(jwt))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("suspended");
    }

    @Test
    void requireStudent_returnsStudentRow_whenPresent() {
        AppUser user = activeUser();
        Student student = new Student();
        student.setStudentId(10L);
        when(appUserRepository.findByAuthUserId(authUserId)).thenReturn(Optional.of(user));
        when(studentRepository.findByUser_UserId(user.getUserId())).thenReturn(Optional.of(student));

        assertThat(currentUserService.requireStudent(jwt).getStudentId()).isEqualTo(10L);
    }

    @Test
    void requireStudent_throws403_whenCallerIsNotAStudent() {
        AppUser user = activeUser();
        when(appUserRepository.findByAuthUserId(authUserId)).thenReturn(Optional.of(user));
        when(studentRepository.findByUser_UserId(user.getUserId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> currentUserService.requireStudent(jwt))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Student access required");
    }

    @Test
    void requireTeacher_returnsTeacherRow_whenPresent() {
        AppUser user = activeUser();
        Teacher teacher = new Teacher();
        teacher.setTeacherId(20L);
        when(appUserRepository.findByAuthUserId(authUserId)).thenReturn(Optional.of(user));
        when(teacherRepository.findByUser_UserId(user.getUserId())).thenReturn(Optional.of(teacher));

        assertThat(currentUserService.requireTeacher(jwt).getTeacherId()).isEqualTo(20L);
    }

    @Test
    void requireAdmin_returnsAdminRow_whenPresent() {
        AppUser user = activeUser();
        Admin admin = new Admin();
        admin.setAdminId(30L);
        when(appUserRepository.findByAuthUserId(authUserId)).thenReturn(Optional.of(user));
        when(adminRepository.findByUser_UserId(user.getUserId())).thenReturn(Optional.of(admin));

        assertThat(currentUserService.requireAdmin(jwt).getAdminId()).isEqualTo(30L);
    }

    @Test
    void requireAdmin_throws403_whenCallerIsNotAnAdmin() {
        AppUser user = activeUser();
        when(appUserRepository.findByAuthUserId(authUserId)).thenReturn(Optional.of(user));
        when(adminRepository.findByUser_UserId(user.getUserId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> currentUserService.requireAdmin(jwt))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Admin access required");
    }
}

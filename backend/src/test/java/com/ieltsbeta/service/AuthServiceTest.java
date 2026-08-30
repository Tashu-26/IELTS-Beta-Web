package com.ieltsbeta.service;

import com.ieltsbeta.dto.CompleteProfileRequest;
import com.ieltsbeta.dto.GoalUpdateRequest;
import com.ieltsbeta.dto.MeResponse;
import com.ieltsbeta.dto.UpdateProfileRequest;
import com.ieltsbeta.entity.AppUser;
import com.ieltsbeta.entity.Person;
import com.ieltsbeta.entity.Student;
import com.ieltsbeta.event.BandCheckEvent;
import com.ieltsbeta.event.MissionCompletedEvent;
import com.ieltsbeta.repository.AdminRepository;
import com.ieltsbeta.repository.AppUserRepository;
import com.ieltsbeta.repository.PersonRepository;
import com.ieltsbeta.repository.StudentRepository;
import com.ieltsbeta.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private PersonRepository personRepository;
    @Mock private AppUserRepository appUserRepository;
    @Mock private StudentRepository studentRepository;
    @Mock private TeacherRepository teacherRepository;
    @Mock private AdminRepository adminRepository;
    @Mock private ApplicationEventPublisher eventPublisher;

    private AuthService authService;
    private UUID authUserId;

    @BeforeEach
    void setUp() {
        authService = new AuthService(personRepository, appUserRepository, studentRepository,
                teacherRepository, adminRepository, eventPublisher);
        authUserId = UUID.randomUUID();
    }

    private AppUser userWithPerson(String first, String last) {
        Person person = new Person();
        person.setFirstName(first);
        person.setLastName(last);
        AppUser user = new AppUser();
        user.setUserId(1L);
        user.setAuthUserId(authUserId);
        user.setEmail("student@example.com");
        user.setRole("Student");
        user.setPerson(person);
        return user;
    }

    @Test
    void completeProfile_createsNewStudentProfile_whenNoneExists() {
        when(appUserRepository.findByAuthUserId(authUserId)).thenReturn(Optional.empty());
        when(personRepository.save(any(Person.class))).thenAnswer(inv -> inv.getArgument(0));
        when(appUserRepository.save(any(AppUser.class))).thenAnswer(inv -> {
            AppUser u = inv.getArgument(0);
            u.setUserId(5L);
            return u;
        });
        when(studentRepository.save(any(Student.class))).thenAnswer(inv -> inv.getArgument(0));
        when(studentRepository.findByUser_UserId(5L)).thenReturn(Optional.empty());

        CompleteProfileRequest request = new CompleteProfileRequest();
        request.setFirstName("Ada");
        request.setLastName("Lovelace");
        MeResponse response = authService.completeProfile(authUserId, "new@example.com", request);

        assertThat(response.getRole()).isEqualTo("Student");
        assertThat(response.getUserId()).isEqualTo(5L);
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    void completeProfile_isIdempotent_returnsExistingProfile() {
        AppUser existing = userWithPerson("Ada", "Lovelace");
        when(appUserRepository.findByAuthUserId(authUserId)).thenReturn(Optional.of(existing));
        when(studentRepository.findByUser_UserId(existing.getUserId())).thenReturn(Optional.empty());

        MeResponse response = authService.completeProfile(authUserId, "ignored@example.com",
                new CompleteProfileRequest());

        assertThat(response.getEmail()).isEqualTo("student@example.com");
        verify(personRepository, never()).save(any());
        verify(appUserRepository, never()).save(any());
    }

    @Test
    void getMe_throws404_whenProfileMissing() {
        when(appUserRepository.findByAuthUserId(authUserId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.getMe(authUserId))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void getMe_includesStudentFields_whenRoleIsStudent() {
        AppUser user = userWithPerson("Ada", "Lovelace");
        Student student = new Student();
        student.setStudentId(7L);
        student.setTargetBand(new BigDecimal("7.0"));
        when(appUserRepository.findByAuthUserId(authUserId)).thenReturn(Optional.of(user));
        when(studentRepository.findByUser_UserId(user.getUserId())).thenReturn(Optional.of(student));

        MeResponse response = authService.getMe(authUserId);

        assertThat(response.getStudentId()).isEqualTo(7L);
        assertThat(response.getTargetBand()).isEqualByComparingTo("7.0");
    }

    @Test
    void updateGoal_savesBands_andPublishesMissionAndBandEvents() {
        AppUser user = userWithPerson("Ada", "Lovelace");
        Student student = new Student();
        student.setStudentId(7L);
        when(appUserRepository.findByAuthUserId(authUserId)).thenReturn(Optional.of(user));
        when(studentRepository.findByUser_UserId(user.getUserId())).thenReturn(Optional.of(student));

        authService.updateGoal(authUserId, new GoalUpdateRequest(new BigDecimal("7.5"), new BigDecimal("6.0")));

        assertThat(student.getTargetBand()).isEqualByComparingTo("7.5");
        assertThat(student.getCurrentBand()).isEqualByComparingTo("6.0");
        verify(studentRepository).save(student);

        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher, times(2)).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getAllValues())
                .anyMatch(e -> e instanceof MissionCompletedEvent)
                .anyMatch(e -> e instanceof BandCheckEvent);
    }

    @Test
    void updateGoal_throws403_whenCallerIsNotAStudent() {
        AppUser user = userWithPerson("Ada", "Lovelace");
        when(appUserRepository.findByAuthUserId(authUserId)).thenReturn(Optional.of(user));
        when(studentRepository.findByUser_UserId(user.getUserId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.updateGoal(authUserId, new GoalUpdateRequest(null, null)))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Student access required");
    }

    @Test
    void updateProfile_updatesNameOnSharedPersonRow() {
        AppUser user = userWithPerson("Old", "Name");
        when(appUserRepository.findByAuthUserId(authUserId)).thenReturn(Optional.of(user));
        when(studentRepository.findByUser_UserId(user.getUserId())).thenReturn(Optional.empty());

        MeResponse response = authService.updateProfile(authUserId, new UpdateProfileRequest("New", "Name2"));

        assertThat(response.getFirstName()).isEqualTo("New");
        assertThat(response.getLastName()).isEqualTo("Name2");
        verify(personRepository).save(user.getPerson());
    }
}

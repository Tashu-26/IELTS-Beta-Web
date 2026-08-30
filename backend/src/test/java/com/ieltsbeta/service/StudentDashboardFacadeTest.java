package com.ieltsbeta.service;

import com.ieltsbeta.dto.GamificationSummaryResponse;
import com.ieltsbeta.dto.MeResponse;
import com.ieltsbeta.dto.ProgressResponse;
import com.ieltsbeta.dto.StudentDashboardResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentDashboardFacadeTest {

    @Mock
    private AuthService authService;

    @Mock
    private TestAttemptService testAttemptService;

    @Mock
    private GamificationService gamificationService;

    @Mock
    private Jwt jwt;

    private StudentDashboardFacade facade;

    @BeforeEach
    void setUp() {
        facade = new StudentDashboardFacade(
                authService,
                testAttemptService,
                gamificationService
        );
    }

    @Test
    void getDashboard_combinesProfileProgressAndGamification() {

        UUID userId = UUID.randomUUID();

        when(jwt.getSubject()).thenReturn(userId.toString());

        MeResponse profile = new MeResponse();
        profile.setUserId(100L);
        profile.setEmail("student@example.com");
        profile.setRole("STUDENT");

        ProgressResponse progress = new ProgressResponse(
                null,
                null,
                5,
                null,
                null,
                null,
                null,
                java.util.Collections.emptyList()
        );

        GamificationSummaryResponse gamification =
                new GamificationSummaryResponse(
                        250,
                        50,
                        3,
                        3,
                        java.util.Collections.emptyList(),
                        java.util.Collections.emptyList()
                );

        when(authService.getMe(userId))
                .thenReturn(profile);

        when(testAttemptService.getProgress(jwt))
                .thenReturn(progress);

        when(gamificationService.getSummary(jwt))
                .thenReturn(gamification);

        StudentDashboardResponse result =
                facade.getDashboard(jwt);

        assertThat(result).isNotNull();

        assertThat(result.profile())
                .isSameAs(profile);

        assertThat(result.progress())
                .isSameAs(progress);

        assertThat(result.gamification())
                .isSameAs(gamification);

        verify(jwt).getSubject();

        verify(authService)
                .getMe(userId);

        verify(testAttemptService)
                .getProgress(jwt);

        verify(gamificationService)
                .getSummary(jwt);
    }

    @Test
    void getDashboard_callsEachUnderlyingServiceExactlyOnce() {

        UUID userId = UUID.randomUUID();

        when(jwt.getSubject()).thenReturn(userId.toString());

        MeResponse profile = new MeResponse();

        ProgressResponse progress = new ProgressResponse(
                null,
                null,
                0,
                null,
                null,
                null,
                null,
                java.util.Collections.emptyList()
        );

        GamificationSummaryResponse gamification =
                new GamificationSummaryResponse(
                        0,
                        0,
                        0,
                        1,
                        java.util.Collections.emptyList(),
                        java.util.Collections.emptyList()
                );

        when(authService.getMe(userId))
                .thenReturn(profile);

        when(testAttemptService.getProgress(jwt))
                .thenReturn(progress);

        when(gamificationService.getSummary(jwt))
                .thenReturn(gamification);

        facade.getDashboard(jwt);

        verify(authService).getMe(userId);
        verify(testAttemptService).getProgress(jwt);
        verify(gamificationService).getSummary(jwt);
    }
}
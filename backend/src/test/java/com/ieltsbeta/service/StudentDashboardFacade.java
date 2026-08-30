package com.ieltsbeta.service;

import com.ieltsbeta.dto.GamificationSummaryResponse;
import com.ieltsbeta.dto.MeResponse;
import com.ieltsbeta.dto.ProgressResponse;
import com.ieltsbeta.dto.StudentDashboardResponse;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * FACADE PATTERN
 *
 * Provides one simple interface for retrieving the student's dashboard
 * information instead of requiring the controller/client to communicate
 * with multiple services separately.
 *
 * The facade coordinates:
 * - AuthService
 * - TestAttemptService
 * - GamificationService
 *
 * The underlying services remain independent.
 */
@Service
public class StudentDashboardFacade {

    private final AuthService authService;
    private final TestAttemptService testAttemptService;
    private final GamificationService gamificationService;

    public StudentDashboardFacade(
            AuthService authService,
            TestAttemptService testAttemptService,
            GamificationService gamificationService) {

        this.authService = authService;
        this.testAttemptService = testAttemptService;
        this.gamificationService = gamificationService;
    }

    /**
     * Retrieves all information needed by the student's dashboard.
     *
     * @param jwt authenticated student's JWT
     * @return combined dashboard response
     */
    public StudentDashboardResponse getDashboard(Jwt jwt) {

        UUID userId = UUID.fromString(jwt.getSubject());

        MeResponse profile = authService.getMe(userId);

        ProgressResponse progress = testAttemptService.getProgress(jwt);

        GamificationSummaryResponse gamification =
                gamificationService.getSummary(jwt);

        return new StudentDashboardResponse(
                profile,
                progress,
                gamification
        );
    }
}
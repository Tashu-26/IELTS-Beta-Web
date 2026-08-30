package com.ieltsbeta.controller;

import com.ieltsbeta.dto.GoalUpdateRequest;
import com.ieltsbeta.dto.MeResponse;
import com.ieltsbeta.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class StudentControllerTest {

    @Test
    void updateGoal_shouldUpdateStudentGoal() {

        AuthService service =
                mock(AuthService.class);

        StudentController controller =
                new StudentController(service);

        Jwt jwt = mock(Jwt.class);

        UUID userId =
                UUID.fromString(
                        "33333333-3333-3333-3333-333333333333"
                );

        GoalUpdateRequest request =
                mock(GoalUpdateRequest.class);

        MeResponse expected =
                mock(MeResponse.class);

        when(jwt.getSubject())
                .thenReturn(userId.toString());

        when(service.updateGoal(
                userId,
                request
        )).thenReturn(expected);

        MeResponse result =
                controller.updateGoal(
                        jwt,
                        request
                );

        assertSame(expected, result);

        verify(jwt)
                .getSubject();

        verify(service)
                .updateGoal(
                        userId,
                        request
                );
    }
}
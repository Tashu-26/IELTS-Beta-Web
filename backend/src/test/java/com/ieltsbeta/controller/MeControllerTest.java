package com.ieltsbeta.controller;

import com.ieltsbeta.dto.MeResponse;
import com.ieltsbeta.dto.UpdateProfileRequest;
import com.ieltsbeta.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class MeControllerTest {

    @Test
    void me_shouldReturnCurrentUser() {

        AuthService service = mock(AuthService.class);

        MeController controller =
                new MeController(service);

        Jwt jwt = mock(Jwt.class);

        UUID userId =
                UUID.fromString(
                        "11111111-1111-1111-1111-111111111111"
                );

        MeResponse expected =
                mock(MeResponse.class);

        when(jwt.getSubject())
                .thenReturn(userId.toString());

        when(service.getMe(userId))
                .thenReturn(expected);

        MeResponse result =
                controller.me(jwt);

        assertSame(expected, result);

        verify(jwt)
                .getSubject();

        verify(service)
                .getMe(userId);
    }


    @Test
    void updateMe_shouldUpdateProfile() {

        AuthService service = mock(AuthService.class);

        MeController controller =
                new MeController(service);

        Jwt jwt = mock(Jwt.class);

        UUID userId =
                UUID.fromString(
                        "22222222-2222-2222-2222-222222222222"
                );

       UpdateProfileRequest request =
        mock(UpdateProfileRequest.class);
        MeResponse expected =
                mock(MeResponse.class);

        when(jwt.getSubject())
                .thenReturn(userId.toString());

        when(service.updateProfile(
                userId,
                request
        )).thenReturn(expected);

        MeResponse result =
                controller.updateMe(
                        jwt,
                        request
                );

        assertSame(expected, result);

        verify(jwt)
                .getSubject();

        verify(service)
                .updateProfile(
                        userId,
                        request
                );
    }
}
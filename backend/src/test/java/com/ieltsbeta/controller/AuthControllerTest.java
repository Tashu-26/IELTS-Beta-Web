package com.ieltsbeta.controller;

import com.ieltsbeta.dto.CompleteProfileRequest;
import com.ieltsbeta.dto.MeResponse;
import com.ieltsbeta.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {


@Mock
private AuthService authService;

@Mock
private Jwt jwt;

@InjectMocks
private AuthController authController;

@Test
void completeProfile_shouldCallAuthServiceAndReturnResponse() {
    UUID authUserId = UUID.randomUUID();
    String email = "test@example.com";

    CompleteProfileRequest request = mock(CompleteProfileRequest.class);
    MeResponse expectedResponse = mock(MeResponse.class);

    when(jwt.getSubject()).thenReturn(authUserId.toString());
    when(jwt.getClaimAsString("email")).thenReturn(email);

    when(authService.completeProfile(
            authUserId,
            email,
            request
    )).thenReturn(expectedResponse);

    MeResponse actualResponse =
            authController.completeProfile(jwt, request);

    assertSame(expectedResponse, actualResponse);

    verify(jwt).getSubject();
    verify(jwt).getClaimAsString("email");

    verify(authService).completeProfile(
            authUserId,
            email,
            request
    );
}

@Test
void completeProfile_shouldUseJwtUserIdAndEmail() {
    UUID authUserId = UUID.randomUUID();
    String email = "student@example.com";

    CompleteProfileRequest request = mock(CompleteProfileRequest.class);
    MeResponse expectedResponse = mock(MeResponse.class);

    when(jwt.getSubject()).thenReturn(authUserId.toString());
    when(jwt.getClaimAsString("email")).thenReturn(email);
    when(authService.completeProfile(
            authUserId,
            email,
            request
    )).thenReturn(expectedResponse);

    MeResponse result =
            authController.completeProfile(jwt, request);

    assertSame(expectedResponse, result);

    verify(authService, times(1)).completeProfile(
            eq(authUserId),
            eq(email),
            same(request)
    );
}


}

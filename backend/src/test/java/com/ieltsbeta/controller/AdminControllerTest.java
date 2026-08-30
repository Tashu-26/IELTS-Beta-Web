package com.ieltsbeta.controller;

import com.ieltsbeta.dto.AdminReportResponse;
import com.ieltsbeta.dto.AdminUserResponse;
import com.ieltsbeta.dto.UserRoleUpdateRequest;
import com.ieltsbeta.dto.UserStatusUpdateRequest;
import com.ieltsbeta.service.AdminUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminUserControllerTest {


@Mock
private AdminUserService adminUserService;

@Mock
private Jwt jwt;

@InjectMocks
private AdminUserController adminUserController;

@Test
void listUsers_shouldReturnUsersFromService() {
    List<AdminUserResponse> expectedUsers =
            List.of(
                    mock(AdminUserResponse.class),
                    mock(AdminUserResponse.class)
            );

    when(adminUserService.listUsers())
            .thenReturn(expectedUsers);

    List<AdminUserResponse> actualUsers =
            adminUserController.listUsers();

    assertSame(expectedUsers, actualUsers);

    verify(adminUserService, times(1))
            .listUsers();
}

@Test
void listUsers_shouldReturnEmptyListWhenNoUsersExist() {
    when(adminUserService.listUsers())
            .thenReturn(List.of());

    List<AdminUserResponse> result =
            adminUserController.listUsers();

    assertEquals(0, result.size());

    verify(adminUserService)
            .listUsers();
}

@Test
void updateRole_shouldCallServiceAndReturnResponse() {
    Long userId = 10L;

    UserRoleUpdateRequest request =
            mock(UserRoleUpdateRequest.class);

    AdminUserResponse expectedResponse =
            mock(AdminUserResponse.class);

    when(adminUserService.updateRole(
            jwt,
            userId,
            request
    )).thenReturn(expectedResponse);

    AdminUserResponse actualResponse =
            adminUserController.updateRole(
                    jwt,
                    userId,
                    request
            );

    assertSame(expectedResponse, actualResponse);

    verify(adminUserService, times(1))
            .updateRole(
                    jwt,
                    userId,
                    request
            );
}

@Test
void updateStatus_shouldCallServiceAndReturnResponse() {
    Long userId = 20L;

    UserStatusUpdateRequest request =
            mock(UserStatusUpdateRequest.class);

    AdminUserResponse expectedResponse =
            mock(AdminUserResponse.class);

    when(adminUserService.updateStatus(
            jwt,
            userId,
            request
    )).thenReturn(expectedResponse);

    AdminUserResponse actualResponse =
            adminUserController.updateStatus(
                    jwt,
                    userId,
                    request
            );

    assertSame(expectedResponse, actualResponse);

    verify(adminUserService, times(1))
            .updateStatus(
                    jwt,
                    userId,
                    request
            );
}

@Test
void report_shouldReturnReportFromService() {
    AdminReportResponse expectedReport =
            mock(AdminReportResponse.class);

    when(adminUserService.getReport())
            .thenReturn(expectedReport);

    AdminReportResponse actualReport =
            adminUserController.report();

    assertSame(expectedReport, actualReport);

    verify(adminUserService, times(1))
            .getReport();
}


}

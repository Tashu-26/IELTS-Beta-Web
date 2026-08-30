package com.ieltsbeta.controller;

import com.ieltsbeta.dto.AdminLogResponse;
import com.ieltsbeta.service.AdminLogService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AdminLogControllerTest {

    @Test
    void list_shouldReturnAllAdminLogs() {

        AdminLogService service = mock(AdminLogService.class);

        AdminLogController controller =
                new AdminLogController(service);

        List<AdminLogResponse> expected = List.of();

        when(service.listAll()).thenReturn(expected);

        List<AdminLogResponse> result = controller.list();

        assertEquals(expected, result);

        verify(service).listAll();
    }
}
package com.ieltsbeta.controller;

import com.ieltsbeta.dto.AnnouncementCreateRequest;
import com.ieltsbeta.dto.AnnouncementResponse;
import com.ieltsbeta.service.AnnouncementService;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class AnnouncementControllerTest {

    @Test
    void list_shouldReturnAnnouncements() {

        AnnouncementService service =
                mock(AnnouncementService.class);

        AnnouncementController controller =
                new AnnouncementController(service);

        List<AnnouncementResponse> expected =
                List.of();

        when(service.listAll())
                .thenReturn(expected);

        List<AnnouncementResponse> result =
                controller.list();

        assertSame(expected, result);

        verify(service).listAll();
    }


    @Test
    void create_shouldCallService() {

        AnnouncementService service =
                mock(AnnouncementService.class);

        AnnouncementController controller =
                new AnnouncementController(service);

        Jwt jwt = mock(Jwt.class);

        AnnouncementCreateRequest request =
                mock(AnnouncementCreateRequest.class);

        AnnouncementResponse expected =
                mock(AnnouncementResponse.class);

        when(service.create(jwt, request))
                .thenReturn(expected);

        AnnouncementResponse result =
                controller.create(jwt, request);

        assertSame(expected, result);

        verify(service)
                .create(jwt, request);
    }
}
package com.ieltsbeta.controller;

import com.ieltsbeta.dto.LiveClassCreateRequest;
import com.ieltsbeta.dto.LiveClassResponse;
import com.ieltsbeta.service.LiveClassService;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class LiveClassControllerTest {

    @Test
    void listForCourse_shouldReturnLiveClasses() {

        LiveClassService service = mock(LiveClassService.class);

        LiveClassController controller =
                new LiveClassController(service);

        Long courseId = 1L;

        List<LiveClassResponse> expected = List.of();

        when(service.listForCourse(courseId))
                .thenReturn(expected);

        List<LiveClassResponse> result =
                controller.listForCourse(courseId);

        assertSame(expected, result);

        verify(service)
                .listForCourse(courseId);
    }

    @Test
    void create_shouldCallService() {

        LiveClassService service = mock(LiveClassService.class);

        LiveClassController controller =
                new LiveClassController(service);

        Jwt jwt = mock(Jwt.class);

        Long teacherCourseId = 1L;

        LiveClassCreateRequest request =
                mock(LiveClassCreateRequest.class);

        LiveClassResponse expected =
                mock(LiveClassResponse.class);

        when(service.create(
                jwt,
                teacherCourseId,
                request
        )).thenReturn(expected);

        LiveClassResponse result =
                controller.create(
                        jwt,
                        teacherCourseId,
                        request
                );

        assertSame(expected, result);

        verify(service)
                .create(
                        jwt,
                        teacherCourseId,
                        request
                );
    }
}
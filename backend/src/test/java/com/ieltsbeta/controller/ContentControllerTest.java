package com.ieltsbeta.controller;

import com.ieltsbeta.dto.ContentCreateRequest;
import com.ieltsbeta.dto.ContentResponse;
import com.ieltsbeta.service.ContentService;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class ContentControllerTest {

    @Test
    void list_shouldReturnCourseContents() {

        ContentService service =
                mock(ContentService.class);

        ContentController controller =
                new ContentController(service);

        Long courseId = 1L;

        List<ContentResponse> expected =
                List.of();

        when(service.listForCourse(courseId))
                .thenReturn(expected);

        List<ContentResponse> result =
                controller.list(courseId);

        assertSame(expected, result);

        verify(service)
                .listForCourse(courseId);
    }


    @Test
    void create_shouldCallContentService() {

        ContentService service =
                mock(ContentService.class);

        ContentController controller =
                new ContentController(service);

        Jwt jwt = mock(Jwt.class);

        Long courseId = 1L;

        ContentCreateRequest request =
                mock(ContentCreateRequest.class);

        ContentResponse expected =
                mock(ContentResponse.class);

        when(service.create(
                jwt,
                courseId,
                request
        )).thenReturn(expected);

        ContentResponse result =
                controller.create(
                        jwt,
                        courseId,
                        request
                );

        assertSame(expected, result);

        verify(service)
                .create(
                        jwt,
                        courseId,
                        request
                );
    }
}
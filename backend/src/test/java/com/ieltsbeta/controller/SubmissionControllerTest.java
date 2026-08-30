package com.ieltsbeta.controller;

import com.ieltsbeta.dto.SubmissionCreateRequest;
import com.ieltsbeta.dto.SubmissionGradeRequest;
import com.ieltsbeta.dto.SubmissionResponse;
import com.ieltsbeta.service.SubmissionService;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class SubmissionControllerTest {

    @Test
    void create_shouldCreateSubmission() {

        SubmissionService service =
                mock(SubmissionService.class);

        SubmissionController controller =
                new SubmissionController(service);

        Jwt jwt = mock(Jwt.class);

        SubmissionCreateRequest request =
                mock(SubmissionCreateRequest.class);

        SubmissionResponse expected =
                mock(SubmissionResponse.class);

        when(service.create(
                jwt,
                request
        )).thenReturn(expected);

        SubmissionResponse result =
                controller.create(
                        jwt,
                        request
                );

        assertSame(expected, result);

        verify(service)
                .create(
                        jwt,
                        request
                );
    }


    @Test
    void mine_shouldReturnStudentSubmissions() {

        SubmissionService service =
                mock(SubmissionService.class);

        SubmissionController controller =
                new SubmissionController(service);

        Jwt jwt = mock(Jwt.class);

        List<SubmissionResponse> expected =
                List.of();

        when(service.myList(jwt))
                .thenReturn(expected);

        List<SubmissionResponse> result =
                controller.mine(jwt);

        assertSame(expected, result);

        verify(service)
                .myList(jwt);
    }


    @Test
    void teacherQueue_shouldReturnTeacherSubmissions() {

        SubmissionService service =
                mock(SubmissionService.class);

        SubmissionController controller =
                new SubmissionController(service);

        Jwt jwt = mock(Jwt.class);

        List<SubmissionResponse> expected =
                List.of();

        when(service.teacherQueue(jwt))
                .thenReturn(expected);

        List<SubmissionResponse> result =
                controller.teacherQueue(jwt);

        assertSame(expected, result);

        verify(service)
                .teacherQueue(jwt);
    }


    @Test
    void listAll_shouldReturnAllSubmissions() {

        SubmissionService service =
                mock(SubmissionService.class);

        SubmissionController controller =
                new SubmissionController(service);

        Jwt jwt = mock(Jwt.class);

        List<SubmissionResponse> expected =
                List.of();

        when(service.listAll(jwt))
                .thenReturn(expected);

        List<SubmissionResponse> result =
                controller.listAll(jwt);

        assertSame(expected, result);

        verify(service)
                .listAll(jwt);
    }


    @Test
    void grade_shouldGradeSubmission() {

        SubmissionService service =
                mock(SubmissionService.class);

        SubmissionController controller =
                new SubmissionController(service);

        Jwt jwt = mock(Jwt.class);

        Long submissionId = 40L;

        SubmissionGradeRequest request =
                mock(SubmissionGradeRequest.class);

        SubmissionResponse expected =
                mock(SubmissionResponse.class);

        when(service.grade(
                jwt,
                submissionId,
                request
        )).thenReturn(expected);

        SubmissionResponse result =
                controller.grade(
                        jwt,
                        submissionId,
                        request
                );

        assertSame(expected, result);

        verify(service)
                .grade(
                        jwt,
                        submissionId,
                        request
                );
    }
}
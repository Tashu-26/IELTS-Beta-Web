package com.ieltsbeta.controller;

import com.ieltsbeta.dto.AnswerOptionCreateRequest;
import com.ieltsbeta.dto.AnswerOptionResponse;
import com.ieltsbeta.dto.PracticeTestCreateRequest;
import com.ieltsbeta.dto.PracticeTestDetailResponse;
import com.ieltsbeta.dto.PracticeTestSummaryResponse;
import com.ieltsbeta.dto.QuestionCreateRequest;
import com.ieltsbeta.dto.QuestionResponse;
import com.ieltsbeta.service.PracticeTestService;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class PracticeTestControllerTest {

    @Test
    void listForCourse_shouldReturnPracticeTests() {

        PracticeTestService service =
                mock(PracticeTestService.class);

        PracticeTestController controller =
                new PracticeTestController(service);

        Long courseId = 1L;

        List<PracticeTestSummaryResponse> expected =
                List.of();

        when(service.listForCourse(courseId))
                .thenReturn(expected);

        List<PracticeTestSummaryResponse> result =
                controller.listForCourse(courseId);

        assertSame(expected, result);

        verify(service)
                .listForCourse(courseId);
    }


    @Test
    void getDetail_shouldReturnPracticeTestDetails() {

        PracticeTestService service =
                mock(PracticeTestService.class);

        PracticeTestController controller =
                new PracticeTestController(service);

        Jwt jwt = mock(Jwt.class);

        Long testId = 10L;

        PracticeTestDetailResponse expected =
                mock(PracticeTestDetailResponse.class);

        when(service.getDetail(jwt, testId))
                .thenReturn(expected);

        PracticeTestDetailResponse result =
                controller.getDetail(
                        jwt,
                        testId
                );

        assertSame(expected, result);

        verify(service)
                .getDetail(jwt, testId);
    }


    @Test
    void create_shouldCreatePracticeTest() {

        PracticeTestService service =
                mock(PracticeTestService.class);

        PracticeTestController controller =
                new PracticeTestController(service);

        Jwt jwt = mock(Jwt.class);

        PracticeTestCreateRequest request =
                mock(PracticeTestCreateRequest.class);

        PracticeTestSummaryResponse expected =
                mock(PracticeTestSummaryResponse.class);

        when(service.create(jwt, request))
                .thenReturn(expected);

        PracticeTestSummaryResponse result =
                controller.create(
                        jwt,
                        request
                );

        assertSame(expected, result);

        verify(service)
                .create(jwt, request);
    }


    @Test
    void addQuestion_shouldAddQuestion() {

        PracticeTestService service =
                mock(PracticeTestService.class);

        PracticeTestController controller =
                new PracticeTestController(service);

        Jwt jwt = mock(Jwt.class);

        Long testId = 20L;

        QuestionCreateRequest request =
                mock(QuestionCreateRequest.class);

        QuestionResponse expected =
                mock(QuestionResponse.class);

        when(service.addQuestion(
                jwt,
                testId,
                request
        )).thenReturn(expected);

        QuestionResponse result =
                controller.addQuestion(
                        jwt,
                        testId,
                        request
                );

        assertSame(expected, result);

        verify(service)
                .addQuestion(
                        jwt,
                        testId,
                        request
                );
    }


    @Test
    void addAnswerOption_shouldAddAnswerOption() {

        PracticeTestService service =
                    mock   (PracticeTestService.class);

        PracticeTestController controller =
                new PracticeTestController(service);

        Jwt jwt = mock(Jwt.class);

        Long questionId = 30L;

        AnswerOptionCreateRequest request =
                mock(AnswerOptionCreateRequest.class);

        AnswerOptionResponse expected =
                mock(AnswerOptionResponse.class);

        when(service.addAnswerOption(
                jwt,
                questionId,
                request
        )).thenReturn(expected);

        AnswerOptionResponse result =
                controller.addAnswerOption(
                        jwt,
                        questionId,
                        request
                );

        assertSame(expected, result);

        verify(service)
                .addAnswerOption(
                        jwt,
                        questionId,
                        request
                );
    }
}
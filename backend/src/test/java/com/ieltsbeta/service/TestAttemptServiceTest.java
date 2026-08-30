package com.ieltsbeta.service;

import com.ieltsbeta.dto.AnswerSubmissionDto;
import com.ieltsbeta.dto.AttemptResultResponse;
import com.ieltsbeta.dto.SubmitAttemptRequest;
import com.ieltsbeta.entity.AnswerOption;
import com.ieltsbeta.entity.PracticeTest;
import com.ieltsbeta.entity.Question;
import com.ieltsbeta.entity.Student;
import com.ieltsbeta.entity.TestAttempt;
import com.ieltsbeta.event.BandCheckEvent;
import com.ieltsbeta.event.MissionCompletedEvent;
import com.ieltsbeta.repository.AnswerOptionRepository;
import com.ieltsbeta.repository.PracticeTestRepository;
import com.ieltsbeta.repository.QuestionRepository;
import com.ieltsbeta.repository.StudentRepository;
import com.ieltsbeta.repository.TestAttemptRepository;
import com.ieltsbeta.repository.TestResultRepository;
import com.ieltsbeta.security.CurrentUserService;
import com.ieltsbeta.service.scoring.BandScoringStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Covers TestAttemptService.submitAttempt -- the core scoring engine -- with
 * the BandScoringStrategy mocked out (proving the STRATEGY PATTERN seam: the
 * service only ever calls strategy.toBand(...), it never computes bands
 * itself) and the GamificationService dependency replaced by an
 * ApplicationEventPublisher mock (proving the OBSERVER PATTERN seam: the
 * service publishes events and never touches GamificationService directly).
 */
@ExtendWith(MockitoExtension.class)
class TestAttemptServiceTest {

    @Mock private TestAttemptRepository testAttemptRepository;
    @Mock private TestResultRepository testResultRepository;
    @Mock private PracticeTestRepository practiceTestRepository;
    @Mock private QuestionRepository questionRepository;
    @Mock private AnswerOptionRepository answerOptionRepository;
    @Mock private StudentRepository studentRepository;
    @Mock private CurrentUserService currentUserService;
    @Mock private PracticeTestService practiceTestService;
    @Mock private ApplicationEventPublisher eventPublisher;
    @Mock private BandScoringStrategy bandScoringStrategy;

    private TestAttemptService testAttemptService;
    private Jwt jwt;
    private Student student;
    private PracticeTest practiceTest;

    @BeforeEach
    void setUp() {
        testAttemptService = new TestAttemptService(testAttemptRepository, testResultRepository,
                practiceTestRepository, questionRepository, answerOptionRepository, studentRepository,
                currentUserService, practiceTestService, eventPublisher, bandScoringStrategy);

        jwt = mock(Jwt.class);
        student = new Student();
        student.setStudentId(1L);
        practiceTest = new PracticeTest();
        practiceTest.setTestId(100L);
    }

    private TestAttempt unsubmittedAttempt() {
        TestAttempt attempt = new TestAttempt();
        attempt.setAttemptId(50L);
        attempt.setStudent(student);
        attempt.setTest(practiceTest);
        return attempt;
    }

    private Question gradableQuestion(long id, String skill, int marks, long correctOptionId) {
        Question q = new Question();
        q.setQuestionId(id);
        q.setSkill(skill);
        q.setMarks(marks);
        AnswerOption correct = new AnswerOption();
        correct.setOptionId(correctOptionId);
        correct.setIsCorrect(true);
        when(answerOptionRepository.findByQuestion_QuestionId(id)).thenReturn(List.of(correct));
        return q;
    }

    @Test
    void submitAttempt_scoresCorrectAnswers_andDelegatesToStrategy() {
        when(currentUserService.requireStudent(jwt)).thenReturn(student);
        TestAttempt attempt = unsubmittedAttempt();
        when(testAttemptRepository.findById(50L)).thenReturn(Optional.of(attempt));

        Question q1 = gradableQuestion(1L, "Listening", 1, 10L);
        Question q2 = gradableQuestion(2L, "Reading", 1, 20L);
        when(questionRepository.findByTest_TestId(100L)).thenReturn(List.of(q1, q2));

        when(bandScoringStrategy.toBand(anyDouble())).thenReturn(new BigDecimal("6.5"));

        SubmitAttemptRequest request = new SubmitAttemptRequest(List.of(
                new AnswerSubmissionDto(1L, 10L),  // correct
                new AnswerSubmissionDto(2L, 999L)  // wrong
        ));

        AttemptResultResponse result = testAttemptService.submitAttempt(jwt, 50L, request);

        assertThat(attempt.getScore()).isEqualByComparingTo("1");
        assertThat(result.overallBand()).isEqualByComparingTo("6.5");
        // overall + Listening + Reading (Writing/Speaking have no questions, so no call for those)
        verify(bandScoringStrategy, times(3)).toBand(anyDouble());
        verify(testAttemptRepository).save(attempt);
        verify(testResultRepository).save(any());
    }

    @Test
    void submitAttempt_publishesMissionAndBandEvents_whenBandComputed() {
        when(currentUserService.requireStudent(jwt)).thenReturn(student);
        TestAttempt attempt = unsubmittedAttempt();
        when(testAttemptRepository.findById(50L)).thenReturn(Optional.of(attempt));
        Question q1 = gradableQuestion(1L, "Listening", 1, 10L);
        when(questionRepository.findByTest_TestId(100L)).thenReturn(List.of(q1));
        when(bandScoringStrategy.toBand(anyDouble())).thenReturn(new BigDecimal("5.0"));

        testAttemptService.submitAttempt(jwt, 50L,
                new SubmitAttemptRequest(List.of(new AnswerSubmissionDto(1L, 10L))));

        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher, times(2)).publishEvent(captor.capture());
        assertThat(captor.getAllValues()).anyMatch(e -> e instanceof BandCheckEvent);
        assertThat(captor.getAllValues()).anyMatch(e -> e instanceof MissionCompletedEvent
                && "take_test".equals(((MissionCompletedEvent) e).getMissionKey()));
    }

    @Test
    void submitAttempt_publishesOnlyMissionEvent_whenNoGradableQuestions() {
        when(currentUserService.requireStudent(jwt)).thenReturn(student);
        TestAttempt attempt = unsubmittedAttempt();
        when(testAttemptRepository.findById(50L)).thenReturn(Optional.of(attempt));
        when(questionRepository.findByTest_TestId(100L)).thenReturn(List.of());

        testAttemptService.submitAttempt(jwt, 50L, new SubmitAttemptRequest(List.of()));

        verify(eventPublisher, times(1)).publishEvent(any(MissionCompletedEvent.class));
        verify(eventPublisher, times(0)).publishEvent(any(BandCheckEvent.class));
        verify(bandScoringStrategy, times(0)).toBand(anyDouble());
    }

    @Test
    void submitAttempt_throwsForbidden_whenAttemptBelongsToAnotherStudent() {
        when(currentUserService.requireStudent(jwt)).thenReturn(student);
        TestAttempt attempt = unsubmittedAttempt();
        Student otherStudent = new Student();
        otherStudent.setStudentId(999L);
        attempt.setStudent(otherStudent);
        when(testAttemptRepository.findById(50L)).thenReturn(Optional.of(attempt));

        assertThatThrownBy(() -> testAttemptService.submitAttempt(jwt, 50L, new SubmitAttemptRequest(List.of())))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("not your attempt");
    }

    @Test
    void submitAttempt_throwsConflict_whenAlreadySubmitted() {
        when(currentUserService.requireStudent(jwt)).thenReturn(student);
        TestAttempt attempt = unsubmittedAttempt();
        attempt.setSubmitTime(java.time.OffsetDateTime.now());
        when(testAttemptRepository.findById(50L)).thenReturn(Optional.of(attempt));

        assertThatThrownBy(() -> testAttemptService.submitAttempt(jwt, 50L, new SubmitAttemptRequest(List.of())))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("already been submitted");
    }

    @Test
    void submitAttempt_throwsNotFound_whenAttemptDoesNotExist() {
        when(currentUserService.requireStudent(jwt)).thenReturn(student);
        when(testAttemptRepository.findById(50L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> testAttemptService.submitAttempt(jwt, 50L, new SubmitAttemptRequest(List.of())))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Attempt not found");
    }

    @Test
    void getAttempt_returnsResult_forOwningStudent() {
        when(currentUserService.requireStudent(jwt)).thenReturn(student);
        TestAttempt attempt = unsubmittedAttempt();
        when(testAttemptRepository.findById(50L)).thenReturn(Optional.of(attempt));
        when(testResultRepository.findByAttempt_AttemptId(50L)).thenReturn(Optional.empty());

        AttemptResultResponse result = testAttemptService.getAttempt(jwt, 50L);

        assertThat(result.attemptId()).isEqualTo(50L);
    }
}

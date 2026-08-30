package com.ieltsbeta.service;

import com.ieltsbeta.dto.*;
import com.ieltsbeta.entity.AnswerOption;
import com.ieltsbeta.entity.PracticeTest;
import com.ieltsbeta.entity.Question;
import com.ieltsbeta.entity.Student;
import com.ieltsbeta.entity.TestAttempt;
import com.ieltsbeta.entity.TestResult;
import com.ieltsbeta.event.BandCheckEvent;
import com.ieltsbeta.event.MissionCompletedEvent;
import com.ieltsbeta.repository.*;
import com.ieltsbeta.security.CurrentUserService;
import com.ieltsbeta.service.scoring.BandScoringStrategy;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Handles taking a practice test: starting an attempt, submitting answers,
 * auto-scoring MCQ-style questions, and computing simple band-score
 * estimates + student progress.
 *
 * SCORING SCOPE: only questions with at least one answer option marked
 * correct are gradable. Writing/Speaking questions (which need a human to
 * grade an essay or recording) are silently excluded from scoring here --
 * that workflow is the "submissions" feature (Phase 12), not this MCQ engine.
 *
 * BAND FORMULA: percentage-of-marks-correct is mapped to a 0.5-increment
 * IELTS-style band. STRATEGY PATTERN: the actual formula lives behind the
 * injected BandScoringStrategy (see that package) instead of being hard-coded
 * here, so the marking scheme can be swapped without touching this class.
 *
 * OBSERVER PATTERN: this service no longer calls GamificationService
 * directly. It publishes MissionCompletedEvent / BandCheckEvent via
 * ApplicationEventPublisher; GamificationService listens for them.
 */
@Service
public class TestAttemptService {

    private final TestAttemptRepository testAttemptRepository;
    private final TestResultRepository testResultRepository;
    private final PracticeTestRepository practiceTestRepository;
    private final QuestionRepository questionRepository;
    private final AnswerOptionRepository answerOptionRepository;
    private final StudentRepository studentRepository;
    private final CurrentUserService currentUserService;
    private final PracticeTestService practiceTestService;
    private final ApplicationEventPublisher eventPublisher;
    private final BandScoringStrategy bandScoringStrategy;

    public TestAttemptService(TestAttemptRepository testAttemptRepository,
                               TestResultRepository testResultRepository,
                               PracticeTestRepository practiceTestRepository,
                               QuestionRepository questionRepository,
                               AnswerOptionRepository answerOptionRepository,
                               StudentRepository studentRepository,
                               CurrentUserService currentUserService,
                               PracticeTestService practiceTestService,
                               ApplicationEventPublisher eventPublisher,
                               BandScoringStrategy bandScoringStrategy) {
        this.testAttemptRepository = testAttemptRepository;
        this.testResultRepository = testResultRepository;
        this.practiceTestRepository = practiceTestRepository;
        this.questionRepository = questionRepository;
        this.answerOptionRepository = answerOptionRepository;
        this.studentRepository = studentRepository;
        this.currentUserService = currentUserService;
        this.practiceTestService = practiceTestService;
        this.eventPublisher = eventPublisher;
        this.bandScoringStrategy = bandScoringStrategy;
    }

    @Transactional
    public AttemptStartResponse startAttempt(Jwt jwt, StartAttemptRequest request) {
        Student student = currentUserService.requireStudent(jwt);
        PracticeTest test = practiceTestRepository.findById(request.testId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Practice test not found."));

        TestAttempt attempt = new TestAttempt();
        attempt.setStudent(student);
        attempt.setTest(test);
        attempt = testAttemptRepository.save(attempt);

        // Reuses the existing student-safe question view (correct answers hidden).
        PracticeTestDetailResponse detail = practiceTestService.getDetail(jwt, test.getTestId());

        return new AttemptStartResponse(attempt.getAttemptId(), detail);
    }

    @Transactional
    public AttemptResultResponse submitAttempt(Jwt jwt, Long attemptId, SubmitAttemptRequest request) {
        Student student = currentUserService.requireStudent(jwt);
        TestAttempt attempt = testAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Attempt not found."));

        if (!attempt.getStudent().getStudentId().equals(student.getStudentId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This is not your attempt.");
        }
        if (attempt.getSubmitTime() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This attempt has already been submitted.");
        }

        Map<Long, Long> selections = request.answers().stream()
                .filter(a -> a.selectedOptionId() != null)
                .collect(Collectors.toMap(AnswerSubmissionDto::questionId, AnswerSubmissionDto::selectedOptionId,
                        (a, b) -> a));

        List<Question> questions = questionRepository.findByTest_TestId(attempt.getTest().getTestId());

        int totalPossible = 0;
        int scored = 0;
        Map<String, int[]> skillTotals = new HashMap<>(); // skill -> [possibleMarks, scoredMarks]

        for (Question q : questions) {
            List<AnswerOption> options = answerOptionRepository.findByQuestion_QuestionId(q.getQuestionId());
            Optional<AnswerOption> correct = options.stream()
                    .filter(o -> Boolean.TRUE.equals(o.getIsCorrect()))
                    .findFirst();
            if (correct.isEmpty()) {
                continue; // ungradable question (e.g. Writing/Speaking) -- excluded entirely
            }

            int marks = q.getMarks();
            totalPossible += marks;
            int[] skillTotal = skillTotals.computeIfAbsent(q.getSkill(), k -> new int[2]);
            skillTotal[0] += marks;

            Long selectedId = selections.get(q.getQuestionId());
            if (selectedId != null && selectedId.equals(correct.get().getOptionId())) {
                scored += marks;
                skillTotal[1] += marks;
            }
        }

        BigDecimal overallBand = totalPossible > 0
                ? bandScoringStrategy.toBand(100.0 * scored / totalPossible)
                : null;

        attempt.setSubmitTime(OffsetDateTime.now());
        attempt.setScore(BigDecimal.valueOf(scored));
        attempt.setBandScore(overallBand);
        testAttemptRepository.save(attempt);

        String feedback = totalPossible > 0
                ? String.format("You scored %d out of %d marks (%.0f%%).", scored, totalPossible,
                        100.0 * scored / totalPossible)
                : "This test has no automatically gradable questions yet.";

        TestResult result = new TestResult();
        result.setAttempt(attempt);
        result.setOverallBand(overallBand);
        result.setListening(bandForSkill(skillTotals, "Listening"));
        result.setReading(bandForSkill(skillTotals, "Reading"));
        result.setWriting(bandForSkill(skillTotals, "Writing"));
        result.setSpeaking(bandForSkill(skillTotals, "Speaking"));
        result.setFeedback(feedback);
        testResultRepository.save(result);

        if (overallBand != null) {
            student.setCurrentBand(overallBand);
            studentRepository.save(student);
            eventPublisher.publishEvent(new BandCheckEvent(student));
        }

        eventPublisher.publishEvent(new MissionCompletedEvent(student.getStudentId(), "take_test"));

        return toAttemptResultResponse(attempt, result);
    }

    @Transactional(readOnly = true)
    public AttemptResultResponse getAttempt(Jwt jwt, Long attemptId) {
        Student student = currentUserService.requireStudent(jwt);
        TestAttempt attempt = testAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Attempt not found."));
        if (!attempt.getStudent().getStudentId().equals(student.getStudentId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This is not your attempt.");
        }
        TestResult result = testResultRepository.findByAttempt_AttemptId(attemptId).orElse(null);
        return toAttemptResultResponse(attempt, result);
    }

    @Transactional(readOnly = true)
    public List<AttemptSummaryResponse> listMyAttempts(Jwt jwt) {
        Student student = currentUserService.requireStudent(jwt);
        return testAttemptRepository.findByStudent_StudentIdOrderByStartTimeDesc(student.getStudentId())
                .stream()
                .filter(a -> a.getSubmitTime() != null)
                .map(a -> new AttemptSummaryResponse(
                        a.getAttemptId(),
                        a.getTest().getTestId(),
                        a.getTest().getTitle(),
                        a.getTest().getCategory(),
                        a.getSubmitTime(),
                        a.getBandScore()
                ))
                .toList();
    }

    @Transactional
    public ProgressResponse getProgress(Jwt jwt) {
        Student student = currentUserService.requireStudent(jwt);
        List<TestResult> results = testResultRepository.findByAttempt_Student_StudentId(student.getStudentId());

        List<AttemptSummaryResponse> recent = listMyAttempts(jwt).stream().limit(10).toList();

        eventPublisher.publishEvent(new MissionCompletedEvent(student.getStudentId(), "review_progress"));

        return new ProgressResponse(
                student.getTargetBand(),
                student.getCurrentBand(),
                recent.size(),
                average(results.stream().map(TestResult::getListening).filter(Objects::nonNull).toList()),
                average(results.stream().map(TestResult::getReading).filter(Objects::nonNull).toList()),
                average(results.stream().map(TestResult::getWriting).filter(Objects::nonNull).toList()),
                average(results.stream().map(TestResult::getSpeaking).filter(Objects::nonNull).toList()),
                recent
        );
    }

    private BigDecimal bandForSkill(Map<String, int[]> skillTotals, String skill) {
        int[] totals = skillTotals.get(skill);
        if (totals == null || totals[0] == 0) {
            return null;
        }
        return bandScoringStrategy.toBand(100.0 * totals[1] / totals[0]);
    }

    private static BigDecimal average(List<BigDecimal> values) {
        if (values.isEmpty()) {
            return null;
        }
        double avg = values.stream().mapToDouble(BigDecimal::doubleValue).average().orElse(0);
        double roundedToHalf = Math.round(avg * 2) / 2.0;
        return BigDecimal.valueOf(roundedToHalf).setScale(1, RoundingMode.HALF_UP);
    }

    private AttemptResultResponse toAttemptResultResponse(TestAttempt attempt, TestResult result) {
        return new AttemptResultResponse(
                attempt.getAttemptId(),
                attempt.getTest().getTestId(),
                attempt.getTest().getTitle(),
                attempt.getStartTime(),
                attempt.getSubmitTime(),
                attempt.getScore(),
                result != null ? result.getOverallBand() : null,
                result != null ? result.getListening() : null,
                result != null ? result.getReading() : null,
                result != null ? result.getWriting() : null,
                result != null ? result.getSpeaking() : null,
                result != null ? result.getFeedback() : null
        );
    }
}

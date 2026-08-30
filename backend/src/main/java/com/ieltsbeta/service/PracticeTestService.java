package com.ieltsbeta.service;

import com.ieltsbeta.dto.*;
import com.ieltsbeta.entity.*;
import com.ieltsbeta.repository.AnswerOptionRepository;
import com.ieltsbeta.repository.PracticeTestRepository;
import com.ieltsbeta.repository.QuestionRepository;
import com.ieltsbeta.security.CurrentUserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * CRUD/authoring for practice tests, questions and answer options.
 * Taking a test and scoring an attempt is a separate concern (Phase 11).
 */
@Service
public class PracticeTestService {

    private final PracticeTestRepository practiceTestRepository;
    private final QuestionRepository questionRepository;
    private final AnswerOptionRepository answerOptionRepository;
    private final CourseService courseService;
    private final CurrentUserService currentUserService;

    public PracticeTestService(PracticeTestRepository practiceTestRepository,
                                QuestionRepository questionRepository,
                                AnswerOptionRepository answerOptionRepository,
                                CourseService courseService,
                                CurrentUserService currentUserService) {
        this.practiceTestRepository = practiceTestRepository;
        this.questionRepository = questionRepository;
        this.answerOptionRepository = answerOptionRepository;
        this.courseService = courseService;
        this.currentUserService = currentUserService;
    }

    @Transactional(readOnly = true)
    public List<PracticeTestSummaryResponse> listForCourse(Long courseId) {
        return practiceTestRepository.findByCourse_CourseId(courseId).stream().map(this::toSummary).toList();
    }

    @Transactional(readOnly = true)
    public PracticeTestDetailResponse getDetail(Jwt jwt, Long testId) {
        PracticeTest test = findTestOrThrow(testId);
        boolean canSeeAnswers = canManage(jwt, test.getCourse().getCourseId());

        List<QuestionResponse> questions = questionRepository.findByTest_TestId(testId).stream()
                .map(q -> toQuestionResponse(q, canSeeAnswers))
                .toList();

        return new PracticeTestDetailResponse(
                test.getTestId(),
                test.getCourse().getCourseId(),
                test.getTitle(),
                test.getCategory(),
                test.getDuration(),
                test.getTotalMarks(),
                questions
        );
    }

    @Transactional
    public PracticeTestSummaryResponse create(Jwt jwt, PracticeTestCreateRequest request) {
        courseService.requireOwnerOrAdmin(jwt, request.courseId());
        Course course = courseService.findCourseOrThrow(request.courseId());

        PracticeTest test = new PracticeTest();
        test.setCourse(course);
        test.setTitle(request.title());
        test.setCategory(request.category());
        test.setDuration(request.duration());
        test.setTotalMarks(request.totalMarks());
        test = practiceTestRepository.save(test);

        return toSummary(test);
    }

    @Transactional
    public QuestionResponse addQuestion(Jwt jwt, Long testId, QuestionCreateRequest request) {
        PracticeTest test = findTestOrThrow(testId);
        courseService.requireOwnerOrAdmin(jwt, test.getCourse().getCourseId());

        Question question = new Question();
        question.setTest(test);
        question.setQuestionText(request.questionText());
        question.setSkill(request.skill());
        question.setMarks(request.marks() != null ? request.marks() : 1);
        question = questionRepository.save(question);

        return toQuestionResponse(question, true);
    }

    @Transactional
    public AnswerOptionResponse addAnswerOption(Jwt jwt, Long questionId, AnswerOptionCreateRequest request) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found."));
        courseService.requireOwnerOrAdmin(jwt, question.getTest().getCourse().getCourseId());

        AnswerOption option = new AnswerOption();
        option.setQuestion(question);
        option.setOptionText(request.optionText());
        option.setIsCorrect(request.isCorrect());
        option = answerOptionRepository.save(option);

        return toAnswerOptionResponse(option, true);
    }

    private boolean canManage(Jwt jwt, Long courseId) {
        try {
            courseService.requireOwnerOrAdmin(jwt, courseId);
            return true;
        } catch (ResponseStatusException e) {
            return false;
        }
    }

    private PracticeTest findTestOrThrow(Long testId) {
        return practiceTestRepository.findById(testId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Practice test not found."));
    }

    private PracticeTestSummaryResponse toSummary(PracticeTest test) {
        return new PracticeTestSummaryResponse(
                test.getTestId(),
                test.getCourse().getCourseId(),
                test.getTitle(),
                test.getCategory(),
                test.getDuration(),
                test.getTotalMarks()
        );
    }

    private QuestionResponse toQuestionResponse(Question question, boolean includeAnswers) {
        List<AnswerOptionResponse> options = answerOptionRepository.findByQuestion_QuestionId(question.getQuestionId())
                .stream().map(o -> toAnswerOptionResponse(o, includeAnswers)).toList();

        return new QuestionResponse(
                question.getQuestionId(),
                question.getQuestionText(),
                question.getSkill(),
                question.getMarks(),
                options
        );
    }

    private AnswerOptionResponse toAnswerOptionResponse(AnswerOption option, boolean includeAnswer) {
        return new AnswerOptionResponse(
                option.getOptionId(),
                option.getOptionText(),
                includeAnswer ? option.getIsCorrect() : null
        );
    }
}

package com.ieltsbeta.service;

import com.ieltsbeta.dto.SubmissionCreateRequest;
import com.ieltsbeta.dto.SubmissionGradeRequest;
import com.ieltsbeta.dto.SubmissionResponse;
import com.ieltsbeta.entity.*;
import com.ieltsbeta.repository.PracticeTestRepository;
import com.ieltsbeta.repository.SubmissionRepository;
import com.ieltsbeta.repository.TeacherCourseRepository;
import com.ieltsbeta.event.MissionCompletedEvent;
import com.ieltsbeta.security.CurrentUserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

@Service
public class SubmissionService {

    private static final Set<String> VALID_SKILLS = Set.of("Writing", "Speaking");

    private final SubmissionRepository submissionRepository;
    private final PracticeTestRepository practiceTestRepository;
    private final TeacherCourseRepository teacherCourseRepository;
    private final CourseService courseService;
    private final CurrentUserService currentUserService;
    private final ApplicationEventPublisher eventPublisher;

    public SubmissionService(SubmissionRepository submissionRepository,
                              PracticeTestRepository practiceTestRepository,
                              TeacherCourseRepository teacherCourseRepository,
                              CourseService courseService,
                              CurrentUserService currentUserService,
                              ApplicationEventPublisher eventPublisher) {
        this.submissionRepository = submissionRepository;
        this.practiceTestRepository = practiceTestRepository;
        this.teacherCourseRepository = teacherCourseRepository;
        this.courseService = courseService;
        this.currentUserService = currentUserService;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public SubmissionResponse create(Jwt jwt, SubmissionCreateRequest request) {
        Student student = currentUserService.requireStudent(jwt);
        Course course = courseService.findCourseOrThrow(request.courseId());

        if (!VALID_SKILLS.contains(request.skill())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Skill must be Writing or Speaking.");
        }
        boolean isWriting = "Writing".equals(request.skill());
        boolean hasText = request.textContent() != null && !request.textContent().isBlank();
        boolean hasAudio = request.audioUrl() != null && !request.audioUrl().isBlank();
        if (isWriting && !hasText) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Writing submissions need text content.");
        }
        if (!isWriting && !hasAudio) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Speaking submissions need an audio recording.");
        }

        Submission submission = new Submission();
        submission.setStudent(student);
        submission.setCourse(course);
        submission.setSkill(request.skill());
        submission.setSubmissionType(request.submissionType());
        submission.setTextContent(isWriting ? request.textContent() : null);
        submission.setAudioUrl(isWriting ? null : request.audioUrl());
        submission.setStatus("Pending");

        if (request.practiceTestId() != null) {
            PracticeTest test = practiceTestRepository.findById(request.practiceTestId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Practice test not found."));
            submission.setPracticeTest(test);
        }

        submission = submissionRepository.save(submission);
        eventPublisher.publishEvent(new MissionCompletedEvent(student.getStudentId(), "submit_work"));
        return toResponse(submission);
    }

    @Transactional(readOnly = true)
    public List<SubmissionResponse> myList(Jwt jwt) {
        Student student = currentUserService.requireStudent(jwt);
        return submissionRepository.findByStudent_StudentIdOrderBySubmittedAtDesc(student.getStudentId())
                .stream().map(this::toResponse).toList();
    }

    /** All Pending/Graded submissions across every course this teacher teaches. */
    @Transactional(readOnly = true)
    public List<SubmissionResponse> teacherQueue(Jwt jwt) {
        Teacher teacher = currentUserService.requireTeacher(jwt);
        List<Long> courseIds = teacherCourseRepository.findByTeacher_TeacherId(teacher.getTeacherId())
                .stream().map(tc -> tc.getCourse().getCourseId()).toList();
        if (courseIds.isEmpty()) {
            return List.of();
        }
        return submissionRepository.findByCourse_CourseIdInOrderBySubmittedAtDesc(courseIds)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<SubmissionResponse> listAll(Jwt jwt) {
        currentUserService.requireAdmin(jwt);
        return submissionRepository.findAllByOrderBySubmittedAtDesc().stream().map(this::toResponse).toList();
    }

    @Transactional
    public SubmissionResponse grade(Jwt jwt, Long submissionId, SubmissionGradeRequest request) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Submission not found."));

        AppUser grader = courseService.requireOwnerOrAdminReturningUser(jwt, submission.getCourse().getCourseId());

        submission.setBandScore(request.bandScore());
        submission.setFeedback(request.feedback());
        submission.setStatus("Graded");
        submission.setGradedBy(grader);
        submission.setGradedAt(OffsetDateTime.now());
        submission = submissionRepository.save(submission);

        return toResponse(submission);
    }

    private SubmissionResponse toResponse(Submission s) {
        return new SubmissionResponse(
                s.getSubmissionId(),
                s.getCourse().getCourseId(),
                s.getCourse().getTitle(),
                s.getSkill(),
                s.getSubmissionType(),
                s.getTextContent(),
                s.getAudioUrl(),
                s.getStatus(),
                s.getBandScore(),
                s.getFeedback(),
                s.getGradedBy() != null
                        ? s.getGradedBy().getPerson().getFirstName() + " " + s.getGradedBy().getPerson().getLastName()
                        : null,
                s.getStudent().getStudentId(),
                s.getStudent().getUser().getPerson().getFirstName() + " "
                        + s.getStudent().getUser().getPerson().getLastName(),
                s.getSubmittedAt(),
                s.getGradedAt()
        );
    }
}

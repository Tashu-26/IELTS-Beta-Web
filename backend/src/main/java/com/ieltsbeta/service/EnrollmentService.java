package com.ieltsbeta.service;

import com.ieltsbeta.dto.CourseEnrollmentResponse;
import com.ieltsbeta.dto.EnrollRequest;
import com.ieltsbeta.dto.EnrollmentResponse;
import com.ieltsbeta.entity.Course;
import com.ieltsbeta.entity.Enrollment;
import com.ieltsbeta.entity.Student;
import com.ieltsbeta.repository.EnrollmentRepository;
import com.ieltsbeta.security.CurrentUserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CurrentUserService currentUserService;
    private final CourseService courseService;

    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                              CurrentUserService currentUserService,
                              CourseService courseService) {
        this.enrollmentRepository = enrollmentRepository;
        this.currentUserService = currentUserService;
        this.courseService = courseService;
    }

    /**
     * Self-enrollment grants immediate "Active" access (no manual approval
     * step -- kept simple; the schema's Pending/Cancelled statuses remain
     * available for future use, e.g. an admin revoking access).
     */
    @Transactional
    public EnrollmentResponse enroll(Jwt jwt, EnrollRequest request) {
        Student student = currentUserService.requireStudent(jwt);
        Course course = courseService.findCourseOrThrow(request.courseId());

        if (enrollmentRepository.existsByStudent_StudentIdAndCourse_CourseId(
                student.getStudentId(), course.getCourseId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Already enrolled in this course.");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setStatus("Active");
        enrollment = enrollmentRepository.save(enrollment);

        return toResponse(enrollment);
    }

    @Transactional(readOnly = true)
    public List<EnrollmentResponse> myEnrollments(Jwt jwt) {
        Student student = currentUserService.requireStudent(jwt);
        return enrollmentRepository.findByStudent_StudentId(student.getStudentId())
                .stream().map(this::toResponse).toList();
    }

    /** Teacher who owns the course (or Admin) sees who's enrolled. */
    @Transactional(readOnly = true)
    public List<CourseEnrollmentResponse> listForCourse(Jwt jwt, Long courseId) {
        courseService.requireOwnerOrAdmin(jwt, courseId);
        return enrollmentRepository.findByCourse_CourseId(courseId).stream()
                .map(e -> new CourseEnrollmentResponse(
                        e.getEnrollmentId(),
                        e.getStudent().getStudentId(),
                        e.getStudent().getUser().getPerson().getFirstName() + " "
                                + e.getStudent().getUser().getPerson().getLastName(),
                        e.getStatus(),
                        e.getEnrolledAt()
                ))
                .toList();
    }

    private EnrollmentResponse toResponse(Enrollment enrollment) {
        return new EnrollmentResponse(
                enrollment.getEnrollmentId(),
                enrollment.getCourse().getCourseId(),
                enrollment.getCourse().getTitle(),
                enrollment.getStatus(),
                enrollment.getEnrolledAt()
        );
    }
}

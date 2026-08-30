package com.ieltsbeta.service;

import com.ieltsbeta.dto.CourseCreateRequest;
import com.ieltsbeta.dto.CourseResponse;
import com.ieltsbeta.entity.AppUser;
import com.ieltsbeta.entity.Course;
import com.ieltsbeta.entity.Teacher;
import com.ieltsbeta.entity.TeacherCourse;
import com.ieltsbeta.repository.CourseRepository;
import com.ieltsbeta.repository.TeacherCourseRepository;
import com.ieltsbeta.security.CurrentUserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final TeacherCourseRepository teacherCourseRepository;
    private final CurrentUserService currentUserService;

    public CourseService(CourseRepository courseRepository,
                          TeacherCourseRepository teacherCourseRepository,
                          CurrentUserService currentUserService) {
        this.courseRepository = courseRepository;
        this.teacherCourseRepository = teacherCourseRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> listCourses() {
        return courseRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public CourseResponse getCourse(Long courseId) {
        return toResponse(findCourseOrThrow(courseId));
    }

    /** Creating a course as a Teacher immediately assigns that teacher to it. */
    @Transactional
    public CourseResponse createCourse(Jwt jwt, CourseCreateRequest request) {
        Course course = new Course();
        course.setTitle(request.title());
        course.setDescription(request.description());
        course.setLevel(request.level());
        course.setDuration(request.duration());
        course = courseRepository.save(course);

        Teacher teacher = findTeacherIfPresent(jwt);
        if (teacher != null) {
            TeacherCourse link = new TeacherCourse();
            link.setTeacher(teacher);
            link.setCourse(course);
            link.setIsActive(true);
            teacherCourseRepository.save(link);
        }

        return toResponse(course);
    }

    @Transactional
    public CourseResponse updateCourse(Jwt jwt, Long courseId, CourseCreateRequest request) {
        Course course = findCourseOrThrow(courseId);
        requireOwnerOrAdmin(jwt, courseId);

        course.setTitle(request.title());
        course.setDescription(request.description());
        course.setLevel(request.level());
        course.setDuration(request.duration());
        return toResponse(courseRepository.save(course));
    }

    /** Throws 403 unless the caller is an Admin, or a Teacher assigned to this course. */
    void requireOwnerOrAdmin(Jwt jwt, Long courseId) {
        try {
            currentUserService.requireAdmin(jwt);
            return;
        } catch (ResponseStatusException ignored) {
            // not an admin, fall through to teacher-ownership check
        }
        Teacher teacher = currentUserService.requireTeacher(jwt);
        boolean owns = teacherCourseRepository.existsByTeacher_TeacherIdAndCourse_CourseId(
                teacher.getTeacherId(), courseId);
        if (!owns) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not teach this course.");
        }
    }

    /** Same ownership check as {@link #requireOwnerOrAdmin}, but also returns the caller's AppUser. */
    AppUser requireOwnerOrAdminReturningUser(Jwt jwt, Long courseId) {
        requireOwnerOrAdmin(jwt, courseId);
        return currentUserService.requireUser(jwt);
    }

    Course findCourseOrThrow(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found."));
    }

    private Teacher findTeacherIfPresent(Jwt jwt) {
        try {
            return currentUserService.requireTeacher(jwt);
        } catch (ResponseStatusException e) {
            return null;
        }
    }

    private CourseResponse toResponse(Course course) {
        return new CourseResponse(
                course.getCourseId(),
                course.getTitle(),
                course.getDescription(),
                course.getLevel(),
                course.getDuration()
        );
    }
}

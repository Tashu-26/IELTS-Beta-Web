package com.ieltsbeta.service;

import com.ieltsbeta.dto.TeacherCourseResponse;
import com.ieltsbeta.entity.Course;
import com.ieltsbeta.entity.Teacher;
import com.ieltsbeta.entity.TeacherCourse;
import com.ieltsbeta.repository.TeacherCourseRepository;
import com.ieltsbeta.repository.TeacherRepository;
import com.ieltsbeta.security.CurrentUserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TeacherCourseService {

    private final TeacherCourseRepository teacherCourseRepository;
    private final TeacherRepository teacherRepository;
    private final CurrentUserService currentUserService;
    private final CourseService courseService;

    public TeacherCourseService(TeacherCourseRepository teacherCourseRepository,
                                 TeacherRepository teacherRepository,
                                 CurrentUserService currentUserService,
                                 CourseService courseService) {
        this.teacherCourseRepository = teacherCourseRepository;
        this.teacherRepository = teacherRepository;
        this.currentUserService = currentUserService;
        this.courseService = courseService;
    }

    @Transactional(readOnly = true)
    public List<TeacherCourseResponse> myCourses(Jwt jwt) {
        Teacher teacher = currentUserService.requireTeacher(jwt);
        return teacherCourseRepository.findByTeacher_TeacherId(teacher.getTeacherId())
                .stream().map(this::toResponse).toList();
    }

    /** Admin assigns an existing teacher to an existing course. */
    @Transactional
    public TeacherCourseResponse assignTeacher(Jwt jwt, Long courseId, Long teacherId) {
        currentUserService.requireAdmin(jwt);
        Course course = courseService.findCourseOrThrow(courseId);
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Teacher not found."));

        if (teacherCourseRepository.existsByTeacher_TeacherIdAndCourse_CourseId(teacherId, courseId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Teacher already assigned to this course.");
        }

        TeacherCourse link = new TeacherCourse();
        link.setTeacher(teacher);
        link.setCourse(course);
        link.setIsActive(true);
        link = teacherCourseRepository.save(link);

        return toResponse(link);
    }

    private TeacherCourseResponse toResponse(TeacherCourse link) {
        return new TeacherCourseResponse(
                link.getTeacherCourseId(),
                link.getCourse().getCourseId(),
                link.getCourse().getTitle(),
                link.getTeacher().getTeacherId(),
                link.getTeacher().getUser().getPerson().getFirstName() + " "
                        + link.getTeacher().getUser().getPerson().getLastName(),
                link.getIsActive()
        );
    }
}

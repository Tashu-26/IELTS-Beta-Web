package com.ieltsbeta.service;

import com.ieltsbeta.dto.LiveClassCreateRequest;
import com.ieltsbeta.dto.LiveClassResponse;
import com.ieltsbeta.entity.LiveClass;
import com.ieltsbeta.entity.Teacher;
import com.ieltsbeta.entity.TeacherCourse;
import com.ieltsbeta.repository.LiveClassRepository;
import com.ieltsbeta.repository.TeacherCourseRepository;
import com.ieltsbeta.security.CurrentUserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class LiveClassService {

    private final LiveClassRepository liveClassRepository;
    private final TeacherCourseRepository teacherCourseRepository;
    private final CurrentUserService currentUserService;

    public LiveClassService(LiveClassRepository liveClassRepository,
                             TeacherCourseRepository teacherCourseRepository,
                             CurrentUserService currentUserService) {
        this.liveClassRepository = liveClassRepository;
        this.teacherCourseRepository = teacherCourseRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional(readOnly = true)
    public List<LiveClassResponse> listForCourse(Long courseId) {
        return liveClassRepository.findByTeacherCourse_Course_CourseId(courseId)
                .stream().map(this::toResponse).toList();
    }

    /** Only the teacher assigned to teacherCourseId (or an Admin) may schedule a class on it. */
    @Transactional
    public LiveClassResponse create(Jwt jwt, Long teacherCourseId, LiveClassCreateRequest request) {
        TeacherCourse teacherCourse = teacherCourseRepository.findById(teacherCourseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Teacher-course assignment not found."));

        boolean isAdmin = tryIsAdmin(jwt);
        if (!isAdmin) {
            Teacher teacher = currentUserService.requireTeacher(jwt);
            if (!teacher.getTeacherId().equals(teacherCourse.getTeacher().getTeacherId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This is not your course assignment.");
            }
        }

        LiveClass liveClass = new LiveClass();
        liveClass.setTeacherCourse(teacherCourse);
        liveClass.setMeetingLink(request.meetingLink());
        liveClass.setClassDate(request.classDate());
        liveClass = liveClassRepository.save(liveClass);

        return toResponse(liveClass);
    }

    private boolean tryIsAdmin(Jwt jwt) {
        try {
            currentUserService.requireAdmin(jwt);
            return true;
        } catch (ResponseStatusException e) {
            return false;
        }
    }

    private LiveClassResponse toResponse(LiveClass liveClass) {
        return new LiveClassResponse(
                liveClass.getClassId(),
                liveClass.getTeacherCourse().getCourse().getCourseId(),
                liveClass.getTeacherCourse().getCourse().getTitle(),
                liveClass.getMeetingLink(),
                liveClass.getClassDate()
        );
    }
}

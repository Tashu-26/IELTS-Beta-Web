package com.ieltsbeta.service;

import com.ieltsbeta.dto.ContentCreateRequest;
import com.ieltsbeta.dto.ContentResponse;
import com.ieltsbeta.entity.Content;
import com.ieltsbeta.entity.Course;
import com.ieltsbeta.repository.ContentRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ContentService {

    private final ContentRepository contentRepository;
    private final CourseService courseService;

    public ContentService(ContentRepository contentRepository, CourseService courseService) {
        this.contentRepository = contentRepository;
        this.courseService = courseService;
    }

    @Transactional(readOnly = true)
    public List<ContentResponse> listForCourse(Long courseId) {
        return contentRepository.findByCourse_CourseId(courseId).stream().map(this::toResponse).toList();
    }

    @Transactional
    public ContentResponse create(Jwt jwt, Long courseId, ContentCreateRequest request) {
        Course course = courseService.findCourseOrThrow(courseId);
        courseService.requireOwnerOrAdmin(jwt, courseId);

        Content content = new Content();
        content.setCourse(course);
        content.setTitle(request.title());
        content.setContentType(request.contentType());
        content.setYoutubeLink(request.youtubeLink());
        content.setFileUrl(request.fileUrl());
        content = contentRepository.save(content);

        return toResponse(content);
    }

    private ContentResponse toResponse(Content content) {
        return new ContentResponse(
                content.getContentId(),
                content.getCourse().getCourseId(),
                content.getTitle(),
                content.getContentType(),
                content.getYoutubeLink(),
                content.getFileUrl()
        );
    }
}

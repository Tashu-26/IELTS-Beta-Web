package com.ieltsbeta.event;

import com.ieltsbeta.entity.Student;

/**
 * OBSERVER PATTERN — published whenever a Student's band scores change
 * (goal update, test submission) so that anything interested in reacting to
 * a "current band reached target band" milestone can do so without the
 * publisher (AuthService, TestAttemptService) depending on GamificationService.
 */
public class BandCheckEvent {

    private final Student student;

    public BandCheckEvent(Student student) {
        this.student = student;
    }

    public Student getStudent() {
        return student;
    }
}

package com.ieltsbeta.event;

/**
 * OBSERVER PATTERN — the "event" published by a Subject (any service that
 * just did something mission-worthy) and consumed by Observers (currently
 * only GamificationService) without either side knowing about the other.
 *
 * Published via Spring's ApplicationEventPublisher (the Subject role) and
 * received by @EventListener methods (the Observer role). See
 * GamificationService#onMissionCompleted for the concrete observer.
 */
public class MissionCompletedEvent {

    private final Long studentId;
    private final String missionKey;

    public MissionCompletedEvent(Long studentId, String missionKey) {
        this.studentId = studentId;
        this.missionKey = missionKey;
    }

    public Long getStudentId() {
        return studentId;
    }

    public String getMissionKey() {
        return missionKey;
    }
}

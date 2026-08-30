package com.ieltsbeta.repository;

import com.ieltsbeta.entity.StudentMission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StudentMissionRepository extends JpaRepository<StudentMission, Long> {
    Optional<StudentMission> findByStudentIdAndMissionKeyAndMissionDate(Long studentId, String missionKey, LocalDate missionDate);
    List<StudentMission> findByStudentIdAndMissionDate(Long studentId, LocalDate missionDate);
}

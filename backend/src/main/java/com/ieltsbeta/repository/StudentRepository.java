package com.ieltsbeta.repository;

import com.ieltsbeta.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByUser_UserId(Long userId);
}

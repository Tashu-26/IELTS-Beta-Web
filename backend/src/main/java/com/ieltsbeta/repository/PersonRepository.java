package com.ieltsbeta.repository;

import com.ieltsbeta.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {
}

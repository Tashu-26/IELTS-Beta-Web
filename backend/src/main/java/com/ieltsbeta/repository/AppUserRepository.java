package com.ieltsbeta.repository;

import com.ieltsbeta.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByAuthUserId(UUID authUserId);

    List<AppUser> findAllByOrderByCreatedAtDesc();
}

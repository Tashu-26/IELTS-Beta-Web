package com.ieltsbeta.controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class DbHealthController {

    private final JdbcTemplate jdbcTemplate;

    public DbHealthController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Phase 3 checkpoint only: proves the Supabase Postgres connection
     * and schema are reachable, before any JPA entities exist (Phase 5).
     */
    @GetMapping("/api/db-health")
    public Map<String, Object> dbHealth() {
        Integer courseCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM courses", Integer.class);
        return Map.of(
                "status", "UP",
                "courseCount", courseCount
        );
    }
}

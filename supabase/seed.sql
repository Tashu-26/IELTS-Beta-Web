-- ============================================================
-- IELTS Beta — Optional seed data
-- Run AFTER schema.sql. Safe to skip entirely.
-- Only seeds courses (public, browsable) — no fake users/students/
-- test-attempts/etc., since those must come from real registered
-- accounts and real activity per the "no fake core functionality" rule.
-- ============================================================

INSERT INTO courses (title, description, level, duration) VALUES
  ('IELTS Academic Foundation', 'Core Listening, Reading, Writing and Speaking skills for Academic IELTS.', 'Beginner', 8),
  ('IELTS Academic Intensive', 'Fast-track preparation for learners targeting Band 7+.', 'Advanced', 6),
  ('IELTS General Training', 'Preparation focused on the General Training module for immigration/work.', 'Intermediate', 6);

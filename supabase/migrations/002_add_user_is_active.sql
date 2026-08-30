-- ============================================================
-- Migration: add account suspension support for the Admin panel (Phase 10)
-- Run this once in the Supabase SQL editor. Safe on an existing database
-- (adds a column with a default; does not touch existing rows' data).
-- ============================================================

ALTER TABLE users
  ADD COLUMN is_active BOOLEAN NOT NULL DEFAULT TRUE;

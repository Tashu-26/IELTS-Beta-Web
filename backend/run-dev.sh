#!/bin/bash
# Sets required env vars and starts the backend in one step.
#
# ⚠️ CONTAINS REAL CREDENTIALS (DB password, JWT secret) — do NOT commit
# this file to git or upload it publicly. It's already excluded via
# .gitignore, but double-check before pushing/submitting if you zip the
# project back up yourself.

export SPRING_DATASOURCE_URL="jdbc:postgresql://aws-0-ap-northeast-1.pooler.supabase.com:5432/postgres?sslmode=require"
export SPRING_DATASOURCE_USERNAME="postgres.fbfrbywgvrbkdymfiwwq"
export SPRING_DATASOURCE_PASSWORD="IELTSBetawevapp"
export SUPABASE_URL="https://fbfrbywgvrbkdymfiwwq.supabase.co"
export SUPABASE_JWT_SECRET="BNnMjf3t1/3kek0IlE3/XNsqHvKfiiT2rE2yKneQpAAPBiOYJBV2XTYqSi7Jb2zbM2/nEhLY6fJWAZNUa/EWag=="

mvn spring-boot:run

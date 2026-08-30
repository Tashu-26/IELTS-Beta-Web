# IELTS Beta

## Project setup (Phase 2)

### Frontend (Next.js)

```
cd frontend
cp .env.local.example .env.local
npm install
npm run dev
```

Open http://localhost:3000 — you should see an "IELTS Beta — Project Setup" card.
It will show **Backend status: DOWN** until you also start the backend below.

### Backend (Spring Boot)

```
cd backend
mvn spring-boot:run
```

Open http://localhost:8080/api/health — you should see:
```json
{"status":"UP","service":"ieltsbeta-backend"}
```

Reload http://localhost:3000 — the badge should now say **UP**.

## Phase 3: Supabase + Database

### 1. Create a Supabase project
1. Go to https://supabase.com/dashboard → **New project**.
2. Pick a name (e.g. `ielts-beta`), a database password (save it — you'll need it below), and a region.
3. Wait for provisioning to finish (~2 min).

### 2. Run the schema
1. In your Supabase project, open **SQL Editor** → **New query**.
2. Paste the full contents of `supabase/schema.sql`, click **Run**.
   You should see 22 `CREATE TABLE` and 15 `CREATE INDEX` confirmations, no errors.
   (This exact file was tested against a real Postgres 16 instance before being handed to you — including confirming the check constraints correctly reject bad data.)
3. Optionally also run `supabase/seed.sql` (adds 3 sample courses so there's something to browse before any teacher has created content).

### 3. Get your credentials
In your Supabase project: **Project Settings**

- **Database** → **Connection string** → **URI** tab. Note the host, e.g. `db.<project-ref>.supabase.co`. Your JDBC URL will be:
  `jdbc:postgresql://db.<project-ref>.supabase.co:5432/postgres?sslmode=require`
- **Database** → the password you set in step 1.
- **API Keys** (Supabase renamed this from "API" — if your project shows a **"Publishable and secret API keys"** tab, use the **Publishable key** (`sb_publishable_...`) for `NEXT_PUBLIC_SUPABASE_ANON_KEY`; if it only shows **legacy keys**, use the `anon` `public` key (`eyJ...`) instead — both work the same way with `@supabase/supabase-js`. Also grab the **Project URL** for `SUPABASE_URL`/`NEXT_PUBLIC_SUPABASE_URL`.
- (Needed later, Phase 4) **JWT Secret**, under JWT Settings — this is a *different* value from the publishable/anon key above, and is only used by the *backend*. Pasting this into the frontend's `NEXT_PUBLIC_SUPABASE_ANON_KEY` by mistake is the most common cause of a Supabase **"Invalid API key"** error on signup/login.

> **Getting "Invalid API key" when registering or logging in?** This means Supabase itself rejected the key `frontend/.env.local` sent it — check, in order: (1) the file still has the literal placeholder text instead of a real key, (2) the key and URL are from the *same* project, (3) you didn't accidentally paste the JWT Secret or secret key instead of the publishable/anon key, (4) no stray quotes/spaces around the value in `.env.local`, (5) you restarted `npm run dev` after editing the file — Next.js only reads `.env.local` at server start.

### 4. Set environment variables

**Backend** — Java does not auto-load `.env` files, so export these in your shell before running (or set them in your IDE's run configuration):

macOS/Linux:
```
export SPRING_DATASOURCE_URL="jdbc:postgresql://db.<project-ref>.supabase.co:5432/postgres?sslmode=require"
export SPRING_DATASOURCE_USERNAME="postgres"
export SPRING_DATASOURCE_PASSWORD="<your-db-password>"
```

Windows (PowerShell):
```
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://db.<project-ref>.supabase.co:5432/postgres?sslmode=require"
$env:SPRING_DATASOURCE_USERNAME="postgres"
$env:SPRING_DATASOURCE_PASSWORD="<your-db-password>"
```

**Frontend** — edit `frontend/.env.local` (auto-loaded by Next.js):
```
NEXT_PUBLIC_SUPABASE_URL=https://<project-ref>.supabase.co
NEXT_PUBLIC_SUPABASE_ANON_KEY=<your-anon-key>
```

### 5. Run and verify

```
cd backend
mvn spring-boot:run
```

Open http://localhost:8080/api/db-health — you should see:
```json
{"status":"UP","courseCount":3}
```
(`courseCount` is `0` if you skipped `seed.sql` — that's fine, it still proves the connection works.)

If you get a connection error, double check the password and that `sslmode=require` is in the URL.

### Notes for this stage

- `backend/src/main/resources/application.yml` now has real datasource config (no more Phase 2 exclusions).
- No JPA `@Entity` classes exist yet — that's Phase 5. `/api/db-health` proves connectivity directly via `JdbcTemplate` in the meantime.
- `ddl-auto` is set to `none` — Hibernate will never auto-create or alter tables. `supabase/schema.sql` is the single source of truth for the database structure.
- CORS is already configured to allow `http://localhost:3000` → `http://localhost:8080`.

## Phase 4: Supabase Auth

### 1. Get your JWT secret
Supabase dashboard → **Project Settings** → **JWT Keys** (or **API** → **JWT Settings** on older dashboards) → copy the **JWT Secret**. This is a *different* value from the publishable/anon key used by the frontend — see the note in the Phase 3 section above about not mixing these up.

### 2. (Recommended for faster local testing) Turn off email confirmation
Supabase dashboard → **Authentication** → **Providers** → **Email** → toggle off **"Confirm email"**.
This isn't required — the app handles either setting correctly — but with it off, `supabase.auth.signUp()` returns a usable session immediately so you can test registration → dashboard in one step instead of confirming via email first.

### 3. Set the new environment variable

Backend (add to your existing exported vars):
```
export SUPABASE_JWT_SECRET="<your-jwt-secret-from-step-1>"
```

Frontend — edit `frontend/.env.local` (should already have `NEXT_PUBLIC_SUPABASE_URL`/`NEXT_PUBLIC_SUPABASE_ANON_KEY` from Phase 3; no new vars needed for Phase 4).

### 4. Run and test

```
cd backend
mvn spring-boot:run
```
```
cd frontend
npm install
npm run dev
```

Open http://localhost:3000:
1. Click **Sign up**, fill in the form, submit.
2. You're redirected home and should see **"Signed in as ... · Role: Student"**.
3. Click **Log out**, then **Log in** with the same credentials — should work.
4. In the Supabase dashboard → **Table Editor** → `person`, `users`, `students` — you should see one real row in each, created by the backend.

If you see "No profile found on the backend yet", check the backend terminal for errors (usually a missing/incorrect `SUPABASE_JWT_SECRET`).

### Notes for this stage
- Self-registration always creates a **Student** account (see Phase 1 decision). Teacher/Admin accounts come in Phase 10 (Admin panel). If you need a Teacher or Admin account to test with before then: register normally as a Student first (so the Supabase Auth user + `person`/`users` rows exist), then in the Supabase SQL editor run:
  ```sql
  UPDATE users SET role = 'Teacher' WHERE email = 'you@example.com';
  INSERT INTO teachers (user_id) SELECT user_id FROM users WHERE email = 'you@example.com';
  ```
  (swap `'Teacher'`/`teachers` for `'Admin'`/`admins` as needed). Log out and back in afterward so the backend re-reads the role.
- The backend never trusts a role from the frontend: `SupabaseJwtAuthenticationConverter` looks up the real role from the `users` table on every request.
- `/api/health` and `/api/db-health` remain public; every other endpoint now requires a valid Supabase JWT.

## Phase 5: Spring Boot backend (courses, enrollment, content, live classes, support, announcements, practice test authoring)

No new environment variables or setup steps — just restart the backend:
```
cd backend
mvn spring-boot:run
```

### What's new
CRUD APIs (with role-based authorization, enforced server-side) for:
- **Courses** — `GET/POST /api/courses`, `PUT /api/courses/{id}`
- **Enrollment** — `POST /api/enrollments`, `GET /api/students/me/enrollments`
- **Content** — `GET/POST /api/courses/{courseId}/contents`
- **Teacher-course assignment** — `GET /api/teachers/me/courses`, `POST /api/courses/{courseId}/teachers/{teacherId}` (Admin)
- **Live classes** — `GET /api/courses/{courseId}/live-classes`, `POST /api/teacher-courses/{id}/live-classes`
- **Support tickets** — `POST /api/support-tickets`, `GET /api/support-tickets/mine`, `GET /api/support-tickets` (Admin), `PUT /api/support-tickets/{id}` (Admin)
- **Announcements** — `GET/POST /api/announcements`
- **Admin logs** — `GET /api/admin/logs` (auto-recorded when an Admin posts an announcement, as a working example)
- **Practice test authoring** — `GET/POST /api/practice-tests`, `GET /api/practice-tests/{id}`, `POST /api/practice-tests/{id}/questions`, `POST /api/questions/{id}/answer-options` (answer correctness is hidden from Students, visible to the owning Teacher/Admin)

**Not in this phase** (deliberately deferred to Phase 11 per the Build Order — taking a test, scoring, results, progress dashboards): `test_attempts`, `test_results`.

### Quick test (needs a real Supabase access token)
Get a token: log in via the frontend, then in the browser console run `(await window.supabase?.auth.getSession())` — or simpler, open dev tools → Application → Local Storage → find the `sb-...-auth-token` entry → copy the `access_token` value.

```bash
TOKEN="paste-your-access-token-here"

# List courses (works even with the seed data from Phase 3)
curl http://localhost:8080/api/courses

# Create a course (needs a Teacher or Admin token)
curl -X POST http://localhost:8080/api/courses \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"title":"IELTS Speaking Bootcamp","level":"Intermediate","duration":4}'

# Enroll as a Student
curl -X POST http://localhost:8080/api/enrollments \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"courseId":1}'

# View your enrollments
curl http://localhost:8080/api/students/me/enrollments -H "Authorization: Bearer $TOKEN"
```

If a Student token tries `POST /api/courses`, you should get a `403` — confirming role enforcement works.

## Phase 6: Next.js UI (public marketing pages)

No new setup — just:
```
cd frontend && npm install && npm run dev
```

### What's new
- `Navbar`/`Footer`/`ThemeToggle` shared components, using the design tokens ported from your prototype in Phase 2 (colors, fonts, radii, shadows — including working dark mode).
- Real **Home** (`/`), **Study IELTS** (`/study`), and **Pricing** (`/pricing`) pages, replacing the Phase 4 dev-checkpoint homepage.
- Navbar is auth-aware: shows Log in/Get Started when signed out, or your name + role + Log out when signed in (pulls from the real `AuthContext` built in Phase 4).

### One adaptation from the prototype, worth knowing about
The static prototype made **Practice**, **Mock Test**, and **Teacher Mode** publicly clickable from the navbar (it had to — it's a fully static demo with no login gate). In the real app those experiences live behind authentication inside the Student/Teacher/Admin portals, so I removed those links from the public navbar rather than pointing them at pages that don't exist yet or wrongly leaving practice content ungated. They'll appear once Phase 8 (Student portal) builds the real, authenticated versions.

### Try it
Visit `/`, `/study`, `/pricing` — should look and feel like the prototype's palette/typography. Log in from the navbar and confirm it switches to your name + role instead of the Login/Get Started buttons.

## Phase 8: Student portal (real data, no mocks)

New backend endpoint: `PUT /api/students/me/goal` (updates target/current band). Restart the backend to pick it up.

```
cd backend && mvn spring-boot:run
cd frontend && npm install && npm run dev
```

### What's new
Protected routes under `/student/*` (redirects to `/login` if signed out, or `/` if signed in as a non-Student — `AuthGuard` from Phase 4 finally gets used):
- **`/student/dashboard`** — real target/current band from your profile, real enrolled courses
- **`/student/courses`** — browse all courses, enroll (writes a real `enrollments` row)
- **`/student/courses/[id]`** — real contents, practice tests, and live classes for that course
- **`/student/goals`** — set target/current band, persists via the new endpoint
- **`/student/support`** — submit a ticket, see your own ticket history
- **`/student/profile`** — read-only account info

Every number on these pages comes from Postgres through the Phase 5 APIs — nothing hardcoded. If you see "0.5 gap to target" or an empty courses list, that's genuinely because that's what's in your database right now.

**Deliberately not built yet:** taking a practice test and getting scored (the course page lists tests but says "coming soon" — that's the real `test_attempts`/`test_results` engine, which is Phase 11 per your Build Order) and Writing/Speaking submissions (Phase 12).

### Try it
Log in as a Student, go to `/student/courses`, enroll in one of the seeded courses, then check `/student/dashboard` — it should now show that course. Set a goal on `/student/goals` and confirm it persists after a page refresh (i.e. it's really in Postgres, not just local state).

## Phase 9: Teacher portal

New backend endpoint: `GET /api/courses/{courseId}/enrollments` (Teacher who owns the course, or Admin, sees who's enrolled). Restart the backend to pick it up.

```
cd backend && mvn spring-boot:run
cd frontend && npm install && npm run dev
```

You'll need a Teacher account — see the "promote to Teacher via SQL" instructions in the Phase 4 section above if you haven't made one yet.

### What's new
Protected routes under `/teacher/*`:
- **`/teacher/dashboard`** — your assigned courses, create a new course (auto-assigns you to it)
- **`/teacher/courses/[id]`** — tabbed course management: add learning materials, create practice tests, schedule live classes, view your enrolled students (roster)
- **`/teacher/courses/[id]/tests/[testId]`** — add questions to a test, add answer options per question (mark the correct one)
- **`/teacher/profile`** — read-only account info

**Deliberately not built yet:** grading Writing/Speaking submissions — that's the `submissions` table work from Phase 12.

### Try it
Log in as a Teacher, create a course, add a content item, create a practice test, add a question with 2–3 answer options (mark one correct), and schedule a live class. Then log in as a Student, enroll in that course, and confirm you can see everything the teacher just added on the course detail page.

## Phase 10: Admin panel

New backend endpoints (restart the backend): `GET /api/admin/users`, `PUT /api/admin/users/{userId}/role`, `GET /api/admin/reports`.

```
cd backend && mvn spring-boot:run
cd frontend && npm install && npm run dev
```

### What's new
Protected routes under `/admin/*`:
- **`/admin/dashboard`** — real platform counts (users, students, teachers, courses, enrollments, open tickets)
- **`/admin/users`** — list every user, **change roles from the UI** — this replaces the manual-SQL workaround from Phase 4! Promote a Student to Teacher or Admin with a dropdown; it creates the corresponding `teachers`/`admins` row automatically.
- **`/admin/courses`** — view/create any course platform-wide
- **`/admin/support`** — view all tickets, change status (auto-assigns the ticket to you)
- **`/admin/announcements`** — post and view announcements
- **`/admin/logs`** — real audit trail (currently logs role changes and announcement posts — see the note in Phase 5 about this being intentionally lightweight, not exhaustively instrumented)
- **`/admin/profile`** — read-only account info

One behavior worth knowing: demoting a user's role does **not** delete their old role-specific row (e.g. a demoted Teacher's `teachers` row and courses stay intact) — this avoids silently destroying data, and re-promoting them later just reuses the existing row.

### Try it
Log in as an Admin, go to `/admin/users`, and promote a Student to Teacher — then check `/admin/logs` to see it recorded. Log in as that user afterward (they'll need to log out/in for the role change to take effect) and confirm they land in the Teacher portal.

---

## Phase 11: Practice-test attempts, scoring, results & progress

New backend endpoints (restart the backend): `POST /api/test-attempts`, `PUT /api/test-attempts/{id}/submit`, `GET /api/test-attempts/{id}`, `GET /api/students/me/test-attempts`, `GET /api/students/me/progress`.

```
cd backend && mvn spring-boot:run
cd frontend && npm install && npm run dev
```

### What's new — this is the real thing now, not a placeholder
- On a course page, **"Start"** next to a practice test now actually works: it creates a real `test_attempts` row, takes you through the questions, and **submitting genuinely scores your answers** against the correct options a teacher set up in Phase 9.
- **`/student/progress`** shows your real target/current band, per-skill averages computed from your actual graded attempts, and your test history — no more static numbers.

### Important simplifications (documented in code, repeating here so it's visible)
- **Only MCQ-style questions get auto-scored.** A question only counts if a teacher marked one of its answer options correct. Writing/Speaking questions are silently skipped by the scorer — real assessment for those skills is Phase 12 (submissions + teacher grading), not this engine. If you want to see a fully-scored test, make sure the teacher-authored practice test uses Listening/Reading-style questions with a marked correct answer.
- **Band score is a simplified percentage → band mapping** (see the javadoc on `TestAttemptService`), not the official IELTS conversion table — there isn't a standardized public one to encode, so this is a transparent, explainable approximation appropriate for a self-practice tool.
- **`Student.currentBand` updates to whatever band you most recently scored.** Real IELTS combines all 4 skills into one band; here each practice test updates the single stored value to its own result. This is called out in the Progress page copy too.

### Try it
As a Teacher: create a practice test on one of your courses, add 3–4 questions with 3–4 answer options each, marking one correct per question. As a Student enrolled in that course: go to the course page, click **Start** on the test, answer the questions, submit, and you should see a real computed band score and feedback. Then check `/student/progress` — it should reflect that attempt.

---

## Phase 12: Writing/Speaking submissions (with Supabase Storage)

### One-time Supabase setup (new)
This is the only feature in the whole project that needs Supabase Storage. In the Supabase SQL Editor, run `supabase/storage-setup.sql` — it creates a `speaking-recordings` bucket and the two policies needed for upload/playback. (I couldn't test this file against a real Supabase project the way I tested `schema.sql` — this sandbox only has vanilla Postgres, not Supabase's storage extension — so it's based on documented Supabase patterns rather than something I actually ran. Let me know if it errors.)

New backend endpoints (restart the backend): `POST /api/submissions`, `GET /api/students/me/submissions`, `GET /api/teachers/me/submissions`, `GET /api/submissions` (Admin), `PUT /api/submissions/{id}/grade`.

```
cd backend && mvn spring-boot:run
cd frontend && npm install && npm run dev
```

### What's new
- **Student**: on any course page, a real "Submit Writing or Speaking work" form. Writing submissions store your essay text directly in Postgres. Speaking submissions upload the audio file straight to Supabase Storage from the browser (using your session, no backend involvement in the file transfer itself), then send the resulting URL to the backend. **`/student/submissions`** shows the status, and once graded, the real band score and feedback your teacher wrote.
- **Teacher**: **`/teacher/submissions`** — a real grading queue split into Pending/Graded, showing the essay text or an audio player, with a band score + feedback form that writes back to Postgres.

### Try it
As a Student, go to a course you're enrolled in, submit a short Writing response. Log in as that course's Teacher, go to `/teacher/submissions`, grade it with a band and feedback. Log back in as the Student and check `/student/submissions` — you should see the real grade and feedback text you just wrote as the teacher.

---

## Phase 13: Gamification

New backend endpoint (restart the backend): `GET /api/students/me/gamification`.

```
cd backend && mvn spring-boot:run
cd frontend && npm install && npm run dev
```

### What's new — and how it differs from the prototype on purpose
The original static prototype had clickable mission checkboxes you could tick yourself. That's not how this works here: **every mission completes automatically from a real action**, never a manual toggle —

| Mission | Completes when you... |
|---|---|
| Complete a practice test today | Submit a scored test attempt (Phase 11) |
| Submit Writing or Speaking work | Create a submission (Phase 12) |
| Check your progress page | Load `/student/progress` |
| Set or update your goal | Save on `/student/goals` |

- **`/student/rewards`** — real XP, coins, streak, and level, computed from your actual activity. Achievements (first test, 5 tests, first submission, 3/7-day streaks, reaching your target band) unlock automatically when the real underlying condition is met — there's no "unlock" button anywhere.
- The Student dashboard now shows a small streak/XP chip linking to Rewards.
- Mission and achievement *definitions* (the 4 missions, 6 achievements) are hardcoded constants in `GamificationCatalog.java`, not database rows — only your *progress* against them is stored (`student_gamification`, `student_missions`, `student_achievements`), per the Phase 1/3 decision to keep this minimal.

### Try it
As a Student: update your goal on `/student/goals`, then check `/student/rewards` — the "Set or update your goal" mission should show as done and your XP/coins should have gone up. Take a practice test and check again — streak should be 1 (or incremented, if you were already active yesterday) and the "first_test" achievement should be unlocked.

---

## Phase 14: Final testing & bug-fixing

This was a whole-application audit, not a new feature. What I actually did and found:

### A real bug found and fixed
The Admin panel's "Suspend/Reactivate user" feature (`/admin/users`) was fully wired end-to-end — frontend button, request payload, backend controller, service logic, even correctly integrated into `SupabaseJwtAuthenticationConverter` so a suspended user's JWT authenticates but carries no role authorities — **except the database was missing the `is_active` column it depends on.** Every operation touching the `users` table would have failed at runtime. Fixed by adding `is_active BOOLEAN NOT NULL DEFAULT TRUE` to `users` in `schema.sql`, then **re-ran the entire Phase 3 validation suite against the current file** (not just the new column) against a real local Postgres instance to confirm nothing else had drifted. All 22 tables, all indexes, and both constraint-rejection tests still pass.

**If you already ran `schema.sql` in an earlier phase**, run this migration in the Supabase SQL editor to pick up the fix:
```sql
ALTER TABLE users ADD COLUMN IF NOT EXISTS is_active BOOLEAN NOT NULL DEFAULT TRUE;
```

### Full endpoint audit
Extracted every API path called from the frontend and every endpoint exposed by the backend, then diffed them systematically: **zero mismatches**. Every frontend call has a real backing endpoint. The backend endpoints not called by any UI are all expected: `/api/health` and `/api/db-health` (dev diagnostics), `/api/students/me/test-attempts` and `/api/test-attempts/{id}` (superseded by `/api/students/me/progress`, which already embeds recent attempts), and `/api/auth/complete-profile`/`/api/me` (called via raw `fetch` in `AuthContext`, not the `apiFetch` helper — verified separately).

### One known gap (documented, not fixed — out of scope for a bug-fixing pass)
`POST /api/courses/{courseId}/teachers/{teacherId}` (assign an additional teacher to an existing course) has no admin UI. Courses get a teacher automatically when a Teacher creates them; there's just no way to *add* a second teacher to an existing course from the UI yet. The endpoint itself works if called directly.

### Full guard sweep
Every protected route in the app — all 18 static + 4 dynamic routes across Student/Teacher/Admin — was checked in one pass (not spot-checked): each one correctly shows the auth-guard state for an unauthenticated request. All 5 public routes serve normally.

### Housekeeping
- Searched the entire codebase for `TODO`/`FIXME`/`console.log`/`System.out.println`/stack-trace-printing: none found.
- Confirmed no real `.env` files exist anywhere and both `.env.example` files contain only placeholders.
- Added `.gitignore` files (root, frontend, backend) — these didn't exist before, a real gap for anyone putting this under version control.

### Full clean builds
Both done from a completely clean `node_modules`/`.next` state as the final check: frontend compiles and type-checks with zero errors (27 routes). The backend still can't be compiled in this sandbox (no JDK compiler, no Maven Central access) — final backend confidence rests on the manual audits above rather than a build, which I want to be upfront about rather than overstate.

---

## Quick start (from scratch)

1. **Supabase**: create a project, run `supabase/schema.sql` then `supabase/seed.sql` in the SQL editor, run `supabase/storage-setup.sql` for Speaking uploads, get your API URL/anon key/JWT secret.
2. **Backend**: `cd backend`, set `SPRING_DATASOURCE_URL`/`USERNAME`/`PASSWORD` and `SUPABASE_JWT_SECRET` as environment variables, `mvn spring-boot:run`.
3. **Frontend**: `cd frontend`, copy `.env.local.example` to `.env.local` and fill in Supabase URL/anon key, `npm install && npm run dev`.
4. Register a Student account, then promote yourself to Teacher/Admin via SQL (see the Phase 4 section) to explore those portals, or use `/admin/users` once you have one Admin account.

This completes all 14 phases.

## Phase 10: Admin panel

**Database migration required** (your Supabase database already exists from Phase 3, so `schema.sql` won't re-run automatically). In the Supabase SQL editor, run:
```sql
ALTER TABLE users ADD COLUMN is_active BOOLEAN NOT NULL DEFAULT TRUE;
```
(same file: `supabase/migrations/002_add_user_is_active.sql`)

```
cd backend && mvn spring-boot:run
cd frontend && npm install && npm run dev
```

You'll need an Admin account — see the "promote via SQL" instructions in the Phase 4 section (swap `'Teacher'`/`teachers` for `'Admin'`/`admins`).

### What's new
Protected routes under `/admin/*`:
- **`/admin/dashboard`** — real aggregate counts (students, teachers, admins, courses, enrollments, open tickets) via a new `GET /api/admin/reports/summary`
- **`/admin/users`** — every registered account, with a working **Suspend/Reactivate** action (new `PUT /api/admin/users/{id}/status`)
- **`/admin/courses`** — all courses, create new ones, assign a teacher to any course
- **`/admin/support`** — every support ticket, change status (Open/In Progress/Resolved)
- **`/admin/announcements`** — post and view announcements
- **`/admin/logs`** — activity log (auto-recorded when an admin suspends/reactivates a user or posts an announcement)
- **`/admin/profile`** — read-only account info

**Suspension is actually enforced, not cosmetic:** `CurrentUserService` (the helper nearly every backend service calls to identify "who's making this request") now checks `is_active` and returns `403 Forbidden` for suspended accounts — so a suspended Teacher genuinely can't create courses, a suspended Student genuinely can't enroll, etc. Try it: suspend a test account from `/admin/users`, then try to use that account — it should get locked out of anything requiring their identity.

### Try it
Log in as Admin, check `/admin/dashboard` for real counts, assign a teacher to a course on `/admin/courses`, resolve a ticket on `/admin/support`, and suspend/reactivate a user on `/admin/users` — then confirm the action shows up on `/admin/logs`.

## Phase 9: Teacher portal (real data, no mocks)

New backend endpoint: `GET /api/courses/{courseId}/enrollments` (Teacher who owns the course, or Admin, sees who's enrolled). Restart the backend to pick it up.

```
cd backend && mvn spring-boot:run
cd frontend && npm install && npm run dev
```

You'll need a Teacher account to test this — see the "promote a Student to Teacher via SQL" instructions in the Phase 4 section above if you haven't done that yet.

### What's new
Protected routes under `/teacher-portal/*` (Teacher role only):
- **`/teacher-portal/dashboard`** — real count of courses you teach
- **`/teacher-portal/courses`** — your courses, plus a form to create a new one (creating a course auto-assigns you to it)
- **`/teacher-portal/courses/[id]`** — tabbed course management: add materials, create practice tests, schedule live classes, and see the real list of enrolled students (name, status, enrollment date — from the new endpoint above)
- **`/teacher-portal/tests/[id]`** — add questions to a practice test, add answer options per question, mark the correct one(s)

Navbar now shows a "Teacher Portal" link when you're signed in as a Teacher, alongside the Student "Dashboard" link.

**Still deferred to Phase 11/12:** grading student submissions and viewing test-attempt results — those need `test_attempts`/`test_results`/`submissions`, which don't exist as UI yet.

### Try it
Promote your test account to Teacher, log in, create a course from `/teacher-portal/courses`, add a practice test with a question and two answer options (mark one correct), then switch to a Student account and enroll in that course — you should see the material, test, and (once scheduled) live class show up on the student's course page.

## Phase 7+8: Connect frontend to backend + Student portal

I merged these two since there was little value "connecting" a frontend before real pages existed to connect. This phase adds one small backend endpoint (`PUT /api/students/me/goal`) plus the full Student portal, all wired to real APIs — no mock data anywhere.

No new setup — just restart both servers.

### What's new
- **`/student/dashboard`** — real target/current band (from your profile), real enrolled-course count, real announcements.
- **`/student/courses`** — real `GET /api/courses`, enroll with real `POST /api/enrollments`, shows accurate "✓ Enrolled" state.
- **`/student/practice`** — real practice tests aggregated across your enrolled courses.
- **`/student/practice/[testId]`** — real questions and answer options for that test, **read-only**. Taking a timed attempt and getting it scored is Phase 11 (`test_attempts`/`test_results` — deliberately not built yet per the Build Order); this page previews real content honestly rather than faking a "Start Test" button that goes nowhere.
- **`/student/goals`** — real `PUT /api/students/me/goal`, refreshes your profile everywhere (navbar, dashboard) immediately after saving.
- **`/student/support`** — real ticket creation + your ticket history.

### What's deliberately not on the dashboard yet
No streak, XP, or per-skill band chart — those need `test_results` (Phase 11) and gamification (Phase 13), which don't exist yet. Rather than hardcode believable-looking numbers, the dashboard shows a "set your goals" prompt when bands aren't set, and otherwise only shows what's real today.

### Try it
1. Log in as a Student (or register a new one).
2. Click **Dashboard** in the navbar → lands on `/student/dashboard`.
3. Go to **Courses**, enroll in one of the seeded courses → see it show up on the dashboard.
4. Go to **Practice** → click into a test → see real questions (empty if you haven't added any via the API yet — see the `curl` examples in Phase 5 to add some).
5. Go to **Goals**, set a target/current band → confirm it updates on the dashboard and in the navbar's account chip after refresh.
6. Go to **Support**, submit a ticket → see it appear below.

## Course deliverable: Design Patterns

Three design patterns are implemented in the backend. All three were introduced by refactoring code that already existed (not bolted on separately), so they reflect real seams in the app rather than textbook demos.

### 1. Strategy Pattern — band-score calculation

**Problem it solves:** `TestAttemptService` needs to convert "percentage of marks correct" into an IELTS-style band score. That conversion is a business rule that is likely to change (a stricter curve for one test category, a real licensed IELTS table later, etc.). Hard-coding the formula inside the 250-line `TestAttemptService` would mean touching core scoring/attempt-workflow code every time the marking scheme changes, and would make the formula impossible to unit-test without the rest of the service.

**Files/classes involved:**
- `service/scoring/BandScoringStrategy.java` — the Strategy interface (`toBand(double percentage)`)
- `service/scoring/PercentageBandScoringStrategy.java` — the concrete Strategy (the fixed percentage→band lookup table), registered as a Spring `@Component`
- `service/TestAttemptService.java` — the Context; holds a `BandScoringStrategy` field injected via constructor and calls `bandScoringStrategy.toBand(...)` wherever a band needs computing (overall score and per-skill scores)

**UML:**
```
 ┌────────────────────────┐        uses        ┌───────────────────────┐
 │   TestAttemptService    │ ──────────────────▶│  «interface»          │
 │   (Context)             │                     │  BandScoringStrategy  │
 │ - bandScoringStrategy   │                     │ + toBand(double): BD  │
 │ + submitAttempt(...)    │                     └───────────▲───────────┘
 └────────────────────────┘                                  │
                                                               │ implements
                                              ┌────────────────┴───────────────┐
                                              │ PercentageBandScoringStrategy   │
                                              │ + toBand(double): BigDecimal    │
                                              └─────────────────────────────────┘
```
The Context (`TestAttemptService`) never knows *which* concrete strategy it holds — Spring injects `PercentageBandScoringStrategy` today, but swapping in a different marking scheme means adding a new class implementing `BandScoringStrategy` and wiring it in; `TestAttemptService` never changes.

### 2. Observer Pattern — gamification events

**Problem it solves:** Three unrelated services (`AuthService` on goal updates, `SubmissionService` on Writing/Speaking submission, `TestAttemptService` on test completion/progress checks) all need to "notify gamification" that something mission-worthy happened. Originally they each held a direct `GamificationService` field and called it inline — meaning every one of those services was coupled to gamification's existence, and adding a fourth interested party (e.g. a future analytics service) would mean editing every publisher again.

**Files/classes involved:**
- `event/MissionCompletedEvent.java`, `event/BandCheckEvent.java` — the Event objects (the "notification")
- `AuthService`, `SubmissionService`, `TestAttemptService` — the Subjects; each holds a Spring `ApplicationEventPublisher` and calls `publishEvent(...)` instead of calling `GamificationService` directly
- `GamificationService` — the concrete Observer; its `onMissionCompleted(MissionCompletedEvent)` and `onBandCheck(BandCheckEvent)` methods are annotated `@EventListener`, so Spring's `ApplicationContext` (acting as the Subject's notification channel) invokes them automatically whenever an event is published, with zero coupling from the publisher's side

**UML:**
```
 ┌────────────────┐  publishEvent(MissionCompletedEvent)   ┌───────────────────────┐
 │  AuthService     │ ──────────────────────────────────▶  │ ApplicationEventPublisher│
 │  SubmissionService│                                       │      (Subject)          │
 │  TestAttemptService│◀── publishEvent(BandCheckEvent) ────│                          │
 └────────────────┘                                        └───────────┬─────────────┘
                                                                          │ notifies
                                                                          ▼
                                                            ┌───────────────────────────┐
                                                            │   GamificationService      │
                                                            │   (Concrete Observer)      │
                                                            │ @EventListener             │
                                                            │ onMissionCompleted(event)  │
                                                            │ onBandCheck(event)         │
                                                            └───────────────────────────┘
```
Publishers never reference `GamificationService` at all anymore — they only depend on Spring's `ApplicationEventPublisher` (a generic interface) and the event classes. This is Observer implemented the idiomatic Spring way: `ApplicationEventPublisher`/`ApplicationContext` plays the role of the Subject's notification mechanism, and any number of `@EventListener` methods can subscribe without the publisher knowing they exist.

### 3. Facade Pattern — `CurrentUserService`

**Problem it solves:** Almost every controller/service needs to answer "who is calling this, and are they allowed to?" — which requires: parsing the Supabase JWT's subject claim, looking up the `users` row, checking `is_active` (suspended accounts must be authenticated-but-role-less), and then looking up the role-specific row (`students`/`teachers`/`admins`). Doing this in every one of the 20 controllers/15 services would mean ~4 repository dependencies and the same suspension-check logic duplicated everywhere.

**Files/classes involved:**
- `security/CurrentUserService.java` — the Facade; wraps `AppUserRepository`, `StudentRepository`, `TeacherRepository`, `AdminRepository` behind three simple calls
- Every service that needs the caller's identity (`AuthService`, `TestAttemptService`, `SubmissionService`, `AdminUserService`, `GamificationService`, etc.) depends only on this one Facade class, never on the four repositories directly for identity resolution

**UML:**
```
                       ┌───────────────────────────────┐
  Controllers/Services │       CurrentUserService        │
  ───────────────────▶ │           (Facade)              │
                       │ + requireUser(jwt): AppUser     │
                       │ + requireStudent(jwt): Student  │
                       │ + requireTeacher(jwt): Teacher  │
                       │ + requireAdmin(jwt): Admin      │
                       └───────┬───────┬───────┬───────┬─┘
                               │       │       │       │
                     ┌─────────┘  ┌────┘  ┌────┘  ┌────┘
                     ▼            ▼       ▼        ▼
             AppUserRepository StudentRepo TeacherRepo AdminRepo
```
Callers get a one-line answer ("give me the Student for this request, or throw a 403") instead of coordinating four repositories and re-implementing the suspension check themselves.

## Course deliverable: Testing (JUnit 5 + Mockito)

**Framework:** JUnit 5 (Jupiter) + Mockito, both already pulled in transitively by `spring-boot-starter-test` in `pom.xml` — no extra dependencies needed. Coverage is measured with the `jacoco-maven-plugin` (added to `pom.xml`), which also enforces a 50% minimum line-coverage gate on `mvn verify` (DTOs/entities/config classes are excluded from the ratio since they're pure boilerplate with no logic to test).

**Approach:** every test is a plain unit test — no Spring context is started (no `@SpringBootTest`), which keeps the whole suite running in a couple of seconds. Each service's dependencies (repositories, `CurrentUserService`, `ApplicationEventPublisher`, `BandScoringStrategy`) are replaced with Mockito mocks (`@Mock` + `@ExtendWith(MockitoExtension.class)`), so no real database or Supabase connection is touched — this **is** the "mocking/stubbing to isolate the unit of work from external dependencies" requirement.

**Test files (`backend/src/test/java/com/ieltsbeta/...`):**
| Test class | What it covers |
|---|---|
| `service/scoring/PercentageBandScoringStrategyTest` | Every branch of the Strategy pattern's band lookup table (parameterized over all thresholds) |
| `security/CurrentUserServiceTest` | The Facade's happy paths and every failure branch (no profile, suspended, wrong role) |
| `service/AuthServiceTest` | Profile creation/idempotency, `/api/me`, goal updates (asserts both events are published — Observer pattern), the new profile-settings update |
| `service/TestAttemptServiceTest` | The core scoring engine: correct/incorrect answers, forbidden access, already-submitted conflict, not-found, and asserts the service delegates to the mocked `BandScoringStrategy` and publishes events instead of calling `GamificationService` directly |
| `service/GamificationServiceTest` | The Observer's reaction logic, called directly via its `@EventListener` methods: XP/mission awarding, idempotency (no double-award same day), achievement unlocking |
| `service/AdminUserServiceTest` | Role promotion (with/without duplicate role-row creation), invalid role rejection, suspend/reactivate, self-suspension guard |

**How to run:**
```
cd backend
mvn test                 # runs all tests
mvn verify                # runs tests + generates + enforces the JaCoCo coverage report
```
After `mvn verify`, open `backend/target/site/jacoco/index.html` in a browser for the human-readable coverage report.

## Bugs fixed in this pass

1. **Admin/Teacher accounts unreachable.** Self-registration always creates a Student (by design — see Phase 4 notes above), and promoting someone to Teacher/Admin requires an existing Admin account via `PUT /api/admin/users/{id}/role`. If you've only ever used the app's own signup, there is no Admin yet, so nothing can be promoted. `supabase/bootstrap_admin.sql` is a ready-to-run script for this one-time bootstrap step (register as Student first with the email you want as Admin, then run the script). After that, use the in-app Admin Users page for every future promotion — never touch SQL again.
2. **Student "profile settings" did nothing.** The profile page only ever displayed data — there was no backend endpoint or UI form to actually change it. Added `PUT /api/me` (`AuthService.updateProfile`, works for any role since the name lives on the shared `Person` row) and turned `student/profile/page.tsx` into a real edit form.

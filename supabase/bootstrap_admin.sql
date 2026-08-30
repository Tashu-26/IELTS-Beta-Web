-- ============================================================
-- Bootstrap the FIRST Admin account.
--
-- WHY THIS IS NEEDED:
-- Self-registration (POST /api/auth/complete-profile, called
-- automatically by the frontend on first login) always creates a
-- Student. That is by design -- Teacher/Admin accounts are meant to
-- be *promoted* by an existing Admin via
-- PUT /api/admin/users/{userId}/role. But that endpoint itself
-- requires you to already be an Admin, so the very first Admin can't
-- be created through the app at all. Without running this script once,
-- there is no Admin account, nothing can promote anyone to Teacher
-- either, and the Admin/Teacher parts of the app have no account that
-- can reach them (every login just becomes another Student).
--
-- HOW TO USE:
-- 1. Sign up normally through the app's own register page with the
--    email you want to use as the admin (e.g. admin@ieltsbeta.test).
--    This creates the person/users/students rows automatically.
-- 2. Run this script in the Supabase SQL editor, with that email
--    substituted below.
-- 3. Log out and back in (or just refresh) -- GET /api/me now returns
--    role "Admin", and /admin/* pages become reachable.
-- 4. From then on, use the Admin Users page (PUT /api/admin/users/
--    {userId}/role) to promote any other account to Teacher or Admin.
--    You never need to run this script again.
-- ============================================================

DO $$
DECLARE
  target_email TEXT := 'admin@ieltsbeta.test';  -- <-- change this
  target_user_id BIGINT;
BEGIN
  SELECT user_id INTO target_user_id FROM users WHERE email = target_email;

  IF target_user_id IS NULL THEN
    RAISE EXCEPTION 'No user with email % yet. Sign up through the app first, then re-run this script.', target_email;
  END IF;

  UPDATE users SET role = 'Admin' WHERE user_id = target_user_id;

  INSERT INTO admins (user_id)
  VALUES (target_user_id)
  ON CONFLICT (user_id) DO NOTHING;

  RAISE NOTICE 'User % (id %) is now an Admin.', target_email, target_user_id;
END $$;

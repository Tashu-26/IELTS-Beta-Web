-- ============================================================
-- IELTS Beta — Supabase Storage setup (Speaking submissions)
-- Run in the Supabase SQL Editor, AFTER schema.sql.
--
-- This is the ONLY place Supabase Storage is used in this project --
-- Writing submissions store their text directly in Postgres
-- (submissions.text_content); only Speaking recordings need object
-- storage.
-- ============================================================

-- Create the bucket (public read, since playback doesn't need to be
-- restricted beyond "you have the link" for this project's scope).
insert into storage.buckets (id, name, public)
values ('speaking-recordings', 'speaking-recordings', true)
on conflict (id) do nothing;

-- Allow any authenticated user to upload into this bucket.
-- (Access control on WHICH student's audio maps to WHICH submission is
-- enforced at the application layer via the `submissions` table --
-- this policy only controls who can write to storage at all.)
create policy "Authenticated users can upload speaking recordings"
on storage.objects for insert
to authenticated
with check (bucket_id = 'speaking-recordings');

-- Allow anyone to read/play back recordings in this bucket (needed so
-- the public URL saved in submissions.audio_url actually plays).
create policy "Anyone can view speaking recordings"
on storage.objects for select
using (bucket_id = 'speaking-recordings');

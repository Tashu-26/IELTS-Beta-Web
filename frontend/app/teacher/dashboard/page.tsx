"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { useAuth } from "@/lib/AuthContext";
import { apiFetch } from "@/lib/api";
import type { TeacherCourse } from "@/lib/types";

export default function TeacherDashboardPage() {
  const { session } = useAuth();
  const [courses, setCourses] = useState<TeacherCourse[] | null>(null);
  const [error, setError] = useState<string | null>(null);

  const [showForm, setShowForm] = useState(false);
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [level, setLevel] = useState("");
  const [duration, setDuration] = useState("");
  const [creating, setCreating] = useState(false);

  const load = async () => {
    if (!session) return;
    try {
      const data = await apiFetch<TeacherCourse[]>("/api/teachers/me/courses", session.access_token);
      setCourses(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to load courses");
    }
  };

  useEffect(() => {
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [session]);

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!session) return;
    setCreating(true);
    setError(null);
    try {
      await apiFetch("/api/courses", session.access_token, {
        method: "POST",
        body: JSON.stringify({
          title,
          description: description || null,
          level: level || null,
          duration: duration ? Number(duration) : null,
        }),
      });
      setTitle("");
      setDescription("");
      setLevel("");
      setDuration("");
      setShowForm(false);
      await load();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to create course");
    } finally {
      setCreating(false);
    }
  };

  return (
    <div>
      <div className="flex items-center justify-between mb-1">
        <h1 className="font-display text-2xl font-bold text-text">My Courses</h1>
        <button
          onClick={() => setShowForm((v) => !v)}
          className="px-4 py-2 rounded-pill bg-teal-500 text-white font-semibold text-sm"
        >
          {showForm ? "Cancel" : "+ New Course"}
        </button>
      </div>
      <p className="text-text-muted mb-8">Courses you teach.</p>

      {error && (
        <div className="mb-6 px-4 py-3 rounded-md bg-coral-100 text-coral-600 text-sm">{error}</div>
      )}

      {showForm && (
        <form onSubmit={handleCreate} className="bg-surface border border-border rounded-lg p-6 mb-8 max-w-lg">
          <label className="block text-sm font-medium text-text mb-1">Title</label>
          <input
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            required
            className="w-full border border-border rounded-sm px-3 py-2 mb-4 bg-surface text-text"
            placeholder="e.g. IELTS Academic Foundation"
          />
          <label className="block text-sm font-medium text-text mb-1">Description</label>
          <textarea
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            rows={2}
            className="w-full border border-border rounded-sm px-3 py-2 mb-4 bg-surface text-text"
          />
          <div className="grid grid-cols-2 gap-3 mb-4">
            <div>
              <label className="block text-sm font-medium text-text mb-1">Level</label>
              <input
                value={level}
                onChange={(e) => setLevel(e.target.value)}
                className="w-full border border-border rounded-sm px-3 py-2 bg-surface text-text"
                placeholder="Intermediate"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-text mb-1">Duration (weeks)</label>
              <input
                type="number"
                min={1}
                value={duration}
                onChange={(e) => setDuration(e.target.value)}
                className="w-full border border-border rounded-sm px-3 py-2 bg-surface text-text"
              />
            </div>
          </div>
          <button
            type="submit"
            disabled={creating}
            className="px-5 py-2.5 rounded-pill bg-teal-500 text-white font-semibold text-sm disabled:opacity-60"
          >
            {creating ? "Creating…" : "Create Course"}
          </button>
        </form>
      )}

      {courses === null && !error && <p className="text-text-soft text-sm">Loading…</p>}

      {courses !== null && courses.length === 0 && (
        <p className="text-text-muted text-sm">You don&apos;t have any courses yet. Create one above.</p>
      )}

      <div className="grid sm:grid-cols-2 gap-4">
        {courses?.map((c) => (
          <Link
            key={c.teacherCourseId}
            href={`/teacher/courses/${c.courseId}`}
            className="bg-surface border border-border rounded-lg p-5 hover:border-teal-500 hover:-translate-y-0.5 transition-all"
          >
            <div className="font-display font-semibold text-text mb-1">{c.courseTitle}</div>
            <span
              className={`inline-block px-2.5 py-0.5 rounded-pill text-xs font-semibold ${
                c.isActive ? "bg-green-100 text-green-600" : "bg-surface-alt text-text-muted"
              }`}
            >
              {c.isActive ? "Active" : "Inactive"}
            </span>
          </Link>
        ))}
      </div>
    </div>
  );
}

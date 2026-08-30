"use client";

import { useEffect, useState } from "react";
import { useAuth } from "@/lib/AuthContext";
import { apiFetch } from "@/lib/api";
import type { Course } from "@/lib/types";

export default function AdminCoursesPage() {
  const { session } = useAuth();
  const [courses, setCourses] = useState<Course[] | null>(null);
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
      const data = await apiFetch<Course[]>("/api/courses", session.access_token);
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
        <h1 className="font-display text-2xl font-bold text-text">Courses</h1>
        <button
          onClick={() => setShowForm((v) => !v)}
          className="px-4 py-2 rounded-pill bg-teal-500 text-white font-semibold text-sm"
        >
          {showForm ? "Cancel" : "+ New Course"}
        </button>
      </div>
      <p className="text-text-muted mb-8">All courses on the platform.</p>

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
          />
          <label className="block text-sm font-medium text-text mb-1">Description</label>
          <textarea
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            rows={2}
            className="w-full border border-border rounded-sm px-3 py-2 mb-4 bg-surface text-text"
          />
          <div className="grid grid-cols-2 gap-3 mb-4">
            <input
              value={level}
              onChange={(e) => setLevel(e.target.value)}
              placeholder="Level"
              className="border border-border rounded-sm px-3 py-2 bg-surface text-text"
            />
            <input
              type="number"
              min={1}
              value={duration}
              onChange={(e) => setDuration(e.target.value)}
              placeholder="Duration (weeks)"
              className="border border-border rounded-sm px-3 py-2 bg-surface text-text"
            />
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

      <div className="grid sm:grid-cols-2 gap-4">
        {courses?.map((c) => (
          <CourseCard key={c.courseId} course={c} onUpdated={load} />
        ))}
      </div>
    </div>
  );
}

function CourseCard({ course, onUpdated }: { course: Course; onUpdated: () => void }) {
  const { session } = useAuth();
  const [editing, setEditing] = useState(false);
  const [title, setTitle] = useState(course.title);
  const [description, setDescription] = useState(course.description ?? "");
  const [level, setLevel] = useState(course.level ?? "");
  const [duration, setDuration] = useState(course.duration?.toString() ?? "");
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!session) return;
    setSaving(true);
    setError(null);
    try {
      await apiFetch(`/api/courses/${course.courseId}`, session.access_token, {
        method: "PUT",
        body: JSON.stringify({
          title,
          description: description || null,
          level: level || null,
          duration: duration ? Number(duration) : null,
        }),
      });
      setEditing(false);
      onUpdated();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to save changes");
    } finally {
      setSaving(false);
    }
  };

  if (editing) {
    return (
      <form onSubmit={handleSave} className="bg-surface border border-teal-500 rounded-lg p-5">
        {error && <div className="mb-3 px-3 py-2 rounded-sm bg-coral-100 text-coral-600 text-sm">{error}</div>}
        <input
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          required
          className="w-full border border-border rounded-sm px-3 py-2 mb-2 bg-surface text-text text-sm font-semibold"
        />
        <textarea
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          rows={2}
          className="w-full border border-border rounded-sm px-3 py-2 mb-2 bg-surface text-text text-sm"
        />
        <div className="grid grid-cols-2 gap-2 mb-3">
          <input
            value={level}
            onChange={(e) => setLevel(e.target.value)}
            placeholder="Level"
            className="border border-border rounded-sm px-3 py-1.5 bg-surface text-text text-sm"
          />
          <input
            type="number"
            min={1}
            value={duration}
            onChange={(e) => setDuration(e.target.value)}
            placeholder="Duration (weeks)"
            className="border border-border rounded-sm px-3 py-1.5 bg-surface text-text text-sm"
          />
        </div>
        <div className="flex gap-2">
          <button
            type="submit"
            disabled={saving}
            className="px-4 py-1.5 rounded-pill bg-teal-500 text-white text-xs font-semibold disabled:opacity-60"
          >
            {saving ? "Saving…" : "Save"}
          </button>
          <button
            type="button"
            onClick={() => setEditing(false)}
            className="px-4 py-1.5 rounded-pill border border-border text-text-muted text-xs font-semibold"
          >
            Cancel
          </button>
        </div>
      </form>
    );
  }

  return (
    <div className="bg-surface border border-border rounded-lg p-5 flex items-start justify-between gap-3">
      <div>
        <div className="font-display font-semibold text-text mb-1">{course.title}</div>
        {course.level && <span className="text-xs text-text-soft">{course.level}</span>}
      </div>
      <button
        onClick={() => setEditing(true)}
        className="shrink-0 px-3 py-1.5 rounded-pill border border-border text-text-muted text-xs font-semibold hover:border-teal-500 hover:text-teal-600"
      >
        Edit
      </button>
    </div>
  );
}

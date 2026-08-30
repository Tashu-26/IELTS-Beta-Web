"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { useAuth } from "@/lib/AuthContext";
import { apiFetch } from "@/lib/api";
import type { Course, Enrollment } from "@/lib/types";

export default function StudentCoursesPage() {
  const { session } = useAuth();
  const [courses, setCourses] = useState<Course[] | null>(null);
  const [enrolledIds, setEnrolledIds] = useState<Set<number>>(new Set());
  const [error, setError] = useState<string | null>(null);
  const [enrollingId, setEnrollingId] = useState<number | null>(null);

  const load = async () => {
    if (!session) return;
    try {
      const [allCourses, myEnrollments] = await Promise.all([
        apiFetch<Course[]>("/api/courses", session.access_token),
        apiFetch<Enrollment[]>("/api/students/me/enrollments", session.access_token),
      ]);
      setCourses(allCourses);
      setEnrolledIds(new Set(myEnrollments.map((e) => e.courseId)));
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to load courses");
    }
  };

  useEffect(() => {
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [session]);

  const enroll = async (courseId: number) => {
    if (!session) return;
    setEnrollingId(courseId);
    setError(null);
    try {
      await apiFetch("/api/enrollments", session.access_token, {
        method: "POST",
        body: JSON.stringify({ courseId }),
      });
      setEnrolledIds((prev) => new Set(prev).add(courseId));
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to enroll");
    } finally {
      setEnrollingId(null);
    }
  };

  return (
    <div>
      <h1 className="font-display text-2xl font-bold text-text mb-1">Courses</h1>
      <p className="text-text-muted mb-8">Browse available courses and enroll to get started.</p>

      {error && (
        <div className="mb-6 px-4 py-3 rounded-md bg-coral-100 text-coral-600 text-sm">{error}</div>
      )}

      {courses === null && <p className="text-text-soft text-sm">Loading…</p>}

      {courses !== null && courses.length === 0 && (
        <p className="text-text-muted text-sm">No courses available yet.</p>
      )}

      <div className="grid sm:grid-cols-2 gap-5">
        {courses?.map((course) => {
          const enrolled = enrolledIds.has(course.courseId);
          return (
            <div key={course.courseId} className="bg-surface border border-border rounded-lg p-6">
              <div className="flex items-start justify-between mb-2">
                <Link
                  href={`/student/courses/${course.courseId}`}
                  className="font-display font-semibold text-text hover:text-teal-600"
                >
                  {course.title}
                </Link>
                {course.level && (
                  <span className="px-2.5 py-0.5 rounded-pill bg-surface-alt text-text-muted text-xs font-semibold shrink-0">
                    {course.level}
                  </span>
                )}
              </div>
              {course.description && (
                <p className="text-sm text-text-muted mb-4 line-clamp-2">{course.description}</p>
              )}
              <div className="flex items-center justify-between">
                {course.duration && (
                  <span className="text-xs text-text-soft">{course.duration} weeks</span>
                )}
                {enrolled ? (
                  <span className="px-3 py-1.5 rounded-pill bg-green-100 text-green-600 text-xs font-semibold">
                    ✓ Enrolled
                  </span>
                ) : (
                  <button
                    onClick={() => enroll(course.courseId)}
                    disabled={enrollingId === course.courseId}
                    className="px-4 py-1.5 rounded-pill bg-teal-500 text-white text-xs font-semibold disabled:opacity-60"
                  >
                    {enrollingId === course.courseId ? "Enrolling…" : "Enroll"}
                  </button>
                )}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}

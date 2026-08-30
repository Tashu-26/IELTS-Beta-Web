"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { useAuth } from "@/lib/AuthContext";
import { apiFetch } from "@/lib/api";
import type { Enrollment, GamificationSummary } from "@/lib/types";

export default function StudentDashboardPage() {
  const { session, profile } = useAuth();
  const [enrollments, setEnrollments] = useState<Enrollment[] | null>(null);
  const [gamification, setGamification] = useState<GamificationSummary | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!session) return;
    apiFetch<Enrollment[]>("/api/students/me/enrollments", session.access_token)
      .then(setEnrollments)
      .catch((err) => setError(err.message));
    apiFetch<GamificationSummary>("/api/students/me/gamification", session.access_token)
      .then(setGamification)
      .catch(() => {
        /* non-critical widget -- fail silently rather than blocking the dashboard */
      });
  }, [session]);

  const targetBand = profile?.targetBand;
  const currentBand = profile?.currentBand;
  const gap = targetBand != null && currentBand != null ? targetBand - currentBand : null;

  return (
    <div>
      <div className="flex items-center justify-between mb-1">
        <h1 className="font-display text-2xl font-bold text-text">
          Welcome back{profile ? `, ${profile.firstName}` : ""} 👋
        </h1>
        {gamification && (
          <Link
            href="/student/rewards"
            className="flex items-center gap-3 px-4 py-2 rounded-pill bg-surface border border-border text-sm font-semibold text-text hover:border-teal-500 transition-colors"
          >
            <span>🔥 {gamification.streak}</span>
            <span className="text-text-soft">·</span>
            <span>⭐ {gamification.xp} XP</span>
          </Link>
        )}
      </div>
      <p className="text-text-muted mb-8">Here's where you stand today.</p>

      <div className="grid sm:grid-cols-3 gap-4 mb-10">
        <div className="bg-surface border border-border rounded-lg p-5">
          <div className="text-xs text-text-soft mb-1">Target Band</div>
          <div className="font-mono font-bold text-2xl text-text">
            {targetBand != null ? targetBand.toFixed(1) : "—"}
          </div>
        </div>
        <div className="bg-surface border border-border rounded-lg p-5">
          <div className="text-xs text-text-soft mb-1">Current Band</div>
          <div className="font-mono font-bold text-2xl text-teal-600">
            {currentBand != null ? currentBand.toFixed(1) : "—"}
          </div>
        </div>
        <div className="bg-surface border border-border rounded-lg p-5">
          <div className="text-xs text-text-soft mb-1">Gap to Target</div>
          <div className="font-mono font-bold text-2xl text-text">
            {gap != null ? (gap <= 0 ? "Reached 🎉" : gap.toFixed(1)) : "—"}
          </div>
        </div>
      </div>

      {(targetBand == null || currentBand == null) && (
        <div className="bg-teal-100 text-teal-700 rounded-md p-4 text-sm mb-10">
          You haven&apos;t set your bands yet.{" "}
          <Link href="/student/goals" className="font-semibold underline">
            Set your goal
          </Link>{" "}
          to see your progress here.
        </div>
      )}

      <div className="flex items-center justify-between mb-4">
        <h2 className="font-display text-lg font-semibold text-text">Your courses</h2>
        <Link href="/student/courses" className="text-sm font-semibold text-teal-600">
          Browse all courses →
        </Link>
      </div>

      {error && <p className="text-coral-600 text-sm">{error}</p>}

      {enrollments === null && !error && <p className="text-text-soft text-sm">Loading…</p>}

      {enrollments !== null && enrollments.length === 0 && (
        <div className="bg-surface border border-dashed border-border rounded-lg p-8 text-center">
          <p className="text-text-muted mb-4">You&apos;re not enrolled in any courses yet.</p>
          <Link
            href="/student/courses"
            className="inline-flex px-5 py-2.5 rounded-pill bg-teal-500 text-white font-semibold text-sm"
          >
            Browse courses
          </Link>
        </div>
      )}

      {enrollments != null && enrollments.length > 0 && (
        <div className="grid sm:grid-cols-2 gap-4">
          {enrollments.map((e) => (
            <Link
              key={e.enrollmentId}
              href={`/student/courses/${e.courseId}`}
              className="bg-surface border border-border rounded-lg p-5 hover:border-teal-500 hover:-translate-y-0.5 transition-all"
            >
              <div className="font-display font-semibold text-text mb-1">{e.courseTitle}</div>
              <span className="inline-block px-2.5 py-0.5 rounded-pill bg-green-100 text-green-600 text-xs font-semibold">
                {e.status}
              </span>
            </Link>
          ))}
        </div>
      )}
    </div>
  );
}

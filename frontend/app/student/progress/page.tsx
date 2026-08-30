"use client";

import { useEffect, useState } from "react";
import { useAuth } from "@/lib/AuthContext";
import { apiFetch } from "@/lib/api";
import type { Progress } from "@/lib/types";

const skillIcons: Record<string, string> = {
  Listening: "🎧",
  Reading: "📖",
  Writing: "✍️",
  Speaking: "🗣️",
};

export default function ProgressPage() {
  const { session } = useAuth();
  const [progress, setProgress] = useState<Progress | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!session) return;
    apiFetch<Progress>("/api/students/me/progress", session.access_token)
      .then(setProgress)
      .catch((err) => setError(err.message));
  }, [session]);

  if (error) return <p className="text-coral-600 text-sm">{error}</p>;
  if (!progress) return <p className="text-text-soft text-sm">Loading…</p>;

  const skills = [
    ["Listening", progress.avgListening],
    ["Reading", progress.avgReading],
    ["Writing", progress.avgWriting],
    ["Speaking", progress.avgSpeaking],
  ] as const;

  return (
    <div>
      <h1 className="font-display text-2xl font-bold text-text mb-1">Your Progress</h1>
      <p className="text-text-muted mb-8">
        Current band reflects your most recently completed test. Skill averages are computed across
        all your graded attempts.
      </p>

      <div className="grid sm:grid-cols-3 gap-4 mb-10">
        <div className="bg-surface border border-border rounded-lg p-5">
          <div className="text-xs text-text-soft mb-1">Target Band</div>
          <div className="font-mono font-bold text-2xl text-text">
            {progress.targetBand != null ? progress.targetBand.toFixed(1) : "—"}
          </div>
        </div>
        <div className="bg-surface border border-border rounded-lg p-5">
          <div className="text-xs text-text-soft mb-1">Current Band</div>
          <div className="font-mono font-bold text-2xl text-teal-600">
            {progress.currentBand != null ? progress.currentBand.toFixed(1) : "—"}
          </div>
        </div>
        <div className="bg-surface border border-border rounded-lg p-5">
          <div className="text-xs text-text-soft mb-1">Tests Completed</div>
          <div className="font-mono font-bold text-2xl text-text">{progress.testsCompleted}</div>
        </div>
      </div>

      <h2 className="font-display text-lg font-semibold text-text mb-4">Skill averages</h2>
      <div className="grid sm:grid-cols-4 gap-3 mb-10">
        {skills.map(([skill, band]) => (
          <div key={skill} className="bg-surface border border-border rounded-lg p-4 text-center">
            <div className="text-2xl mb-1">{skillIcons[skill]}</div>
            <div className="text-xs text-text-soft mb-1">{skill}</div>
            <div className="font-mono font-bold text-text">{band != null ? band.toFixed(1) : "—"}</div>
          </div>
        ))}
      </div>

      <h2 className="font-display text-lg font-semibold text-text mb-4">Recent tests</h2>
      {progress.recentAttempts.length === 0 ? (
        <p className="text-text-soft text-sm">
          You haven&apos;t completed any tests yet. Enroll in a course and take a practice test to see
          your progress here.
        </p>
      ) : (
        <div className="bg-surface border border-border rounded-lg divide-y divide-border-soft">
          {progress.recentAttempts.map((a) => (
            <div key={a.attemptId} className="flex items-center justify-between px-5 py-3.5">
              <div>
                <div className="text-sm font-semibold text-text">{a.testTitle}</div>
                <div className="text-xs text-text-soft">
                  {a.category} · {new Date(a.submitTime).toLocaleDateString()}
                </div>
              </div>
              <div className="font-mono font-bold text-teal-600">
                {a.bandScore != null ? a.bandScore.toFixed(1) : "—"}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

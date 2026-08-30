"use client";

import { useEffect, useState } from "react";
import { useAuth } from "@/lib/AuthContext";
import { apiFetch } from "@/lib/api";
import type { Submission } from "@/lib/types";

export default function StudentSubmissionsPage() {
  const { session } = useAuth();
  const [submissions, setSubmissions] = useState<Submission[] | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!session) return;
    apiFetch<Submission[]>("/api/students/me/submissions", session.access_token)
      .then(setSubmissions)
      .catch((err) => setError(err.message));
  }, [session]);

  if (error) return <p className="text-coral-600 text-sm">{error}</p>;
  if (!submissions) return <p className="text-text-soft text-sm">Loading…</p>;

  return (
    <div>
      <h1 className="font-display text-2xl font-bold text-text mb-1">My Submissions</h1>
      <p className="text-text-muted mb-8">Writing and Speaking work you've submitted for grading.</p>

      {submissions.length === 0 ? (
        <p className="text-text-soft text-sm">
          You haven&apos;t submitted any Writing or Speaking work yet. Go to a course page to submit.
        </p>
      ) : (
        <div className="space-y-4">
          {submissions.map((s) => (
            <div key={s.submissionId} className="bg-surface border border-border rounded-lg p-5">
              <div className="flex items-center justify-between mb-2">
                <div>
                  <span className="text-sm font-semibold text-text">
                    {s.skill} — {s.submissionType}
                  </span>
                  <span className="text-xs text-text-soft ml-2">{s.courseTitle}</span>
                </div>
                <span
                  className={`px-2.5 py-0.5 rounded-pill text-xs font-semibold ${
                    s.status === "Graded" ? "bg-green-100 text-green-600" : "bg-sun-100 text-amber-500"
                  }`}
                >
                  {s.status}
                </span>
              </div>

              {s.skill === "Writing" && s.textContent && (
                <p className="text-sm text-text-muted mb-3 line-clamp-3">{s.textContent}</p>
              )}
              {s.skill === "Speaking" && s.audioUrl && (
                <audio controls src={s.audioUrl} className="w-full mb-3" />
              )}

              {s.status === "Graded" ? (
                <div className="bg-surface-alt rounded-md p-3">
                  <div className="flex items-center gap-2 mb-1">
                    <span className="font-mono font-bold text-teal-600">
                      Band {s.bandScore?.toFixed(1)}
                    </span>
                    <span className="text-xs text-text-soft">by {s.gradedByName}</span>
                  </div>
                  <p className="text-sm text-text-muted">{s.feedback}</p>
                </div>
              ) : (
                <p className="text-xs text-text-soft">Waiting for your teacher to grade this.</p>
              )}

              <p className="text-xs text-text-soft mt-3">{new Date(s.submittedAt).toLocaleString()}</p>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

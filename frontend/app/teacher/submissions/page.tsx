"use client";

import { useEffect, useState } from "react";
import { useAuth } from "@/lib/AuthContext";
import { apiFetch } from "@/lib/api";
import type { Submission } from "@/lib/types";

export default function TeacherSubmissionsPage() {
  const { session } = useAuth();
  const [submissions, setSubmissions] = useState<Submission[] | null>(null);
  const [error, setError] = useState<string | null>(null);

  const load = async () => {
    if (!session) return;
    try {
      const data = await apiFetch<Submission[]>("/api/teachers/me/submissions", session.access_token);
      setSubmissions(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to load submissions");
    }
  };

  useEffect(() => {
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [session]);

  if (error) return <p className="text-coral-600 text-sm">{error}</p>;
  if (!submissions) return <p className="text-text-soft text-sm">Loading…</p>;

  const pending = submissions.filter((s) => s.status === "Pending");
  const graded = submissions.filter((s) => s.status === "Graded");

  return (
    <div>
      <h1 className="font-display text-2xl font-bold text-text mb-1">Submissions</h1>
      <p className="text-text-muted mb-8">Writing and Speaking work from your students.</p>

      <h2 className="font-display text-lg font-semibold text-text mb-4">
        Pending ({pending.length})
      </h2>
      {pending.length === 0 ? (
        <p className="text-text-soft text-sm mb-8">Nothing to grade right now.</p>
      ) : (
        <div className="space-y-4 mb-10">
          {pending.map((s) => (
            <SubmissionCard key={s.submissionId} submission={s} onGraded={load} />
          ))}
        </div>
      )}

      <h2 className="font-display text-lg font-semibold text-text mb-4">
        Graded ({graded.length})
      </h2>
      {graded.length === 0 ? (
        <p className="text-text-soft text-sm">No graded submissions yet.</p>
      ) : (
        <div className="space-y-4">
          {graded.map((s) => (
            <SubmissionCard key={s.submissionId} submission={s} onGraded={load} />
          ))}
        </div>
      )}
    </div>
  );
}

function SubmissionCard({ submission, onGraded }: { submission: Submission; onGraded: () => void }) {
  const { session } = useAuth();
  const [bandScore, setBandScore] = useState(submission.bandScore?.toString() ?? "");
  const [feedback, setFeedback] = useState(submission.feedback ?? "");
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const isGraded = submission.status === "Graded";

  const handleGrade = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!session) return;
    setSaving(true);
    setError(null);
    try {
      await apiFetch(`/api/submissions/${submission.submissionId}/grade`, session.access_token, {
        method: "PUT",
        body: JSON.stringify({ bandScore: Number(bandScore), feedback }),
      });
      onGraded();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to save grade");
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="bg-surface border border-border rounded-lg p-5">
      <div className="flex items-center justify-between mb-2">
        <div>
          <span className="text-sm font-semibold text-text">
            {submission.skill} — {submission.submissionType}
          </span>
          <span className="text-xs text-text-soft ml-2">
            {submission.studentName} · {submission.courseTitle}
          </span>
        </div>
        <span className="text-xs text-text-soft">{new Date(submission.submittedAt).toLocaleDateString()}</span>
      </div>

      {submission.skill === "Writing" && submission.textContent && (
        <p className="text-sm text-text-muted mb-3 whitespace-pre-wrap">{submission.textContent}</p>
      )}
      {submission.skill === "Speaking" && submission.audioUrl && (
        <audio controls src={submission.audioUrl} className="w-full mb-3" />
      )}

      <form onSubmit={handleGrade} className="flex flex-wrap items-end gap-3 border-t border-border-soft pt-3">
        {error && <span className="text-xs text-coral-600 w-full">{error}</span>}
        <div>
          <label className="block text-xs text-text-muted mb-1">Band</label>
          <input
            type="number"
            step="0.5"
            min="0"
            max="9"
            value={bandScore}
            onChange={(e) => setBandScore(e.target.value)}
            required
            className="w-20 border border-border rounded-sm px-2 py-1.5 text-sm bg-surface text-text"
          />
        </div>
        <div className="flex-1 min-w-[200px]">
          <label className="block text-xs text-text-muted mb-1">Feedback</label>
          <input
            value={feedback}
            onChange={(e) => setFeedback(e.target.value)}
            required
            className="w-full border border-border rounded-sm px-2 py-1.5 text-sm bg-surface text-text"
          />
        </div>
        <button
          type="submit"
          disabled={saving}
          className="px-4 py-1.5 rounded-pill bg-teal-500 text-white text-xs font-semibold disabled:opacity-60"
        >
          {saving ? "Saving…" : isGraded ? "Update grade" : "Submit grade"}
        </button>
      </form>
    </div>
  );
}

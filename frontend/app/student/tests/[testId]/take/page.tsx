"use client";

import { useEffect, useRef, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import { useAuth } from "@/lib/AuthContext";
import { apiFetch } from "@/lib/api";
import type { AttemptResult, AttemptStart } from "@/lib/types";

export default function TakeTestPage() {
  const { testId } = useParams<{ testId: string }>();
  const router = useRouter();
  const { session } = useAuth();

  const [attempt, setAttempt] = useState<AttemptStart | null>(null);
  const [answers, setAnswers] = useState<Record<number, number>>({});
  const [result, setResult] = useState<AttemptResult | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const hasStarted = useRef(false);

  useEffect(() => {
    if (!session || !testId || hasStarted.current) return;
    hasStarted.current = true;
    apiFetch<AttemptStart>("/api/test-attempts", session.access_token, {
      method: "POST",
      body: JSON.stringify({ testId: Number(testId) }),
    })
      .then(setAttempt)
      .catch((err) => setError(err instanceof Error ? err.message : "Failed to start test"));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [session, testId]);

  const selectAnswer = (questionId: number, optionId: number) => {
    setAnswers((prev) => ({ ...prev, [questionId]: optionId }));
  };

  const handleSubmit = async () => {
    if (!session || !attempt) return;
    setSubmitting(true);
    setError(null);
    try {
      const payload = {
        answers: attempt.test.questions.map((q) => ({
          questionId: q.questionId,
          selectedOptionId: answers[q.questionId] ?? null,
        })),
      };
      const res = await apiFetch<AttemptResult>(
        `/api/test-attempts/${attempt.attemptId}/submit`,
        session.access_token,
        { method: "PUT", body: JSON.stringify(payload) }
      );
      setResult(res);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to submit");
    } finally {
      setSubmitting(false);
    }
  };

  if (error && !attempt) return <p className="text-coral-600 text-sm">{error}</p>;
  if (!attempt) return <p className="text-text-soft text-sm">Starting test…</p>;

  if (result) {
    const skillRows = [
      ["Listening", result.listening],
      ["Reading", result.reading],
      ["Writing", result.writing],
      ["Speaking", result.speaking],
    ] as const;

    return (
      <div className="max-w-lg mx-auto">
        <div className="bg-surface border border-border rounded-lg p-8 text-center">
          <div className="text-xs text-text-soft mb-1">Estimated Band</div>
          <div className="font-mono font-bold text-5xl text-teal-600 mb-4">
            {result.overallBand != null ? result.overallBand.toFixed(1) : "—"}
          </div>
          <p className="text-text-muted mb-6">{result.feedback}</p>

          <div className="grid grid-cols-2 gap-3 mb-6">
            {skillRows
              .filter(([, band]) => band != null)
              .map(([skill, band]) => (
                <div key={skill} className="bg-surface-alt rounded-md p-3">
                  <div className="text-xs text-text-soft">{skill}</div>
                  <div className="font-mono font-bold text-text">{band?.toFixed(1)}</div>
                </div>
              ))}
          </div>

          <button
            onClick={() => router.push("/student/progress")}
            className="px-6 py-2.5 rounded-pill bg-teal-500 text-white font-semibold text-sm"
          >
            View my progress
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-2xl mx-auto">
      <h1 className="font-display text-2xl font-bold text-text mb-1">{attempt.test.title}</h1>
      <p className="text-text-muted mb-8">
        {attempt.test.category} · {attempt.test.questions.length} question
        {attempt.test.questions.length === 1 ? "" : "s"}
      </p>

      {error && (
        <div className="mb-6 px-4 py-3 rounded-md bg-coral-100 text-coral-600 text-sm">{error}</div>
      )}

      <div className="space-y-5 mb-8">
        {attempt.test.questions.map((q, i) => (
          <div key={q.questionId} className="bg-surface border border-border rounded-lg p-5">
            <div className="flex items-start gap-2 mb-3">
              <span className="font-mono text-xs text-text-soft font-bold mt-0.5">
                {String(i + 1).padStart(2, "0")}
              </span>
              <p className="text-sm font-medium text-text">{q.questionText}</p>
            </div>
            <div className="space-y-2 pl-7">
              {q.options.map((o) => (
                <label
                  key={o.optionId}
                  className="flex items-center gap-2 text-sm text-text-muted cursor-pointer"
                >
                  <input
                    type="radio"
                    name={`q-${q.questionId}`}
                    checked={answers[q.questionId] === o.optionId}
                    onChange={() => selectAnswer(q.questionId, o.optionId)}
                  />
                  {o.optionText}
                </label>
              ))}
            </div>
          </div>
        ))}
      </div>

      <button
        onClick={handleSubmit}
        disabled={submitting}
        className="w-full px-6 py-3 rounded-pill bg-teal-500 text-white font-semibold disabled:opacity-60"
      >
        {submitting ? "Submitting…" : "Submit test"}
      </button>
    </div>
  );
}

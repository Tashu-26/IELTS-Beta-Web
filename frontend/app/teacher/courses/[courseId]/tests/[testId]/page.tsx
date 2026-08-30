"use client";

import { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import { useAuth } from "@/lib/AuthContext";
import { apiFetch } from "@/lib/api";
import type { PracticeTestDetail } from "@/lib/types";

const skills = ["Listening", "Reading", "Writing", "Speaking"];

export default function ManageTestPage() {
  const { courseId, testId } = useParams<{ courseId: string; testId: string }>();
  const router = useRouter();
  const { session } = useAuth();

  const [test, setTest] = useState<PracticeTestDetail | null>(null);
  const [error, setError] = useState<string | null>(null);

  const load = async () => {
    if (!session || !testId) return;
    try {
      const data = await apiFetch<PracticeTestDetail>(`/api/practice-tests/${testId}`, session.access_token);
      setTest(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to load test");
    }
  };

  useEffect(() => {
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [session, testId]);

  if (error) return <p className="text-coral-600 text-sm">{error}</p>;
  if (!test) return <p className="text-text-soft text-sm">Loading…</p>;

  return (
    <div>
      <button
        onClick={() => router.push(`/teacher/courses/${courseId}`)}
        className="text-sm font-semibold text-teal-600 mb-4"
      >
        ← Back to course
      </button>
      <h1 className="font-display text-2xl font-bold text-text mb-1">{test.title}</h1>
      <p className="text-text-muted mb-8">
        {test.category} · {test.questions.length} question{test.questions.length === 1 ? "" : "s"}
      </p>

      <AddQuestionForm testId={test.testId} onAdded={load} />

      <div className="space-y-4 mt-8">
        {test.questions.map((q) => (
          <div key={q.questionId} className="bg-surface border border-border rounded-lg p-5">
            <div className="flex items-center gap-2 mb-2">
              <span className="px-2 py-0.5 rounded-pill bg-surface-alt text-text-muted text-xs font-semibold">
                {q.skill}
              </span>
              <span className="text-xs text-text-soft">{q.marks} mark{q.marks === 1 ? "" : "s"}</span>
            </div>
            <p className="text-sm font-medium text-text mb-3">{q.questionText}</p>

            <ul className="space-y-1.5 mb-3">
              {q.options.map((o) => (
                <li key={o.optionId} className="flex items-center gap-2 text-sm text-text-muted">
                  <span>{o.isCorrect ? "✅" : "⬜"}</span>
                  {o.optionText}
                </li>
              ))}
            </ul>

            <AddOptionForm questionId={q.questionId} onAdded={load} />
          </div>
        ))}
      </div>
    </div>
  );
}

function AddQuestionForm({ testId, onAdded }: { testId: number; onAdded: () => void }) {
  const { session } = useAuth();
  const [questionText, setQuestionText] = useState("");
  const [skill, setSkill] = useState(skills[0]);
  const [marks, setMarks] = useState("1");
  const [error, setError] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!session) return;
    setSaving(true);
    setError(null);
    try {
      await apiFetch(`/api/practice-tests/${testId}/questions`, session.access_token, {
        method: "POST",
        body: JSON.stringify({ questionText, skill, marks: Number(marks) }),
      });
      setQuestionText("");
      setMarks("1");
      onAdded();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to add question");
    } finally {
      setSaving(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="bg-surface border border-border rounded-lg p-5 max-w-xl">
      <h2 className="font-display font-semibold text-text mb-3">Add a question</h2>
      {error && <div className="mb-3 px-3 py-2 rounded-sm bg-coral-100 text-coral-600 text-sm">{error}</div>}
      <textarea
        value={questionText}
        onChange={(e) => setQuestionText(e.target.value)}
        required
        rows={2}
        placeholder="Question text"
        className="w-full border border-border rounded-sm px-3 py-2 mb-3 bg-surface text-text"
      />
      <div className="grid grid-cols-2 gap-3 mb-3">
        <select
          value={skill}
          onChange={(e) => setSkill(e.target.value)}
          className="border border-border rounded-sm px-3 py-2 bg-surface text-text"
        >
          {skills.map((s) => (
            <option key={s}>{s}</option>
          ))}
        </select>
        <input
          type="number"
          min={1}
          value={marks}
          onChange={(e) => setMarks(e.target.value)}
          className="border border-border rounded-sm px-3 py-2 bg-surface text-text"
        />
      </div>
      <button
        type="submit"
        disabled={saving}
        className="px-4 py-2 rounded-pill bg-teal-500 text-white text-sm font-semibold disabled:opacity-60"
      >
        {saving ? "Adding…" : "Add question"}
      </button>
    </form>
  );
}

function AddOptionForm({ questionId, onAdded }: { questionId: number; onAdded: () => void }) {
  const { session } = useAuth();
  const [optionText, setOptionText] = useState("");
  const [isCorrect, setIsCorrect] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!session) return;
    setSaving(true);
    setError(null);
    try {
      await apiFetch(`/api/questions/${questionId}/answer-options`, session.access_token, {
        method: "POST",
        body: JSON.stringify({ optionText, isCorrect }),
      });
      setOptionText("");
      setIsCorrect(false);
      onAdded();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to add option");
    } finally {
      setSaving(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="flex items-center gap-2 border-t border-border-soft pt-3">
      {error && <span className="text-xs text-coral-600">{error}</span>}
      <input
        value={optionText}
        onChange={(e) => setOptionText(e.target.value)}
        required
        placeholder="Add answer option"
        className="flex-1 border border-border rounded-sm px-2 py-1.5 text-sm bg-surface text-text"
      />
      <label className="flex items-center gap-1 text-xs text-text-muted whitespace-nowrap">
        <input type="checkbox" checked={isCorrect} onChange={(e) => setIsCorrect(e.target.checked)} />
        Correct
      </label>
      <button
        type="submit"
        disabled={saving}
        className="px-3 py-1.5 rounded-pill bg-surface-alt text-text text-xs font-semibold disabled:opacity-60"
      >
        Add
      </button>
    </form>
  );
}

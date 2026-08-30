"use client";

import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import Link from "next/link";
import { useAuth } from "@/lib/AuthContext";
import { apiFetch } from "@/lib/api";
import { supabase } from "@/lib/supabaseClient";
import type { Content, Course, LiveClass, PracticeTestSummary } from "@/lib/types";

const contentIcon: Record<string, string> = {
  Video: "🎬",
  PDF: "📄",
  YouTube: "▶️",
  Notes: "📝",
};

export default function CourseDetailPage() {
  const { courseId } = useParams<{ courseId: string }>();
  const { session } = useAuth();

  const [course, setCourse] = useState<Course | null>(null);
  const [contents, setContents] = useState<Content[]>([]);
  const [tests, setTests] = useState<PracticeTestSummary[]>([]);
  const [liveClasses, setLiveClasses] = useState<LiveClass[]>([]);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!session || !courseId) return;
    const token = session.access_token;

    Promise.all([
      apiFetch<Course>(`/api/courses/${courseId}`, token),
      apiFetch<Content[]>(`/api/courses/${courseId}/contents`, token),
      apiFetch<PracticeTestSummary[]>(`/api/courses/${courseId}/practice-tests`, token),
      apiFetch<LiveClass[]>(`/api/courses/${courseId}/live-classes`, token),
    ])
      .then(([c, ct, pt, lc]) => {
        setCourse(c);
        setContents(ct);
        setTests(pt);
        setLiveClasses(lc);
      })
      .catch((err) => setError(err instanceof Error ? err.message : "Failed to load course"));
  }, [session, courseId]);

  if (error) {
    return <p className="text-coral-600 text-sm">{error}</p>;
  }

  if (!course) {
    return <p className="text-text-soft text-sm">Loading…</p>;
  }

  return (
    <div>
      <h1 className="font-display text-2xl font-bold text-text mb-1">{course.title}</h1>
      {course.description && <p className="text-text-muted mb-8 max-w-2xl">{course.description}</p>}

      <section className="mb-10">
        <h2 className="font-display text-lg font-semibold text-text mb-4">Learning materials</h2>
        {contents.length === 0 ? (
          <p className="text-text-soft text-sm">No materials posted yet.</p>
        ) : (
          <div className="grid sm:grid-cols-2 gap-3">
            {contents.map((c) => (
              <a
                key={c.contentId}
                href={c.youtubeLink || c.fileUrl || "#"}
                target="_blank"
                rel="noreferrer"
                className="flex items-center gap-3 bg-surface border border-border rounded-md p-4 hover:border-teal-500 transition-colors"
              >
                <span className="text-xl">{contentIcon[c.contentType] ?? "📎"}</span>
                <div>
                  <div className="text-sm font-semibold text-text">{c.title}</div>
                  <div className="text-xs text-text-soft">{c.contentType}</div>
                </div>
              </a>
            ))}
          </div>
        )}
      </section>

      <section className="mb-10">
        <h2 className="font-display text-lg font-semibold text-text mb-4">Practice tests</h2>
        {tests.length === 0 ? (
          <p className="text-text-soft text-sm">No practice tests posted yet.</p>
        ) : (
          <div className="grid sm:grid-cols-2 gap-3">
            {tests.map((t) => (
              <div key={t.testId} className="bg-surface border border-border rounded-md p-4 flex items-center justify-between gap-3">
                <div>
                  <div className="text-sm font-semibold text-text mb-1">{t.title}</div>
                  <div className="flex gap-2 text-xs text-text-soft">
                    <span>{t.category}</span>
                    {t.duration && <span>· {t.duration} min</span>}
                    {t.totalMarks && <span>· {t.totalMarks} marks</span>}
                  </div>
                </div>
                <Link
                  href={`/student/tests/${t.testId}/take`}
                  className="shrink-0 px-4 py-1.5 rounded-pill bg-teal-500 text-white text-xs font-semibold"
                >
                  Start
                </Link>
              </div>
            ))}
          </div>
        )}
      </section>

      <section className="mb-10">
        <h2 className="font-display text-lg font-semibold text-text mb-4">Live classes</h2>
        {liveClasses.length === 0 ? (
          <p className="text-text-soft text-sm">No live classes scheduled yet.</p>
        ) : (
          <div className="space-y-3">
            {liveClasses.map((lc) => (
              <div
                key={lc.classId}
                className="flex items-center justify-between bg-surface border border-border rounded-md p-4"
              >
                <div>
                  <div className="text-sm font-semibold text-text">
                    {new Date(lc.classDate).toLocaleString()}
                  </div>
                </div>
                <a
                  href={lc.meetingLink}
                  target="_blank"
                  rel="noreferrer"
                  className="px-4 py-1.5 rounded-pill bg-teal-500 text-white text-xs font-semibold"
                >
                  Join
                </a>
              </div>
            ))}
          </div>
        )}
      </section>

      <section>
        <h2 className="font-display text-lg font-semibold text-text mb-4">
          Submit Writing or Speaking work
        </h2>
        <SubmissionForm courseId={Number(courseId)} />
        <p className="text-xs text-text-soft mt-3">
          See the status and feedback on your submissions under{" "}
          <Link href="/student/submissions" className="font-semibold text-teal-600">
            My Submissions
          </Link>
          .
        </p>
      </section>
    </div>
  );
}

function SubmissionForm({ courseId }: { courseId: number }) {
  const { session } = useAuth();
  const [skill, setSkill] = useState<"Writing" | "Speaking">("Writing");
  const [submissionType, setSubmissionType] = useState("Task 2");
  const [textContent, setTextContent] = useState("");
  const [file, setFile] = useState<File | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!session) return;
    setSubmitting(true);
    setError(null);
    setSuccess(false);
    try {
      let audioUrl: string | undefined;

      if (skill === "Speaking") {
        if (!file) throw new Error("Please choose an audio recording to upload.");
        const path = `${session.user.id}/${Date.now()}-${file.name}`;
        const { error: uploadError } = await supabase.storage
          .from("speaking-recordings")
          .upload(path, file);
        if (uploadError) throw uploadError;
        const { data } = supabase.storage.from("speaking-recordings").getPublicUrl(path);
        audioUrl = data.publicUrl;
      }

      await apiFetch("/api/submissions", session.access_token, {
        method: "POST",
        body: JSON.stringify({
          courseId,
          skill,
          submissionType,
          textContent: skill === "Writing" ? textContent : null,
          audioUrl: skill === "Speaking" ? audioUrl : null,
        }),
      });

      setTextContent("");
      setFile(null);
      setSuccess(true);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to submit");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="bg-surface border border-border rounded-lg p-6 max-w-xl">
      {error && <div className="mb-4 px-3 py-2 rounded-sm bg-coral-100 text-coral-600 text-sm">{error}</div>}
      {success && (
        <div className="mb-4 px-3 py-2 rounded-sm bg-green-100 text-green-600 text-sm">
          Submitted! Your teacher will grade it soon.
        </div>
      )}

      <div className="grid grid-cols-2 gap-3 mb-4">
        <div>
          <label className="block text-sm font-medium text-text mb-1">Skill</label>
          <select
            value={skill}
            onChange={(e) => setSkill(e.target.value as "Writing" | "Speaking")}
            className="w-full border border-border rounded-sm px-3 py-2 bg-surface text-text"
          >
            <option>Writing</option>
            <option>Speaking</option>
          </select>
        </div>
        <div>
          <label className="block text-sm font-medium text-text mb-1">Task</label>
          <input
            value={submissionType}
            onChange={(e) => setSubmissionType(e.target.value)}
            required
            placeholder="e.g. Task 2, Part 2"
            className="w-full border border-border rounded-sm px-3 py-2 bg-surface text-text"
          />
        </div>
      </div>

      {skill === "Writing" ? (
        <>
          <label className="block text-sm font-medium text-text mb-1">Your essay</label>
          <textarea
            value={textContent}
            onChange={(e) => setTextContent(e.target.value)}
            required
            rows={8}
            className="w-full border border-border rounded-sm px-3 py-2 mb-4 bg-surface text-text"
            placeholder="Write your response here..."
          />
        </>
      ) : (
        <>
          <label className="block text-sm font-medium text-text mb-1">Audio recording</label>
          <input
            type="file"
            accept="audio/*"
            onChange={(e) => setFile(e.target.files?.[0] ?? null)}
            required
            className="w-full text-sm text-text mb-4"
          />
        </>
      )}

      <button
        type="submit"
        disabled={submitting}
        className="px-5 py-2.5 rounded-pill bg-teal-500 text-white font-semibold text-sm disabled:opacity-60"
      >
        {submitting ? "Submitting…" : "Submit for grading"}
      </button>
    </form>
  );
}

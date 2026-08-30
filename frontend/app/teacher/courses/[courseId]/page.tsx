"use client";

import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import Link from "next/link";
import { useAuth } from "@/lib/AuthContext";
import { apiFetch } from "@/lib/api";
import type {
  Content,
  Course,
  CourseEnrollment,
  LiveClass,
  PracticeTestSummary,
  TeacherCourse,
} from "@/lib/types";

type Tab = "content" | "tests" | "live" | "students";

export default function TeacherCourseDetailPage() {
  const { courseId } = useParams<{ courseId: string }>();
  const { session } = useAuth();

  const [tab, setTab] = useState<Tab>("content");
  const [course, setCourse] = useState<Course | null>(null);
  const [teacherCourseId, setTeacherCourseId] = useState<number | null>(null);

  const [contents, setContents] = useState<Content[]>([]);
  const [tests, setTests] = useState<PracticeTestSummary[]>([]);
  const [liveClasses, setLiveClasses] = useState<LiveClass[]>([]);
  const [students, setStudents] = useState<CourseEnrollment[]>([]);

  const [error, setError] = useState<string | null>(null);

  const loadAll = async () => {
    if (!session || !courseId) return;
    const token = session.access_token;
    try {
      const [c, ct, pt, lc, roster, myCourses] = await Promise.all([
        apiFetch<Course>(`/api/courses/${courseId}`, token),
        apiFetch<Content[]>(`/api/courses/${courseId}/contents`, token),
        apiFetch<PracticeTestSummary[]>(`/api/courses/${courseId}/practice-tests`, token),
        apiFetch<LiveClass[]>(`/api/courses/${courseId}/live-classes`, token),
        apiFetch<CourseEnrollment[]>(`/api/courses/${courseId}/enrollments`, token),
        apiFetch<TeacherCourse[]>("/api/teachers/me/courses", token),
      ]);
      setCourse(c);
      setContents(ct);
      setTests(pt);
      setLiveClasses(lc);
      setStudents(roster);
      const mine = myCourses.find((tc) => tc.courseId === Number(courseId));
      setTeacherCourseId(mine?.teacherCourseId ?? null);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to load course");
    }
  };

  useEffect(() => {
    loadAll();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [session, courseId]);

  if (error) return <p className="text-coral-600 text-sm">{error}</p>;
  if (!course) return <p className="text-text-soft text-sm">Loading…</p>;

  return (
    <div>
      <h1 className="font-display text-2xl font-bold text-text mb-1">{course.title}</h1>
      <p className="text-text-muted mb-6 max-w-2xl">{course.description}</p>

      <div className="flex gap-2 border-b border-border-soft mb-6">
        {([
          ["content", "Content"],
          ["tests", "Practice Tests"],
          ["live", "Live Classes"],
          ["students", "Students"],
        ] as [Tab, string][]).map(([key, label]) => (
          <button
            key={key}
            onClick={() => setTab(key)}
            className={`px-4 py-2.5 text-sm font-semibold border-b-2 transition-colors ${
              tab === key ? "border-teal-500 text-teal-600" : "border-transparent text-text-muted"
            }`}
          >
            {label}
          </button>
        ))}
      </div>

      {tab === "content" && (
        <ContentTab courseId={Number(courseId)} contents={contents} onCreated={loadAll} />
      )}
      {tab === "tests" && <TestsTab courseId={Number(courseId)} tests={tests} onCreated={loadAll} />}
      {tab === "live" && (
        <LiveClassesTab teacherCourseId={teacherCourseId} liveClasses={liveClasses} onCreated={loadAll} />
      )}
      {tab === "students" && <StudentsTab students={students} />}
    </div>
  );
}

function ContentTab({
  courseId,
  contents,
  onCreated,
}: {
  courseId: number;
  contents: Content[];
  onCreated: () => void;
}) {
  const { session } = useAuth();
  const [title, setTitle] = useState("");
  const [contentType, setContentType] = useState("Notes");
  const [youtubeLink, setYoutubeLink] = useState("");
  const [fileUrl, setFileUrl] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!session) return;
    setSaving(true);
    setError(null);
    try {
      await apiFetch(`/api/courses/${courseId}/contents`, session.access_token, {
        method: "POST",
        body: JSON.stringify({
          title,
          contentType,
          youtubeLink: youtubeLink || null,
          fileUrl: fileUrl || null,
        }),
      });
      setTitle("");
      setYoutubeLink("");
      setFileUrl("");
      onCreated();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to add content");
    } finally {
      setSaving(false);
    }
  };

  return (
    <div>
      <form onSubmit={handleSubmit} className="bg-surface border border-border rounded-lg p-5 mb-6 max-w-lg">
        {error && <div className="mb-3 px-3 py-2 rounded-sm bg-coral-100 text-coral-600 text-sm">{error}</div>}
        <div className="grid grid-cols-2 gap-3 mb-3">
          <input
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            required
            placeholder="Title"
            className="border border-border rounded-sm px-3 py-2 bg-surface text-text col-span-2"
          />
          <select
            value={contentType}
            onChange={(e) => setContentType(e.target.value)}
            className="border border-border rounded-sm px-3 py-2 bg-surface text-text col-span-2"
          >
            <option>Notes</option>
            <option>Video</option>
            <option>YouTube</option>
            <option>PDF</option>
          </select>
          <input
            value={youtubeLink}
            onChange={(e) => setYoutubeLink(e.target.value)}
            placeholder="YouTube link (optional)"
            className="border border-border rounded-sm px-3 py-2 bg-surface text-text col-span-2"
          />
          <input
            value={fileUrl}
            onChange={(e) => setFileUrl(e.target.value)}
            placeholder="File URL (optional)"
            className="border border-border rounded-sm px-3 py-2 bg-surface text-text col-span-2"
          />
        </div>
        <button
          type="submit"
          disabled={saving}
          className="px-4 py-2 rounded-pill bg-teal-500 text-white text-sm font-semibold disabled:opacity-60"
        >
          {saving ? "Adding…" : "Add material"}
        </button>
      </form>

      <div className="grid sm:grid-cols-2 gap-3">
        {contents.map((c) => (
          <div key={c.contentId} className="bg-surface border border-border rounded-md p-4">
            <div className="text-sm font-semibold text-text">{c.title}</div>
            <div className="text-xs text-text-soft">{c.contentType}</div>
          </div>
        ))}
      </div>
    </div>
  );
}

function TestsTab({
  courseId,
  tests,
  onCreated,
}: {
  courseId: number;
  tests: PracticeTestSummary[];
  onCreated: () => void;
}) {
  const { session } = useAuth();
  const [title, setTitle] = useState("");
  const [category, setCategory] = useState("Academic");
  const [duration, setDuration] = useState("");
  const [totalMarks, setTotalMarks] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!session) return;
    setSaving(true);
    setError(null);
    try {
      await apiFetch("/api/practice-tests", session.access_token, {
        method: "POST",
        body: JSON.stringify({
          courseId,
          title,
          category,
          duration: duration ? Number(duration) : null,
          totalMarks: totalMarks ? Number(totalMarks) : null,
        }),
      });
      setTitle("");
      setDuration("");
      setTotalMarks("");
      onCreated();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to create test");
    } finally {
      setSaving(false);
    }
  };

  return (
    <div>
      <form onSubmit={handleSubmit} className="bg-surface border border-border rounded-lg p-5 mb-6 max-w-lg">
        {error && <div className="mb-3 px-3 py-2 rounded-sm bg-coral-100 text-coral-600 text-sm">{error}</div>}
        <div className="grid grid-cols-2 gap-3 mb-3">
          <input
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            required
            placeholder="Test title"
            className="border border-border rounded-sm px-3 py-2 bg-surface text-text col-span-2"
          />
          <select
            value={category}
            onChange={(e) => setCategory(e.target.value)}
            className="border border-border rounded-sm px-3 py-2 bg-surface text-text"
          >
            <option>Academic</option>
            <option>General</option>
          </select>
          <input
            type="number"
            value={duration}
            onChange={(e) => setDuration(e.target.value)}
            placeholder="Duration (min)"
            className="border border-border rounded-sm px-3 py-2 bg-surface text-text"
          />
          <input
            type="number"
            value={totalMarks}
            onChange={(e) => setTotalMarks(e.target.value)}
            placeholder="Total marks"
            className="border border-border rounded-sm px-3 py-2 bg-surface text-text col-span-2"
          />
        </div>
        <button
          type="submit"
          disabled={saving}
          className="px-4 py-2 rounded-pill bg-teal-500 text-white text-sm font-semibold disabled:opacity-60"
        >
          {saving ? "Creating…" : "Create test"}
        </button>
      </form>

      <div className="grid sm:grid-cols-2 gap-3">
        {tests.map((t) => (
          <Link
            key={t.testId}
            href={`/teacher/courses/${courseId}/tests/${t.testId}`}
            className="bg-surface border border-border rounded-md p-4 hover:border-teal-500 transition-colors"
          >
            <div className="text-sm font-semibold text-text mb-1">{t.title}</div>
            <div className="text-xs text-text-soft">{t.category}</div>
          </Link>
        ))}
      </div>
    </div>
  );
}

function LiveClassesTab({
  teacherCourseId,
  liveClasses,
  onCreated,
}: {
  teacherCourseId: number | null;
  liveClasses: LiveClass[];
  onCreated: () => void;
}) {
  const { session } = useAuth();
  const [meetingLink, setMeetingLink] = useState("");
  const [classDate, setClassDate] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!session || !teacherCourseId) return;
    setSaving(true);
    setError(null);
    try {
      await apiFetch(`/api/teacher-courses/${teacherCourseId}/live-classes`, session.access_token, {
        method: "POST",
        body: JSON.stringify({ meetingLink, classDate: new Date(classDate).toISOString() }),
      });
      setMeetingLink("");
      setClassDate("");
      onCreated();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to schedule class");
    } finally {
      setSaving(false);
    }
  };

  return (
    <div>
      <form onSubmit={handleSubmit} className="bg-surface border border-border rounded-lg p-5 mb-6 max-w-lg">
        {error && <div className="mb-3 px-3 py-2 rounded-sm bg-coral-100 text-coral-600 text-sm">{error}</div>}
        <div className="grid grid-cols-2 gap-3 mb-3">
          <input
            value={meetingLink}
            onChange={(e) => setMeetingLink(e.target.value)}
            required
            placeholder="Meeting link"
            className="border border-border rounded-sm px-3 py-2 bg-surface text-text col-span-2"
          />
          <input
            type="datetime-local"
            value={classDate}
            onChange={(e) => setClassDate(e.target.value)}
            required
            className="border border-border rounded-sm px-3 py-2 bg-surface text-text col-span-2"
          />
        </div>
        <button
          type="submit"
          disabled={saving || !teacherCourseId}
          className="px-4 py-2 rounded-pill bg-teal-500 text-white text-sm font-semibold disabled:opacity-60"
        >
          {saving ? "Scheduling…" : "Schedule class"}
        </button>
      </form>

      <div className="space-y-3">
        {liveClasses.map((lc) => (
          <div key={lc.classId} className="flex items-center justify-between bg-surface border border-border rounded-md p-4">
            <span className="text-sm font-semibold text-text">{new Date(lc.classDate).toLocaleString()}</span>
            <a href={lc.meetingLink} target="_blank" rel="noreferrer" className="text-xs font-semibold text-teal-600">
              {lc.meetingLink}
            </a>
          </div>
        ))}
      </div>
    </div>
  );
}

function StudentsTab({ students }: { students: CourseEnrollment[] }) {
  if (students.length === 0) {
    return <p className="text-text-soft text-sm">No students enrolled yet.</p>;
  }
  return (
    <div className="bg-surface border border-border rounded-lg divide-y divide-border-soft">
      {students.map((s) => (
        <div key={s.enrollmentId} className="flex items-center justify-between px-5 py-3">
          <span className="text-sm font-medium text-text">{s.studentName}</span>
          <span className="px-2.5 py-0.5 rounded-pill bg-green-100 text-green-600 text-xs font-semibold">
            {s.status}
          </span>
        </div>
      ))}
    </div>
  );
}

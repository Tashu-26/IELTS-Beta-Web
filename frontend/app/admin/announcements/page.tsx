"use client";

import { useEffect, useState } from "react";
import { useAuth } from "@/lib/AuthContext";
import { apiFetch } from "@/lib/api";
import type { Announcement } from "@/lib/types";

export default function AdminAnnouncementsPage() {
  const { session } = useAuth();
  const [announcements, setAnnouncements] = useState<Announcement[] | null>(null);
  const [title, setTitle] = useState("");
  const [message, setMessage] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [posting, setPosting] = useState(false);

  const load = async () => {
    if (!session) return;
    try {
      const data = await apiFetch<Announcement[]>("/api/announcements", session.access_token);
      setAnnouncements(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to load announcements");
    }
  };

  useEffect(() => {
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [session]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!session) return;
    setPosting(true);
    setError(null);
    try {
      await apiFetch("/api/announcements", session.access_token, {
        method: "POST",
        body: JSON.stringify({ title, message }),
      });
      setTitle("");
      setMessage("");
      await load();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to post announcement");
    } finally {
      setPosting(false);
    }
  };

  return (
    <div>
      <h1 className="font-display text-2xl font-bold text-text mb-1">Announcements</h1>
      <p className="text-text-muted mb-8">Post updates visible to everyone.</p>

      <form onSubmit={handleSubmit} className="bg-surface border border-border rounded-lg p-6 mb-10 max-w-xl">
        {error && (
          <div className="mb-4 px-3 py-2 rounded-sm bg-coral-100 text-coral-600 text-sm">{error}</div>
        )}
        <label className="block text-sm font-medium text-text mb-1">Title</label>
        <input
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          required
          className="w-full border border-border rounded-sm px-3 py-2 mb-4 bg-surface text-text"
        />
        <label className="block text-sm font-medium text-text mb-1">Message</label>
        <textarea
          value={message}
          onChange={(e) => setMessage(e.target.value)}
          required
          rows={3}
          className="w-full border border-border rounded-sm px-3 py-2 mb-4 bg-surface text-text"
        />
        <button
          type="submit"
          disabled={posting}
          className="px-5 py-2.5 rounded-pill bg-teal-500 text-white font-semibold text-sm disabled:opacity-60"
        >
          {posting ? "Posting…" : "Post announcement"}
        </button>
      </form>

      <div className="space-y-3">
        {announcements?.map((a) => (
          <div key={a.announcementId} className="bg-surface border border-border rounded-md p-4">
            <div className="text-sm font-semibold text-text mb-1">{a.title}</div>
            <p className="text-sm text-text-muted mb-2">{a.message}</p>
            <p className="text-xs text-text-soft">
              by {a.adminName} · {new Date(a.createdAt).toLocaleString()}
            </p>
          </div>
        ))}
      </div>
    </div>
  );
}

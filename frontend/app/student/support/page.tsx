"use client";

import { useEffect, useState } from "react";
import { useAuth } from "@/lib/AuthContext";
import { apiFetch } from "@/lib/api";
import type { SupportTicket } from "@/lib/types";

const statusColor: Record<string, string> = {
  Open: "bg-coral-100 text-coral-600",
  "In Progress": "bg-sun-100 text-amber-500",
  Resolved: "bg-green-100 text-green-600",
};

export default function SupportPage() {
  const { session } = useAuth();
  const [tickets, setTickets] = useState<SupportTicket[] | null>(null);
  const [subject, setSubject] = useState("");
  const [message, setMessage] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const load = async () => {
    if (!session) return;
    try {
      const data = await apiFetch<SupportTicket[]>("/api/support-tickets/mine", session.access_token);
      setTickets(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to load tickets");
    }
  };

  useEffect(() => {
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [session]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!session) return;
    setSubmitting(true);
    setError(null);
    try {
      await apiFetch("/api/support-tickets", session.access_token, {
        method: "POST",
        body: JSON.stringify({ subject, message }),
      });
      setSubject("");
      setMessage("");
      await load();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to submit ticket");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div>
      <h1 className="font-display text-2xl font-bold text-text mb-1">Support</h1>
      <p className="text-text-muted mb-8">Need help? Send us a message.</p>

      <form onSubmit={handleSubmit} className="bg-surface border border-border rounded-lg p-6 mb-10 max-w-xl">
        {error && (
          <div className="mb-4 px-3 py-2 rounded-sm bg-coral-100 text-coral-600 text-sm">{error}</div>
        )}
        <label className="block text-sm font-medium text-text mb-1">Subject</label>
        <input
          value={subject}
          onChange={(e) => setSubject(e.target.value)}
          required
          className="w-full border border-border rounded-sm px-3 py-2 mb-4 bg-surface text-text"
          placeholder="What's this about?"
        />
        <label className="block text-sm font-medium text-text mb-1">Message</label>
        <textarea
          value={message}
          onChange={(e) => setMessage(e.target.value)}
          required
          rows={4}
          className="w-full border border-border rounded-sm px-3 py-2 mb-4 bg-surface text-text"
          placeholder="Describe your issue..."
        />
        <button
          type="submit"
          disabled={submitting}
          className="px-5 py-2.5 rounded-pill bg-teal-500 text-white font-semibold text-sm disabled:opacity-60"
        >
          {submitting ? "Sending…" : "Send message"}
        </button>
      </form>

      <h2 className="font-display text-lg font-semibold text-text mb-4">Your tickets</h2>
      {tickets === null && <p className="text-text-soft text-sm">Loading…</p>}
      {tickets !== null && tickets.length === 0 && (
        <p className="text-text-soft text-sm">You haven&apos;t submitted any tickets yet.</p>
      )}
      <div className="space-y-3">
        {tickets?.map((t) => (
          <div key={t.ticketId} className="bg-surface border border-border rounded-md p-4">
            <div className="flex items-center justify-between mb-1">
              <span className="text-sm font-semibold text-text">{t.subject}</span>
              <span className={`px-2.5 py-0.5 rounded-pill text-xs font-semibold ${statusColor[t.status] ?? ""}`}>
                {t.status}
              </span>
            </div>
            <p className="text-sm text-text-muted">{t.message}</p>
            <p className="text-xs text-text-soft mt-2">{new Date(t.createdAt).toLocaleString()}</p>
          </div>
        ))}
      </div>
    </div>
  );
}

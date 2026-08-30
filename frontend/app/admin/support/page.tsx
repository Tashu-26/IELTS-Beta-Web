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

const statuses = ["Open", "In Progress", "Resolved"];

export default function AdminSupportPage() {
  const { session } = useAuth();
  const [tickets, setTickets] = useState<SupportTicket[] | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [updatingId, setUpdatingId] = useState<number | null>(null);

  const load = async () => {
    if (!session) return;
    try {
      const data = await apiFetch<SupportTicket[]>("/api/support-tickets", session.access_token);
      setTickets(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to load tickets");
    }
  };

  useEffect(() => {
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [session]);

  const updateStatus = async (ticketId: number, status: string) => {
    if (!session) return;
    setUpdatingId(ticketId);
    setError(null);
    try {
      await apiFetch(`/api/support-tickets/${ticketId}`, session.access_token, {
        method: "PUT",
        body: JSON.stringify({ status }),
      });
      await load();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to update ticket");
    } finally {
      setUpdatingId(null);
    }
  };

  return (
    <div>
      <h1 className="font-display text-2xl font-bold text-text mb-1">Support Tickets</h1>
      <p className="text-text-muted mb-8">All student support requests.</p>

      {error && (
        <div className="mb-6 px-4 py-3 rounded-md bg-coral-100 text-coral-600 text-sm">{error}</div>
      )}
      {tickets === null && !error && <p className="text-text-soft text-sm">Loading…</p>}
      {tickets !== null && tickets.length === 0 && (
        <p className="text-text-soft text-sm">No support tickets yet.</p>
      )}

      <div className="space-y-3">
        {tickets?.map((t) => (
          <div key={t.ticketId} className="bg-surface border border-border rounded-md p-4">
            <div className="flex items-center justify-between mb-1 gap-3">
              <div>
                <span className="text-sm font-semibold text-text">{t.subject}</span>
                <span className="text-xs text-text-soft ml-2">from {t.studentName}</span>
              </div>
              <select
                value={t.status}
                disabled={updatingId === t.ticketId}
                onChange={(e) => updateStatus(t.ticketId, e.target.value)}
                className={`px-2.5 py-1 rounded-pill text-xs font-semibold border-0 disabled:opacity-50 ${statusColor[t.status] ?? ""}`}
              >
                {statuses.map((s) => (
                  <option key={s} value={s}>
                    {s}
                  </option>
                ))}
              </select>
            </div>
            <p className="text-sm text-text-muted">{t.message}</p>
            <p className="text-xs text-text-soft mt-2">{new Date(t.createdAt).toLocaleString()}</p>
          </div>
        ))}
      </div>
    </div>
  );
}

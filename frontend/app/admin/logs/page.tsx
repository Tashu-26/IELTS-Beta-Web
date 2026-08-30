"use client";

import { useEffect, useState } from "react";
import { useAuth } from "@/lib/AuthContext";
import { apiFetch } from "@/lib/api";
import type { AdminLog } from "@/lib/types";

export default function AdminLogsPage() {
  const { session } = useAuth();
  const [logs, setLogs] = useState<AdminLog[] | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!session) return;
    apiFetch<AdminLog[]>("/api/admin/logs", session.access_token)
      .then(setLogs)
      .catch((err) => setError(err.message));
  }, [session]);

  return (
    <div>
      <h1 className="font-display text-2xl font-bold text-text mb-1">Admin Logs</h1>
      <p className="text-text-muted mb-8">Audit trail of admin actions.</p>

      {error && (
        <div className="mb-6 px-4 py-3 rounded-md bg-coral-100 text-coral-600 text-sm">{error}</div>
      )}
      {logs === null && !error && <p className="text-text-soft text-sm">Loading…</p>}
      {logs !== null && logs.length === 0 && <p className="text-text-soft text-sm">No actions logged yet.</p>}

      <div className="bg-surface border border-border rounded-lg divide-y divide-border-soft">
        {logs?.map((log) => (
          <div key={log.logId} className="px-5 py-3.5">
            <div className="flex items-center justify-between mb-0.5">
              <span className="text-sm font-semibold text-text">{log.action}</span>
              <span className="text-xs text-text-soft">{new Date(log.loggedAt).toLocaleString()}</span>
            </div>
            {log.details && <p className="text-sm text-text-muted">{log.details}</p>}
            <p className="text-xs text-text-soft mt-1">by {log.adminName}</p>
          </div>
        ))}
      </div>
    </div>
  );
}

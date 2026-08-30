"use client";

import { useEffect, useState } from "react";
import { useAuth } from "@/lib/AuthContext";
import { apiFetch } from "@/lib/api";
import type { AdminReport } from "@/lib/types";

export default function AdminDashboardPage() {
  const { session } = useAuth();
  const [report, setReport] = useState<AdminReport | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!session) return;
    apiFetch<AdminReport>("/api/admin/reports", session.access_token)
      .then(setReport)
      .catch((err) => setError(err.message));
  }, [session]);

  const cards = report
    ? [
        { label: "Total Users", value: report.totalUsers },
        { label: "Students", value: report.totalStudents },
        { label: "Teachers", value: report.totalTeachers },
        { label: "Courses", value: report.totalCourses },
        { label: "Enrollments", value: report.totalEnrollments },
        { label: "Open Support Tickets", value: report.openSupportTickets },
      ]
    : [];

  return (
    <div>
      <h1 className="font-display text-2xl font-bold text-text mb-1">Admin Dashboard</h1>
      <p className="text-text-muted mb-8">Platform overview.</p>

      {error && (
        <div className="mb-6 px-4 py-3 rounded-md bg-coral-100 text-coral-600 text-sm">{error}</div>
      )}
      {report === null && !error && <p className="text-text-soft text-sm">Loading…</p>}

      <div className="grid sm:grid-cols-3 gap-4">
        {cards.map((c) => (
          <div key={c.label} className="bg-surface border border-border rounded-lg p-5">
            <div className="text-xs text-text-soft mb-1">{c.label}</div>
            <div className="font-mono font-bold text-2xl text-text">{c.value}</div>
          </div>
        ))}
      </div>
    </div>
  );
}

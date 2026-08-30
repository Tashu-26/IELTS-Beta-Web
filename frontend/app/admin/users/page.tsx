"use client";

import { useEffect, useState } from "react";
import { useAuth } from "@/lib/AuthContext";
import { apiFetch } from "@/lib/api";
import type { AdminUser } from "@/lib/types";

const roleBadge: Record<string, string> = {
  Student: "bg-teal-100 text-teal-700",
  Teacher: "bg-green-100 text-green-600",
  Admin: "bg-purple-100 text-purple-500",
};

export default function AdminUsersPage() {
  const { session, profile } = useAuth();
  const [users, setUsers] = useState<AdminUser[] | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [updatingId, setUpdatingId] = useState<number | null>(null);

  const load = async () => {
    if (!session) return;
    try {
      const data = await apiFetch<AdminUser[]>("/api/admin/users", session.access_token);
      setUsers(data);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to load users");
    }
  };

  useEffect(() => {
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [session]);

  const changeRole = async (userId: number, role: string) => {
    if (!session) return;
    setUpdatingId(userId);
    setError(null);
    try {
      await apiFetch(`/api/admin/users/${userId}/role`, session.access_token, {
        method: "PUT",
        body: JSON.stringify({ role }),
      });
      await load();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to update role");
    } finally {
      setUpdatingId(null);
    }
  };

  const toggleStatus = async (userId: number, currentlyActive: boolean) => {
    if (!session) return;
    setUpdatingId(userId);
    setError(null);
    try {
      await apiFetch(`/api/admin/users/${userId}/status`, session.access_token, {
        method: "PUT",
        body: JSON.stringify({ isActive: !currentlyActive }),
      });
      await load();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to update status");
    } finally {
      setUpdatingId(null);
    }
  };

  return (
    <div>
      <h1 className="font-display text-2xl font-bold text-text mb-1">Users</h1>
      <p className="text-text-muted mb-8">Manage user roles and account status across the platform.</p>

      {error && (
        <div className="mb-6 px-4 py-3 rounded-md bg-coral-100 text-coral-600 text-sm">{error}</div>
      )}
      {users === null && !error && <p className="text-text-soft text-sm">Loading…</p>}

      <div className="bg-surface border border-border rounded-lg divide-y divide-border-soft">
        {users?.map((u) => (
          <div key={u.userId} className="flex items-center justify-between px-5 py-3.5 gap-4">
            <div>
              <div className="text-sm font-semibold text-text">
                {u.firstName} {u.lastName}
                {u.userId === profile?.userId && (
                  <span className="ml-2 text-xs text-text-soft">(you)</span>
                )}
                {!u.isActive && (
                  <span className="ml-2 px-2 py-0.5 rounded-pill bg-coral-100 text-coral-600 text-xs font-semibold">
                    Suspended
                  </span>
                )}
              </div>
              <div className="text-xs text-text-soft">{u.email}</div>
            </div>
            <div className="flex items-center gap-3">
              <span className={`px-2.5 py-0.5 rounded-pill text-xs font-semibold ${roleBadge[u.role] ?? ""}`}>
                {u.role}
              </span>
              <select
                value={u.role}
                disabled={updatingId === u.userId || u.userId === profile?.userId}
                onChange={(e) => changeRole(u.userId, e.target.value)}
                className="border border-border rounded-sm px-2 py-1 text-xs bg-surface text-text disabled:opacity-50"
              >
                <option value="Student">Student</option>
                <option value="Teacher">Teacher</option>
                <option value="Admin">Admin</option>
              </select>
              <button
                onClick={() => toggleStatus(u.userId, u.isActive)}
                disabled={updatingId === u.userId || u.userId === profile?.userId}
                className={`px-3 py-1 rounded-pill text-xs font-semibold border disabled:opacity-50 ${
                  u.isActive
                    ? "border-border text-text-muted hover:border-coral-500 hover:text-coral-600"
                    : "border-green-500 text-green-600"
                }`}
              >
                {u.isActive ? "Suspend" : "Reactivate"}
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

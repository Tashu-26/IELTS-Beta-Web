"use client";

import { useAuth } from "@/lib/AuthContext";

export default function AdminProfilePage() {
  const { profile, session } = useAuth();

  return (
    <div className="max-w-md">
      <h1 className="font-display text-2xl font-bold text-text mb-1">Profile</h1>
      <p className="text-text-muted mb-8">Your account information.</p>

      <div className="bg-surface border border-border rounded-lg p-6">
        <div className="flex items-center gap-4 mb-6">
          <div className="w-14 h-14 rounded-pill bg-navy-900 text-white flex items-center justify-center font-display font-bold text-xl">
            {(profile?.firstName || "?").charAt(0).toUpperCase()}
          </div>
          <div>
            <div className="font-display font-semibold text-text">
              {profile ? `${profile.firstName} ${profile.lastName}` : "Loading…"}
            </div>
            <div className="text-sm text-text-soft">{session?.user.email}</div>
          </div>
        </div>

        <dl className="text-sm">
          <div className="flex justify-between pb-2">
            <dt className="text-text-muted">Role</dt>
            <dd className="font-semibold text-text">{profile?.role ?? "—"}</dd>
          </div>
        </dl>
      </div>
    </div>
  );
}

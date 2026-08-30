"use client";

import { useState } from "react";
import { useAuth } from "@/lib/AuthContext";
import { apiFetch } from "@/lib/api";

export default function ProfilePage() {
  const { profile, session, refreshProfile } = useAuth();
  const [firstName, setFirstName] = useState(profile?.firstName ?? "");
  const [lastName, setLastName] = useState(profile?.lastName ?? "");
  const [editing, setEditing] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [saved, setSaved] = useState(false);

  const startEditing = () => {
    setFirstName(profile?.firstName ?? "");
    setLastName(profile?.lastName ?? "");
    setSaved(false);
    setError(null);
    setEditing(true);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!session) return;
    setSaving(true);
    setError(null);
    try {
      // PUT /api/me -- profile settings (first/last name). Works for any
      // role since the name lives on the shared Person row.
      await apiFetch("/api/me", session.access_token, {
        method: "PUT",
        body: JSON.stringify({ firstName, lastName }),
      });
      await refreshProfile();
      setSaved(true);
      setEditing(false);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to save profile");
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="max-w-md">
      <h1 className="font-display text-2xl font-bold text-text mb-1">Profile</h1>
      <p className="text-text-muted mb-8">Your account information.</p>

      <div className="bg-surface border border-border rounded-lg p-6">
        {saved && (
          <div className="mb-4 px-3 py-2 rounded-sm bg-green-100 text-green-600 text-sm">Saved!</div>
        )}
        {error && (
          <div className="mb-4 px-3 py-2 rounded-sm bg-coral-100 text-coral-600 text-sm">{error}</div>
        )}

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

        {editing ? (
          <form onSubmit={handleSubmit}>
            <label className="block text-sm font-medium text-text mb-1">First Name</label>
            <input
              value={firstName}
              onChange={(e) => setFirstName(e.target.value)}
              required
              className="w-full border border-border rounded-sm px-3 py-2 mb-4 bg-surface text-text"
            />
            <label className="block text-sm font-medium text-text mb-1">Last Name</label>
            <input
              value={lastName}
              onChange={(e) => setLastName(e.target.value)}
              required
              className="w-full border border-border rounded-sm px-3 py-2 mb-6 bg-surface text-text"
            />
            <div className="flex gap-2">
              <button
                type="submit"
                disabled={saving}
                className="flex-1 bg-teal-500 text-white font-semibold rounded-pill py-2.5 disabled:opacity-60"
              >
                {saving ? "Saving…" : "Save"}
              </button>
              <button
                type="button"
                onClick={() => setEditing(false)}
                className="flex-1 border border-border rounded-pill py-2.5 text-text"
              >
                Cancel
              </button>
            </div>
          </form>
        ) : (
          <>
            <dl className="space-y-3 text-sm">
              <div className="flex justify-between border-b border-border-soft pb-2">
                <dt className="text-text-muted">Role</dt>
                <dd className="font-semibold text-text">{profile?.role ?? "—"}</dd>
              </div>
              {profile?.role === "Student" && (
                <>
                  <div className="flex justify-between border-b border-border-soft pb-2">
                    <dt className="text-text-muted">Target Band</dt>
                    <dd className="font-semibold text-text">{profile?.targetBand?.toFixed(1) ?? "Not set"}</dd>
                  </div>
                  <div className="flex justify-between pb-2">
                    <dt className="text-text-muted">Current Band</dt>
                    <dd className="font-semibold text-text">{profile?.currentBand?.toFixed(1) ?? "Not set"}</dd>
                  </div>
                </>
              )}
            </dl>
            <button
              onClick={startEditing}
              className="mt-6 w-full border border-border rounded-pill py-2.5 text-text font-semibold"
            >
              Edit Profile
            </button>
          </>
        )}
      </div>
    </div>
  );
}

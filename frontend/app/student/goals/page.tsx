"use client";

import { useState } from "react";
import { useAuth } from "@/lib/AuthContext";
import { apiFetch } from "@/lib/api";

export default function GoalsPage() {
  const { session, profile, refreshProfile } = useAuth();
  const [targetBand, setTargetBand] = useState(profile?.targetBand?.toString() ?? "");
  const [currentBand, setCurrentBand] = useState(profile?.currentBand?.toString() ?? "");
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [saved, setSaved] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!session) return;
    setSaving(true);
    setError(null);
    setSaved(false);
    try {
      await apiFetch("/api/students/me/goal", session.access_token, {
        method: "PUT",
        body: JSON.stringify({
          targetBand: targetBand ? Number(targetBand) : null,
          currentBand: currentBand ? Number(currentBand) : null,
        }),
      });
      await refreshProfile();
      setSaved(true);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to save goal");
    } finally {
      setSaving(false);
    }
  };

  const bandOptions = Array.from({ length: 18 }, (_, i) => (i * 0.5).toFixed(1)).filter(
    (v) => Number(v) >= 0 && Number(v) <= 9
  );

  return (
    <div className="max-w-md">
      <h1 className="font-display text-2xl font-bold text-text mb-1">Your Goal</h1>
      <p className="text-text-muted mb-8">Set your target band and your current estimated band.</p>

      <form onSubmit={handleSubmit} className="bg-surface border border-border rounded-lg p-6">
        {error && (
          <div className="mb-4 px-3 py-2 rounded-sm bg-coral-100 text-coral-600 text-sm">{error}</div>
        )}
        {saved && (
          <div className="mb-4 px-3 py-2 rounded-sm bg-green-100 text-green-600 text-sm">Saved!</div>
        )}

        <label className="block text-sm font-medium text-text mb-1">Target Band</label>
        <select
          value={targetBand}
          onChange={(e) => setTargetBand(e.target.value)}
          className="w-full border border-border rounded-sm px-3 py-2 mb-4 bg-surface text-text"
        >
          <option value="">Not set</option>
          {bandOptions.map((v) => (
            <option key={v} value={v}>
              {v}
            </option>
          ))}
        </select>

        <label className="block text-sm font-medium text-text mb-1">Current Band (estimate)</label>
        <select
          value={currentBand}
          onChange={(e) => setCurrentBand(e.target.value)}
          className="w-full border border-border rounded-sm px-3 py-2 mb-6 bg-surface text-text"
        >
          <option value="">Not set</option>
          {bandOptions.map((v) => (
            <option key={v} value={v}>
              {v}
            </option>
          ))}
        </select>

        <button
          type="submit"
          disabled={saving}
          className="w-full bg-teal-500 text-white font-semibold rounded-pill py-2.5 disabled:opacity-60"
        >
          {saving ? "Saving…" : "Save Goal"}
        </button>
      </form>
    </div>
  );
}

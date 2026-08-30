"use client";

import { useEffect, useState } from "react";
import { useAuth } from "@/lib/AuthContext";
import { apiFetch } from "@/lib/api";
import type { GamificationSummary } from "@/lib/types";

export default function RewardsPage() {
  const { session } = useAuth();
  const [summary, setSummary] = useState<GamificationSummary | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!session) return;
    apiFetch<GamificationSummary>("/api/students/me/gamification", session.access_token)
      .then(setSummary)
      .catch((err) => setError(err.message));
  }, [session]);

  if (error) return <p className="text-coral-600 text-sm">{error}</p>;
  if (!summary) return <p className="text-text-soft text-sm">Loading…</p>;

  return (
    <div>
      <h1 className="font-display text-2xl font-bold text-text mb-1">Rewards</h1>
      <p className="text-text-muted mb-8">
        XP, streaks, and badges earned from things you actually do here — practicing, submitting
        work, and staying on top of your goals.
      </p>

      <div className="grid sm:grid-cols-4 gap-4 mb-10">
        <div className="bg-surface border border-border rounded-lg p-5 text-center">
          <div className="text-xs text-text-soft mb-1">Level</div>
          <div className="font-mono font-bold text-2xl text-text">{summary.level}</div>
        </div>
        <div className="bg-surface border border-border rounded-lg p-5 text-center">
          <div className="text-xs text-text-soft mb-1">XP</div>
          <div className="font-mono font-bold text-2xl text-teal-600">{summary.xp}</div>
        </div>
        <div className="bg-surface border border-border rounded-lg p-5 text-center">
          <div className="text-xs text-text-soft mb-1">Coins</div>
          <div className="font-mono font-bold text-2xl text-sun-500">🪙 {summary.coins}</div>
        </div>
        <div className="bg-surface border border-border rounded-lg p-5 text-center">
          <div className="text-xs text-text-soft mb-1">Streak</div>
          <div className="font-mono font-bold text-2xl text-coral-600">🔥 {summary.streak}</div>
        </div>
      </div>

      <h2 className="font-display text-lg font-semibold text-text mb-1">Today's missions</h2>
      <p className="text-xs text-text-soft mb-4">
        These complete automatically when you do the real thing — there's nothing to click here.
      </p>
      <div className="bg-surface border border-border rounded-lg divide-y divide-border-soft mb-10">
        {summary.missions.map((m) => (
          <div key={m.key} className="flex items-center justify-between px-5 py-3.5">
            <div className="flex items-center gap-3">
              <span
                className={`w-6 h-6 rounded-pill flex items-center justify-center text-xs ${
                  m.done ? "bg-green-100 text-green-600" : "bg-surface-alt text-text-soft"
                }`}
              >
                {m.done ? "✓" : ""}
              </span>
              <span className={`text-sm ${m.done ? "text-text-soft line-through" : "text-text font-medium"}`}>
                {m.title}
              </span>
            </div>
            <span className="text-xs font-mono text-text-soft">+{m.xpReward} XP</span>
          </div>
        ))}
      </div>

      <h2 className="font-display text-lg font-semibold text-text mb-4">Achievements</h2>
      <div className="grid sm:grid-cols-3 gap-3">
        {summary.achievements.map((a) => (
          <div
            key={a.key}
            className={`rounded-lg p-4 text-center border ${
              a.unlocked
                ? "bg-surface border-teal-500"
                : "bg-surface-alt border-border-soft opacity-50 grayscale"
            }`}
          >
            <div className="text-3xl mb-2">{a.icon}</div>
            <div className="text-xs font-medium text-text">{a.title}</div>
          </div>
        ))}
      </div>
    </div>
  );
}

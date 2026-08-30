"use client";

import { useAuth } from "@/lib/AuthContext";

export default function TeacherTopbar() {
  const { profile, signOut } = useAuth();

  return (
    <header className="flex items-center justify-between px-6 py-4 border-b border-border-soft bg-surface md:bg-transparent md:border-0">
      <div className="md:hidden font-display font-bold text-text">IELTS Beta</div>
      <div className="hidden md:block" />
      <div className="flex items-center gap-3">
        <div className="text-right leading-tight hidden sm:block">
          <div className="text-sm font-semibold text-text">{profile?.firstName ?? "..."}</div>
          <div className="text-xs text-text-soft">Teacher</div>
        </div>
        <div className="w-9 h-9 rounded-pill bg-navy-900 text-white flex items-center justify-center font-display font-semibold text-sm">
          {(profile?.firstName || "?").charAt(0).toUpperCase()}
        </div>
        <button
          onClick={signOut}
          className="px-3 py-2 rounded-pill text-sm font-semibold text-text-muted border border-border hover:border-coral-500 hover:text-coral-600 transition-colors"
        >
          Log out
        </button>
      </div>
    </header>
  );
}

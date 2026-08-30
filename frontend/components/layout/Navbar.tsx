"use client";

import Link from "next/link";
import Image from "next/image";
import { useAuth } from "@/lib/AuthContext";
import ThemeToggle from "./ThemeToggle";

export default function Navbar() {
  const { session, profile, loading, signOut } = useAuth();

  return (
    <header className="sticky top-0 z-40 bg-surface/90 backdrop-blur border-b border-border-soft">
      <div className="max-w-6xl mx-auto px-5 h-16 flex items-center justify-between gap-6">
        <Link href="/" className="flex items-center gap-2 shrink-0">
          <Image src="/logo.png" alt="IELTS Beta" width={36} height={32} className="h-8 w-auto" priority />
          <span className="font-display font-bold text-text text-lg tracking-tight">IELTS Beta</span>
        </Link>

        <nav className="hidden md:flex items-center gap-7 text-sm font-medium text-text-muted">
          <Link href="/" className="hover:text-text transition-colors">Home</Link>
          <Link href="/study" className="hover:text-text transition-colors">Study IELTS</Link>
          <Link href="/pricing" className="hover:text-text transition-colors">Pricing</Link>
        </nav>

        <div className="flex items-center gap-3">
          <ThemeToggle />

          {!loading && !session && (
            <>
              <Link
                href="/login"
                className="hidden sm:inline-flex px-4 py-2 rounded-pill text-sm font-semibold text-text hover:bg-surface-alt transition-colors"
              >
                Log in
              </Link>
              <Link
                href="/register"
                className="inline-flex px-4 py-2 rounded-pill text-sm font-semibold text-white bg-teal-500 hover:bg-teal-600 transition-colors"
              >
                Get Started
              </Link>
            </>
          )}

          {!loading && session && (
            <div className="flex items-center gap-2">
              {profile?.role === "Student" && (
                <Link
                  href="/student/dashboard"
                  className="hidden sm:inline-flex px-4 py-2 rounded-pill text-sm font-semibold text-teal-600 hover:bg-teal-100 transition-colors"
                >
                  Dashboard
                </Link>
              )}
              {profile?.role === "Teacher" && (
                <Link
                  href="/teacher/dashboard"
                  className="hidden sm:inline-flex px-4 py-2 rounded-pill text-sm font-semibold text-teal-600 hover:bg-teal-100 transition-colors"
                >
                  Teacher Portal
                </Link>
              )}
              {profile?.role === "Admin" && (
                <Link
                  href="/admin/dashboard"
                  className="hidden sm:inline-flex px-4 py-2 rounded-pill text-sm font-semibold text-teal-600 hover:bg-teal-100 transition-colors"
                >
                  Admin Panel
                </Link>
              )}
              <div className="hidden sm:flex flex-col items-end leading-tight">
                <span className="text-sm font-semibold text-text">
                  {profile ? profile.firstName : session.user.email}
                </span>
                {profile && (
                  <span className="text-xs text-text-soft">{profile.role}</span>
                )}
              </div>
              <div className="w-9 h-9 rounded-pill bg-navy-900 text-white flex items-center justify-center font-display font-semibold text-sm">
                {(profile?.firstName || session.user.email || "?").charAt(0).toUpperCase()}
              </div>
              <button
                onClick={signOut}
                className="px-3 py-2 rounded-pill text-sm font-semibold text-text-muted border border-border hover:border-coral-500 hover:text-coral-600 transition-colors"
              >
                Log out
              </button>
            </div>
          )}
        </div>
      </div>
    </header>
  );
}

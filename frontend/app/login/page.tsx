"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import Image from "next/image";
import { supabase } from "@/lib/supabaseClient";

export default function LoginPage() {
  const router = useRouter();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      const { error: signInError } = await supabase.auth.signInWithPassword({ email, password });
      if (signInError) throw signInError;
      router.push("/");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Invalid email or password");
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="min-h-screen flex items-center justify-center bg-bg px-4">
      <form
        onSubmit={handleSubmit}
        className="w-full max-w-md bg-surface border border-border rounded-lg shadow-md p-8"
      >
        <Image src="/logo.png" alt="IELTS Beta" width={140} height={40} className="h-9 w-auto mb-6" />
        <h1 className="font-display text-2xl font-bold text-text mb-1">Welcome back</h1>
        <p className="text-text-muted text-sm mb-6">Log in to continue your IELTS prep.</p>

        {error && (
          <div className="mb-4 px-3 py-2 rounded-sm bg-coral-100 text-coral-600 text-sm">{error}</div>
        )}

        <label className="block text-sm font-medium text-text mb-1">Email</label>
        <input
          type="email"
          className="w-full border border-border rounded-sm px-3 py-2 mb-4 bg-surface text-text"
          placeholder="you@example.com"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />

        <label className="block text-sm font-medium text-text mb-1">Password</label>
        <input
          type="password"
          className="w-full border border-border rounded-sm px-3 py-2 mb-6 bg-surface text-text"
          placeholder="••••••••"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />

        <button
          type="submit"
          disabled={loading}
          className="w-full bg-teal-500 text-white font-semibold rounded-pill py-2.5 disabled:opacity-60"
        >
          {loading ? "Logging in…" : "Log in"}
        </button>

        <p className="text-sm text-text-muted text-center mt-4">
          Don&apos;t have an account?{" "}
          <Link href="/register" className="text-teal-600 font-medium">
            Sign up
          </Link>
        </p>
      </form>
    </main>
  );
}

"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";
import Image from "next/image";
import { supabase } from "@/lib/supabaseClient";

export default function RegisterPage() {
  const router = useRouter();
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [info, setInfo] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setInfo(null);
    setLoading(true);
    try {
      const { data, error: signUpError } = await supabase.auth.signUp({
        email,
        password,
        options: {
          data: { first_name: firstName, last_name: lastName },
        },
      });
      if (signUpError) throw signUpError;

      if (data.session) {
        // Email confirmation is off for this project -- session exists
        // immediately, and AuthContext creates the backend profile as
        // soon as it sees this session.
        router.push("/");
      } else {
        // Email confirmation is required -- profile is created
        // automatically on first login after confirming.
        setInfo("Account created. Check your email to confirm it, then log in.");
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : "Something went wrong");
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
        <h1 className="font-display text-2xl font-bold text-text mb-1">Create your account</h1>
        <p className="text-text-muted text-sm mb-6">Start your IELTS journey with a free account.</p>

        {error && (
          <div className="mb-4 px-3 py-2 rounded-sm bg-coral-100 text-coral-600 text-sm">{error}</div>
        )}
        {info && (
          <div className="mb-4 px-3 py-2 rounded-sm bg-green-100 text-green-600 text-sm">{info}</div>
        )}

        <label className="block text-sm font-medium text-text mb-1">Full name</label>
        <div className="flex gap-2 mb-4">
          <input
            className="w-1/2 border border-border rounded-sm px-3 py-2 bg-surface text-text"
            placeholder="First name"
            value={firstName}
            onChange={(e) => setFirstName(e.target.value)}
            required
          />
          <input
            className="w-1/2 border border-border rounded-sm px-3 py-2 bg-surface text-text"
            placeholder="Last name"
            value={lastName}
            onChange={(e) => setLastName(e.target.value)}
            required
          />
        </div>

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
          placeholder="At least 6 characters"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          minLength={6}
          required
        />

        <button
          type="submit"
          disabled={loading}
          className="w-full bg-teal-500 text-white font-semibold rounded-pill py-2.5 disabled:opacity-60"
        >
          {loading ? "Creating account…" : "Create account"}
        </button>

        <p className="text-sm text-text-muted text-center mt-4">
          Already have an account?{" "}
          <Link href="/login" className="text-teal-600 font-medium">
            Log in
          </Link>
        </p>
      </form>
    </main>
  );
}

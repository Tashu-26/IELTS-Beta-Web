"use client";

import { createContext, useContext, useEffect, useState } from "react";
import type { Session } from "@supabase/supabase-js";
import { supabase } from "./supabaseClient";

export type Role = "Student" | "Teacher" | "Admin";

export type Profile = {
  userId: number;
  email: string;
  role: Role;
  firstName: string;
  lastName: string;
  studentId?: number;
  targetBand?: number;
  currentBand?: number;
  teacherId?: number;
  specialization?: string;
  adminId?: number;
};

type AuthContextValue = {
  session: Session | null;
  profile: Profile | null;
  loading: boolean;
  signOut: () => Promise<void>;
  refreshProfile: () => Promise<void>;
};

const AuthContext = createContext<AuthContextValue>({
  session: null,
  profile: null,
  loading: true,
  signOut: async () => {},
  refreshProfile: async () => {},
});

const API_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080";

/**
 * Makes sure a backend profile (person/users/students rows) exists for the
 * given session, creating it if this is the first time we've seen this
 * account authenticated. Works regardless of whether Supabase's "confirm
 * email" setting is on or off -- it just runs the first time a valid
 * session shows up here, whenever that is.
 */
async function ensureProfile(session: Session): Promise<Profile | null> {
  const accessToken = session.access_token;

  let res = await fetch(`${API_URL}/api/me`, {
    headers: { Authorization: `Bearer ${accessToken}` },
  });

  if (res.status === 404) {
    const meta = (session.user.user_metadata || {}) as Record<string, string>;
    await fetch(`${API_URL}/api/auth/complete-profile`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${accessToken}`,
      },
      body: JSON.stringify({
        firstName: meta.first_name || "New",
        lastName: meta.last_name || "Student",
      }),
    });
    res = await fetch(`${API_URL}/api/me`, {
      headers: { Authorization: `Bearer ${accessToken}` },
    });
  }

  if (res.ok) {
    return (await res.json()) as Profile;
  }
  return null;
}

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [session, setSession] = useState<Session | null>(null);
  const [profile, setProfile] = useState<Profile | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let isMounted = true;

    supabase.auth.getSession().then(async ({ data }) => {
      if (!isMounted) return;
      setSession(data.session);
      if (data.session) {
        const p = await ensureProfile(data.session);
        if (isMounted) setProfile(p);
      }
      if (isMounted) setLoading(false);
    });

    const { data: listener } = supabase.auth.onAuthStateChange(async (_event, newSession) => {
      if (!isMounted) return;
      setSession(newSession);
      if (newSession) {
        const p = await ensureProfile(newSession);
        if (isMounted) setProfile(p);
      } else {
        setProfile(null);
      }
    });

    return () => {
      isMounted = false;
      listener.subscription.unsubscribe();
    };
  }, []);

  const signOut = async () => {
    await supabase.auth.signOut();
    setProfile(null);
  };

  /** Re-fetches /api/me. Used after actions that change profile data, like updating goals. */
  const refreshProfile = async () => {
    if (!session) return;
    const p = await ensureProfile(session);
    setProfile(p);
  };

  return (
    <AuthContext.Provider value={{ session, profile, loading, signOut, refreshProfile }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}

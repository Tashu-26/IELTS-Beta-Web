"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { useAuth, Role } from "@/lib/AuthContext";

export default function AuthGuard({
  children,
  allowedRoles,
}: {
  children: React.ReactNode;
  allowedRoles?: Role[];
}) {
  const { session, profile, loading } = useAuth();
  const router = useRouter();

  const roleMismatch = Boolean(allowedRoles && profile && !allowedRoles.includes(profile.role));

  useEffect(() => {
    if (loading) return;
    if (!session) {
      router.replace("/login");
      return;
    }
    if (roleMismatch) {
      router.replace("/");
    }
  }, [loading, session, roleMismatch, router]);

  if (loading || !session || roleMismatch) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-bg">
        <p className="text-text-muted">Loading…</p>
      </div>
    );
  }

  return <>{children}</>;
}

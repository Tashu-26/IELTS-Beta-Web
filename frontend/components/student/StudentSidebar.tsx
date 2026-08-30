"use client";

import Link from "next/link";
import Image from "next/image";
import { usePathname } from "next/navigation";

const links = [
  { href: "/student/dashboard", label: "Dashboard", icon: "🏠" },
  { href: "/student/courses", label: "Courses", icon: "📚" },
  { href: "/student/progress", label: "Progress", icon: "📈" },
  { href: "/student/rewards", label: "Rewards", icon: "🏆" },
  { href: "/student/submissions", label: "Submissions", icon: "📝" },
  { href: "/student/goals", label: "Goals", icon: "🎯" },
  { href: "/student/support", label: "Support", icon: "💬" },
  { href: "/student/profile", label: "Profile", icon: "👤" },
];

export default function StudentSidebar() {
  const pathname = usePathname();

  return (
    <aside className="hidden md:flex flex-col w-60 shrink-0 border-r border-border-soft bg-surface min-h-screen py-6 px-4">
      <Link href="/" className="flex items-center gap-2 px-2 mb-8">
        <Image src="/logo.png" alt="IELTS Beta" width={32} height={28} className="h-7 w-auto" />
        <span className="font-display font-bold text-text">IELTS Beta</span>
      </Link>

      <nav className="flex flex-col gap-1">
        {links.map((link) => {
          const active = pathname === link.href || pathname.startsWith(link.href + "/");
          return (
            <Link
              key={link.href}
              href={link.href}
              className={`flex items-center gap-3 px-3 py-2.5 rounded-md text-sm font-medium transition-colors ${
                active
                  ? "bg-teal-100 text-teal-700"
                  : "text-text-muted hover:bg-surface-alt hover:text-text"
              }`}
            >
              <span>{link.icon}</span>
              {link.label}
            </Link>
          );
        })}
      </nav>
    </aside>
  );
}

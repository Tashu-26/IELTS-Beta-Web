import Link from "next/link";
import Image from "next/image";

export default function Footer() {
  return (
    <footer className="border-t border-border-soft bg-surface-alt">
      <div className="max-w-6xl mx-auto px-5 py-14 grid grid-cols-2 md:grid-cols-4 gap-8">
        <div className="col-span-2">
          <div className="flex items-center gap-2 mb-3">
            <Image src="/logo.png" alt="IELTS Beta" width={32} height={28} className="h-7 w-auto" />
            <span className="font-display font-bold text-text">IELTS Beta</span>
          </div>
          <p className="text-sm text-text-muted max-w-xs">
            Personalized IELTS preparation — Listening, Reading, Writing and Speaking practice,
            progress tracking, and live teacher support in one place.
          </p>
        </div>

        <div>
          <h4 className="font-display font-semibold text-text text-sm mb-3">Product</h4>
          <ul className="space-y-2 text-sm text-text-muted">
            <li><Link href="/study" className="hover:text-text">Study IELTS</Link></li>
            <li><Link href="/pricing" className="hover:text-text">Pricing</Link></li>
            <li><Link href="/register" className="hover:text-text">Get Started</Link></li>
          </ul>
        </div>

        <div>
          <h4 className="font-display font-semibold text-text text-sm mb-3">Account</h4>
          <ul className="space-y-2 text-sm text-text-muted">
            <li><Link href="/login" className="hover:text-text">Log in</Link></li>
            <li><Link href="/register" className="hover:text-text">Create account</Link></li>
          </ul>
        </div>
      </div>

      <div className="border-t border-border-soft">
        <div className="max-w-6xl mx-auto px-5 py-5 text-xs text-text-soft">
          © {new Date().getFullYear()} IELTS Beta. Built as a university project — not affiliated with IDP/British Council/Cambridge.
        </div>
      </div>
    </footer>
  );
}

import Link from "next/link";

const skills = [
  { icon: "🎧", name: "Listening", desc: "4 sections, real exam audio, instant scoring." },
  { icon: "📖", name: "Reading", desc: "Academic & General passages with all official question types." },
  { icon: "✍️", name: "Writing", desc: "Task 1 & Task 2 prompts, graded by a real teacher." },
  { icon: "🗣️", name: "Speaking", desc: "Part 1–3 practice with recorded submissions and band feedback." },
];

const highlights = [
  {
    title: "Track every skill separately",
    desc: "See your Listening, Reading, Writing and Speaking bands move independently, not just one overall number.",
  },
  {
    title: "Real teacher feedback",
    desc: "Writing and Speaking submissions are graded by an actual teacher, with written feedback attached to every band score.",
  },
  {
    title: "Live classes, not just recordings",
    desc: "Join scheduled live sessions with your course's teacher alongside self-paced practice.",
  },
];

export default function HomePage() {
  return (
    <>
      {/* Hero */}
      <section className="relative overflow-hidden">
        <div className="absolute inset-0 bg-gradient-to-br from-teal-100 via-bg to-bg -z-10" />
        <div className="max-w-6xl mx-auto px-5 pt-16 pb-20 md:pt-24 md:pb-28 grid md:grid-cols-2 gap-12 items-center">
          <div>
            <span className="inline-block px-3 py-1 rounded-pill bg-teal-100 text-teal-700 text-xs font-semibold mb-5">
              Personalized IELTS Prep
            </span>
            <h1 className="font-display text-4xl md:text-5xl font-bold text-text leading-tight mb-5">
              Practice smarter. Track your progress. Reach your{" "}
              <span className="bg-gradient-to-r from-teal-500 to-green-500 bg-clip-text text-transparent">
                target band
              </span>.
            </h1>
            <p className="text-text-muted text-lg mb-8 max-w-md">
              IELTS Beta brings Listening, Reading, Writing and Speaking practice together with
              personalized progress tracking, goals and real teacher feedback.
            </p>
            <div className="flex flex-wrap gap-3 mb-8">
              <Link
                href="/register"
                className="inline-flex items-center gap-2 px-6 py-3 rounded-pill bg-teal-500 text-white font-semibold hover:bg-teal-600 transition-colors"
              >
                Start Practicing Free
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round"><path d="M5 12h14M13 6l6 6-6 6" /></svg>
              </Link>
              <Link
                href="/study"
                className="inline-flex items-center px-6 py-3 rounded-pill border border-border text-text font-semibold hover:bg-surface-alt transition-colors"
              >
                Explore Practice
              </Link>
            </div>
            <div className="flex items-center gap-2 text-sm text-text-muted">
              <span className="flex gap-1">
                <span className="w-2 h-2 rounded-full bg-teal-500" />
                <span className="w-2 h-2 rounded-full bg-green-500" />
                <span className="w-2 h-2 rounded-full bg-navy-700" />
              </span>
              Built for both IELTS Academic &amp; General Training
            </div>
          </div>

          {/* Dashboard preview mockup — illustrative only, not live data */}
          <div className="relative">
            <div className="bg-surface border border-border rounded-lg shadow-lg p-6">
              <div className="flex items-center justify-between mb-5">
                <div>
                  <div className="text-xs text-text-soft">Good evening 👋</div>
                  <div className="font-display font-semibold text-text">Welcome back, Tasnia</div>
                </div>
                <div className="w-9 h-9 rounded-pill bg-navy-900 text-white flex items-center justify-center font-display font-semibold text-sm">
                  T
                </div>
              </div>

              <div className="grid grid-cols-4 gap-3 mb-5">
                <div className="text-center">
                  <div className="font-mono font-bold text-lg text-text">6.5</div>
                  <div className="text-[11px] text-text-soft">Target</div>
                </div>
                <div className="text-center">
                  <div className="font-mono font-bold text-lg text-teal-600">6.5</div>
                  <div className="text-[11px] text-text-soft">Current</div>
                </div>
                <div className="text-center">
                  <div className="font-mono font-bold text-lg text-sun-500">🔥12</div>
                  <div className="text-[11px] text-text-soft">Streak</div>
                </div>
                <div className="text-center">
                  <div className="font-mono font-bold text-lg text-text">4</div>
                  <div className="text-[11px] text-text-soft">Tests</div>
                </div>
              </div>

              <div className="flex flex-wrap gap-2">
                <span className="px-3 py-1 rounded-pill bg-teal-100 text-teal-700 text-xs font-semibold">🎧 L 6.5</span>
                <span className="px-3 py-1 rounded-pill bg-green-100 text-green-600 text-xs font-semibold">📖 R 6.0</span>
                <span className="px-3 py-1 rounded-pill bg-teal-100 text-teal-700 text-xs font-semibold">✍️ W 6.5</span>
                <span className="px-3 py-1 rounded-pill bg-green-100 text-green-600 text-xs font-semibold">🗣️ S 7.0</span>
              </div>
            </div>

            <div className="hidden sm:flex absolute -top-4 -right-4 items-center gap-2 bg-surface border border-border rounded-lg shadow-md px-4 py-3">
              <span className="w-8 h-8 rounded-md bg-green-100 text-green-600 flex items-center justify-center">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round"><path d="M20 6 9 17l-5-5" /></svg>
              </span>
              <div className="text-xs">
                <div className="font-semibold text-text">Goal achieved 🎯</div>
                <div className="text-text-soft">Band 6.5 reached</div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Four skills */}
      <section className="max-w-6xl mx-auto px-5 py-16 md:py-20">
        <div className="text-center mb-12">
          <h2 className="font-display text-3xl font-bold text-text mb-3">Everything IELTS, in one place</h2>
          <p className="text-text-muted max-w-xl mx-auto">
            Structured practice across all four skills, with real question types and real feedback.
          </p>
        </div>
        <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-5">
          {skills.map((s) => (
            <div
              key={s.name}
              className="bg-surface border border-border rounded-lg p-6 hover:shadow-md hover:-translate-y-0.5 transition-all"
            >
              <div className="text-3xl mb-3">{s.icon}</div>
              <h3 className="font-display font-semibold text-text mb-1.5">{s.name}</h3>
              <p className="text-sm text-text-muted">{s.desc}</p>
            </div>
          ))}
        </div>
      </section>

      {/* Highlights */}
      <section className="bg-surface-alt border-y border-border-soft">
        <div className="max-w-6xl mx-auto px-5 py-16 md:py-20 grid md:grid-cols-3 gap-8">
          {highlights.map((h) => (
            <div key={h.title}>
              <h3 className="font-display font-semibold text-text text-lg mb-2">{h.title}</h3>
              <p className="text-sm text-text-muted">{h.desc}</p>
            </div>
          ))}
        </div>
      </section>

      {/* CTA */}
      <section className="max-w-6xl mx-auto px-5 py-16 md:py-20 text-center">
        <h2 className="font-display text-3xl font-bold text-text mb-3">
          Ready to reach your target band?
        </h2>
        <p className="text-text-muted mb-8 max-w-md mx-auto">
          Create a free account and start practicing today — no credit card required.
        </p>
        <Link
          href="/register"
          className="inline-flex px-7 py-3 rounded-pill bg-teal-500 text-white font-semibold hover:bg-teal-600 transition-colors"
        >
          Get Started Free
        </Link>
      </section>
    </>
  );
}

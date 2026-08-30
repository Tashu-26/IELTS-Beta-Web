import Link from "next/link";

const plans = [
  {
    name: "Free",
    price: "$0",
    period: "forever",
    description: "Everything you need to start practicing.",
    features: [
      "Access to all four skills",
      "Unlimited self-practice questions",
      "Progress tracking & goals",
      "Join courses with live classes",
    ],
    cta: "Get Started Free",
    highlighted: false,
  },
  {
    name: "Pro",
    price: "$9",
    period: "/ month",
    description: "For learners who want graded feedback.",
    features: [
      "Everything in Free",
      "Writing submissions graded by a teacher",
      "Speaking submissions graded by a teacher",
      "Priority support",
    ],
    cta: "Get Started",
    highlighted: true,
  },
];

export default function PricingPage() {
  return (
    <div className="max-w-4xl mx-auto px-5 py-14 md:py-20">
      <div className="text-center mb-12">
        <h1 className="font-display text-3xl md:text-4xl font-bold text-text mb-3">
          Simple, transparent pricing
        </h1>
        <p className="text-text-muted max-w-md mx-auto">
          Start for free. Upgrade when you want graded feedback on Writing and Speaking.
        </p>
      </div>

      <div className="grid sm:grid-cols-2 gap-6">
        {plans.map((plan) => (
          <div
            key={plan.name}
            className={`rounded-lg p-8 border ${
              plan.highlighted
                ? "border-teal-500 bg-surface shadow-lg relative"
                : "border-border bg-surface"
            }`}
          >
            {plan.highlighted && (
              <span className="absolute -top-3 left-8 px-3 py-1 rounded-pill bg-teal-500 text-white text-xs font-semibold">
                Most popular
              </span>
            )}
            <h2 className="font-display font-bold text-xl text-text mb-1">{plan.name}</h2>
            <p className="text-sm text-text-muted mb-4">{plan.description}</p>
            <div className="flex items-baseline gap-1 mb-6">
              <span className="font-display font-bold text-3xl text-text">{plan.price}</span>
              <span className="text-text-soft text-sm">{plan.period}</span>
            </div>
            <ul className="space-y-2.5 mb-8">
              {plan.features.map((f) => (
                <li key={f} className="flex items-start gap-2 text-sm text-text-muted">
                  <svg className="mt-0.5 shrink-0" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                    <path d="M20 6 9 17l-5-5" className="text-green-500" stroke="currentColor" />
                  </svg>
                  {f}
                </li>
              ))}
            </ul>
            <Link
              href="/register"
              className={`block text-center px-5 py-2.5 rounded-pill font-semibold text-sm transition-colors ${
                plan.highlighted
                  ? "bg-teal-500 text-white hover:bg-teal-600"
                  : "border border-border text-text hover:bg-surface-alt"
              }`}
            >
              {plan.cta}
            </Link>
          </div>
        ))}
      </div>
    </div>
  );
}

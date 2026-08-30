import type { Config } from "tailwindcss";

const config: Config = {
  darkMode: ["class", '[data-theme="dark"]'],
  content: [
    "./app/**/*.{ts,tsx}",
    "./components/**/*.{ts,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        bg: "var(--bg)",
        surface: "var(--surface)",
        "surface-alt": "var(--surface-alt)",
        "surface-sunken": "var(--surface-sunken)",
        border: "var(--border)",
        "border-soft": "var(--border-soft)",

        navy: {
          950: "var(--navy-950)",
          900: "var(--navy-900)",
          800: "var(--navy-800)",
          700: "var(--navy-700)",
          600: "var(--navy-600)",
        },
        teal: {
          700: "var(--teal-700)",
          600: "var(--teal-600)",
          500: "var(--teal-500)",
          400: "var(--teal-400)",
          100: "var(--teal-100)",
        },
        green: {
          600: "var(--green-600)",
          500: "var(--green-500)",
          400: "var(--green-400)",
          100: "var(--green-100)",
        },
        amber: { 500: "var(--amber-500)" },
        coral: {
          600: "var(--coral-600)",
          500: "var(--coral-500)",
          100: "var(--coral-100)",
        },
        sun: {
          500: "var(--sun-500)",
          400: "var(--sun-400)",
          100: "var(--sun-100)",
        },
        purple: {
          500: "var(--purple-500)",
          100: "var(--purple-100)",
        },

        text: {
          DEFAULT: "var(--text)",
          muted: "var(--text-muted)",
          soft: "var(--text-soft)",
          ondark: "var(--text-on-dark)",
          "ondark-muted": "var(--text-on-dark-muted)",
        },
      },
      fontFamily: {
        display: ["Sora", "sans-serif"],
        body: ["Inter", "sans-serif"],
        mono: ["IBM Plex Mono", "monospace"],
      },
      borderRadius: {
        sm: "10px",
        md: "16px",
        lg: "24px",
        pill: "999px",
      },
      boxShadow: {
        sm: "0 1px 2px rgba(15,42,74,.06), 0 1px 1px rgba(15,42,74,.04)",
        md: "0 12px 28px -8px rgba(15,42,74,.16)",
        lg: "0 28px 60px -12px rgba(15,42,74,.24)",
      },
    },
  },
  plugins: [],
};

export default config;

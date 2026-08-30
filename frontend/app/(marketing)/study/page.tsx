"use client";

import { useState } from "react";

type Skill = "Listening" | "Reading" | "Writing" | "Speaking";

const questionTypes: Record<Skill, string[]> = {
  Listening: [
    "Form Completion",
    "Note Completion",
    "Table Completion",
    "Multiple Choice",
    "Matching",
    "Plan/Map/Diagram Labelling",
    "Sentence Completion",
    "Short Answer",
  ],
  Reading: [
    "True/False/Not Given",
    "Yes/No/Not Given",
    "Matching Headings",
    "Matching Information",
    "Matching Features",
    "Sentence Completion",
    "Summary Completion",
    "Multiple Choice",
    "Short Answer",
  ],
  Writing: [
    "Task 1: Line Graph",
    "Task 1: Bar Chart",
    "Task 1: Pie Chart",
    "Task 1: Process Diagram",
    "Task 1: Maps",
    "Task 1: Letter",
    "Task 2: Opinion Essay",
    "Task 2: Discussion Essay",
    "Task 2: Problem/Solution",
  ],
  Speaking: ["Part 1: Introduction", "Part 2: Cue Card", "Part 3: Discussion", "Follow-up Questions"],
};

const skillIcons: Record<Skill, string> = {
  Listening: "🎧",
  Reading: "📖",
  Writing: "✍️",
  Speaking: "🗣️",
};

const skills: Skill[] = ["Listening", "Reading", "Writing", "Speaking"];

export default function StudyPage() {
  const [activeSkill, setActiveSkill] = useState<Skill>("Listening");

  return (
    <div className="max-w-5xl mx-auto px-5 py-14 md:py-20">
      <div className="text-center mb-10">
        <h1 className="font-display text-3xl md:text-4xl font-bold text-text mb-3">Study IELTS</h1>
        <p className="text-text-muted max-w-xl mx-auto">
          Every official IELTS question type, organized by skill, so you know exactly what to expect
          on test day.
        </p>
      </div>

      <div className="flex justify-center gap-2 flex-wrap mb-10">
        {skills.map((skill) => (
          <button
            key={skill}
            onClick={() => setActiveSkill(skill)}
            className={`px-5 py-2.5 rounded-pill text-sm font-semibold transition-colors ${
              activeSkill === skill
                ? "bg-teal-500 text-white"
                : "bg-surface border border-border text-text-muted hover:border-teal-500"
            }`}
          >
            {skillIcons[skill]} {skill}
          </button>
        ))}
      </div>

      <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-3">
        {questionTypes[activeSkill].map((type, i) => (
          <div
            key={type}
            className="bg-surface border border-border rounded-md p-4 flex items-center gap-3 hover:border-teal-500 hover:-translate-y-0.5 transition-all"
          >
            <span className="font-mono text-xs text-text-soft font-bold">
              {String(i + 1).padStart(2, "0")}
            </span>
            <span className="text-sm font-semibold text-text">{type}</span>
          </div>
        ))}
      </div>
    </div>
  );
}

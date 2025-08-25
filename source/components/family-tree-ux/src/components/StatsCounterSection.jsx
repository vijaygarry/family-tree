import React from "react";
import AnimatedCounter from "./AnimatedCounter";

const stats = [
  { value: 120, label: "Number of registered families" },
  { value: 579, label: "Number of registered Members" },
  { value: 125, label: "Registered Users" },
  { value: 300, label: "Number of Males" },
  { value: 279, label: "Number of Females" },
  { value: 120, label: "Kids (Under 15)" },
  { value: 25,  label: "Single Girls (Age Between 22 to 30)" },
  { value: 30,  label: "Single Boys (Age Between 22 to 30)" },
];

export default function StatsCounterSection() {
  return (
    <div
      style={{
        display: "grid",
        gridTemplateColumns: "repeat(auto-fill, minmax(220px, 1fr))",
        gap: "24px",
        textAlign: "center",
        padding: "24px",
      }}
    >
      {stats.map((s, i) => (
        <div
          key={i}
          style={{
            padding: "16px",
            borderRadius: "16px",
            boxShadow: "0 6px 20px rgba(0,0,0,0.08)",
            background: "#fff",
          }}
        >
          <div style={{ fontSize: 40, fontWeight: 800, color: "#A42502" }}>
            <AnimatedCounter
              target={s.value}
              duration={1500}
              startOnView={true}
              once={true}
              suffix=""         // e.g., "+" if you want "579+"
              decimals={0}
            />
          </div>
          <div style={{ marginTop: 8, color: "#000", fontWeight:600 }}>{s.label}</div>
        </div>
      ))}
    </div>
  );
}

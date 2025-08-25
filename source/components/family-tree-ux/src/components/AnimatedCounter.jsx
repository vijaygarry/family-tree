import React, { useEffect, useRef, useState } from "react";

function easeOutQuad(t) {
  return t * (2 - t);
}

export default function AnimatedCounter({
  target = 100,
  duration = 1500,        // ms
  startOnView = true,     // start when visible
  once = true,            // animate only once when visible
  prefix = "",
  suffix = "",
  decimals = 0,
  className = "",
  formatter,              // custom formatter function(number) => string
}) {
  const [value, setValue] = useState(0);
  const [hasAnimated, setHasAnimated] = useState(false);
  const ref = useRef(null);
  const rafRef = useRef(null);

  const animate = () => {
    const startTime = performance.now();
    const from = 0;
    const to = Number(target);

    const tick = (now) => {
      const elapsed = now - startTime;
      const t = Math.min(1, elapsed / duration);
      const eased = easeOutQuad(t);
      const next = from + (to - from) * eased;

      setValue(next);

      if (t < 1) {
        rafRef.current = requestAnimationFrame(tick);
      } else {
        setValue(to);
        setHasAnimated(true);
      }
    };

    cancelAnimationFrame(rafRef.current);
    rafRef.current = requestAnimationFrame(tick);
  };

  useEffect(() => {
    if (!startOnView) {
      animate();
      return () => cancelAnimationFrame(rafRef.current);
    }

    const el = ref.current;
    if (!el) return;

    const io = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            if (once && hasAnimated) return;
            animate();
            if (once) io.unobserve(el);
          }
        });
      },
      { threshold: 0.2 }
    );

    io.observe(el);
    return () => {
      io.disconnect();
      cancelAnimationFrame(rafRef.current);
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [target, duration, startOnView, once]);

  const number =
    typeof formatter === "function"
      ? formatter(value)
      : Number(value).toLocaleString(undefined, {
          minimumFractionDigits: decimals,
          maximumFractionDigits: decimals,
        });

  return (
    <span ref={ref} className={className}>
      {prefix}
      {number}
      {suffix}
    </span>
  );
}

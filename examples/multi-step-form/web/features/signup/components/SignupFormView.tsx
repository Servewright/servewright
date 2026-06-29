"use client";

import { useEffect, useMemo, useState } from "react";
import {
  ServewrightView,
  createRegistry,
  type View,
} from "@servewright/react";
import { registerShadcnPrimitives } from "@servewright/react-shadcn";

export function SignupFormView() {
  const [view, setView] = useState<View | null>(null);
  const [error, setError] = useState<string | null>(null);

  const registry = useMemo(() => {
    const next = createRegistry();
    registerShadcnPrimitives(next);
    return next;
  }, []);

  useEffect(() => {
    const controller = new AbortController();

    fetch("/servewright/view/demo-form", { signal: controller.signal })
      .then((response) => {
        if (!response.ok) {
          throw new Error(`HTTP ${response.status}`);
        }
        return response.json() as Promise<View>;
      })
      .then(setView)
      .catch((cause: unknown) => {
        if (cause instanceof DOMException && cause.name === "AbortError") {
          return;
        }
        setError(cause instanceof Error ? cause.message : "Unknown error");
      });

    return () => controller.abort();
  }, []);

  if (error) {
    return <p role="alert">Failed to load signup form: {error}</p>;
  }

  if (!view) {
    return <p>Loading…</p>;
  }

  return (
    <main>
      <ServewrightView view={view} registry={registry} onViewChange={setView} />
    </main>
  );
}

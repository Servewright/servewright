"use client";

import { useEffect, useMemo, useState } from "react";
import {
  createRegistry,
  createRenderer,
  type View,
} from "@servewright/react";
import { registerShadcnPrimitives } from "@servewright/react-shadcn";

type HelloViewProps = {
  initialView?: View;
};

export function HelloView({ initialView }: HelloViewProps) {
  const [view, setView] = useState<View | null>(initialView ?? null);
  const [error, setError] = useState<string | null>(null);

  const renderer = useMemo(() => {
    const registry = createRegistry();
    registerShadcnPrimitives(registry);
    return createRenderer(registry);
  }, []);

  useEffect(() => {
    if (initialView) {
      return;
    }

    const controller = new AbortController();

    fetch("/servewright/view/hello", { signal: controller.signal })
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
  }, [initialView]);

  if (error) {
    return <p role="alert">Failed to load view: {error}</p>;
  }

  if (!view) {
    return <p>Loading…</p>;
  }

  return <main>{renderer.render(view)}</main>;
}

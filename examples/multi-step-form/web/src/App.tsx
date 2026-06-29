import { useEffect, useMemo, useState } from "react";
import {
  createRegistry,
  createRenderer,
  type View,
} from "@servewright/react";
import { registerShadcnPrimitives } from "@servewright/react-shadcn";

export function App() {
  const [view, setView] = useState<View | null>(null);
  const [error, setError] = useState<string | null>(null);

  const renderer = useMemo(() => {
    const registry = createRegistry();
    registerShadcnPrimitives(registry);
    return createRenderer(registry);
  }, []);

  useEffect(() => {
    fetch("/servewright/view/hello")
      .then((response) => {
        if (!response.ok) {
          throw new Error(`HTTP ${response.status}`);
        }
        return response.json() as Promise<View>;
      })
      .then(setView)
      .catch((cause: unknown) => {
        setError(cause instanceof Error ? cause.message : "Unknown error");
      });
  }, []);

  if (error) {
    return <p role="alert">Failed to load view: {error}</p>;
  }

  if (!view) {
    return <p>Loading…</p>;
  }

  return <main>{renderer.render(view)}</main>;
}

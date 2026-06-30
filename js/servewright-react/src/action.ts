import type { Action, ActionResponse, View } from "./types.js";

export async function fetchView(viewUrl: string, screen: string): Promise<View> {
  const response = await fetch(`${viewUrl}/${encodeURIComponent(screen)}`);
  if (!response.ok) {
    throw new Error(`View fetch failed: HTTP ${response.status}`);
  }
  return (await response.json()) as View;
}

export async function postAction(actionUrl: string, action: Action): Promise<ActionResponse> {
  const response = await fetch(actionUrl, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(action),
  });

  if (!response.ok) {
    throw new Error(`Action failed: HTTP ${response.status}`);
  }

  return (await response.json()) as ActionResponse;
}

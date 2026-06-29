import type { Action, ActionResponse } from "./types.js";

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

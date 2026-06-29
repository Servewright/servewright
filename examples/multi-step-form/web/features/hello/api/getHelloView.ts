import type { View } from "@servewright/react";

export async function getHelloView(): Promise<View> {
  const baseUrl = process.env.SERVEWRIGHT_API_URL ?? "http://localhost:8080";
  const response = await fetch(`${baseUrl}/servewright/view/hello`, {
    cache: "no-store",
  });

  if (!response.ok) {
    throw new Error(`HTTP ${response.status}`);
  }

  return response.json() as Promise<View>;
}

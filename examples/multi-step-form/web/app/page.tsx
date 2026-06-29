import { HelloView, getHelloView } from "@/features/hello";

export default async function HomePage() {
  let initialView;
  try {
    initialView = await getHelloView();
  } catch {
    initialView = undefined;
  }

  return <HelloView initialView={initialView} />;
}

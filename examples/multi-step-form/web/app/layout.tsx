import type { Metadata } from "next";

export const metadata: Metadata = {
  title: "Servewright Hello",
  description: "Server-driven UI hello example",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body>{children}</body>
    </html>
  );
}

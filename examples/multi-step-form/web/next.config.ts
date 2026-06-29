import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  async rewrites() {
    return [
      {
        source: "/servewright/:path*",
        destination: "http://localhost:8080/servewright/:path*",
      },
    ];
  },
};

export default nextConfig;

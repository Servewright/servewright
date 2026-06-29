import { describe, it } from "node:test";
import assert from "node:assert/strict";
import { readdirSync, readFileSync } from "node:fs";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";
import { renderToStaticMarkup } from "react-dom/server";
import { createRegistry, createRenderer, type View } from "@servewright/react";
import { registerShadcnPrimitives } from "@servewright/react-shadcn";

interface ConformanceCase {
  primitive: string;
  root: View["root"];
  expect: { contains: string[] };
}

const casesDir = join(dirname(fileURLToPath(import.meta.url)), "..", "cases");
const registry = createRegistry();
registerShadcnPrimitives(registry);
const renderer = createRenderer(registry);

describe("conformance react", () => {
  for (const file of readdirSync(casesDir).filter((name) => name.endsWith(".json"))) {
    const testCase = JSON.parse(readFileSync(join(casesDir, file), "utf8")) as ConformanceCase;

    it(`renders ${testCase.primitive} (${file})`, () => {
      const view: View = {
        servewrightVersion: "1.0",
        schemaVersion: "0.1.0",
        screen: "conformance",
        stateVersion: 0,
        root: testCase.root,
      };

      const markup = renderToStaticMarkup(renderer.render(view));
      for (const text of testCase.expect.contains) {
        assert.match(markup, new RegExp(text.replace(/[.*+?^${}()|[\]\\]/g, "\\$&")));
      }
    });
  }
});

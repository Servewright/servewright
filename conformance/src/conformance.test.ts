import { describe, it } from "node:test";
import assert from "node:assert/strict";
import { readdirSync, readFileSync } from "node:fs";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";
import { renderToStaticMarkup } from "react-dom/server";
import {
  applyTransition,
  createRegistry,
  createRenderer,
  type Transition,
  type View,
} from "@servewright/react";
import { registerShadcnPrimitives } from "@servewright/react-shadcn";

interface Expect {
  contains?: string[];
  notContains?: string[];
  attributes?: string[];
}

interface PrimitiveCase {
  primitive: string;
  schemaVersion?: string;
  root: View["root"];
  expect: Expect;
}

interface TransitionCase {
  name: string;
  initialView: View;
  transitions: Transition[];
  expect: Expect;
}

const casesDir = join(dirname(fileURLToPath(import.meta.url)), "..", "cases");
const registry = createRegistry();
registerShadcnPrimitives(registry);
const renderer = createRenderer(registry);

function assertExpect(markup: string, expect: Expect): void {
  for (const text of expect.contains ?? []) {
    assert.match(markup, new RegExp(text.replace(/[.*+?^${}()|[\]\\]/g, "\\$&")));
  }
  for (const text of expect.notContains ?? []) {
    assert.doesNotMatch(markup, new RegExp(text.replace(/[.*+?^${}()|[\]\\]/g, "\\$&")));
  }
  for (const attribute of expect.attributes ?? []) {
    assert.match(markup, new RegExp(attribute.replace(/[.*+?^${}()|[\]\\]/g, "\\$&")));
  }
}

describe("conformance react", () => {
  for (const file of readdirSync(casesDir).filter((name) => name.endsWith(".json"))) {
    const raw = JSON.parse(readFileSync(join(casesDir, file), "utf8")) as PrimitiveCase | TransitionCase;

    if ("initialView" in raw) {
      it(`applies transition sequence (${file})`, () => {
        let view = raw.initialView;
        for (const transition of raw.transitions) {
          view = applyTransition(view, transition);
        }
        const markup = renderToStaticMarkup(renderer.render(view));
        assertExpect(markup, raw.expect);
      });
      continue;
    }

    it(`renders ${raw.primitive} (${file})`, () => {
      const view: View = {
        servewrightVersion: "1.0",
        schemaVersion: raw.schemaVersion ?? "0.1.0",
        screen: "conformance",
        stateVersion: 0,
        root: raw.root,
      };

      const markup = renderToStaticMarkup(renderer.render(view));
      assertExpect(markup, raw.expect);
    });
  }
});

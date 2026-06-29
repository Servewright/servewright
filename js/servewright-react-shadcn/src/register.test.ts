import { describe, it } from "node:test";
import assert from "node:assert/strict";
import { createRegistry, createRenderer } from "@servewright/react";
import { renderToStaticMarkup } from "react-dom/server";
import { registerShadcnPrimitives } from "./index.js";

describe("registerShadcnPrimitives", () => {
  it("registers Text renderer", () => {
    const registry = createRegistry();
    registerShadcnPrimitives(registry);
    const renderer = createRenderer(registry);

    const markup = renderToStaticMarkup(
      renderer.render({
        servewrightVersion: "1.0",
        schemaVersion: "0.1.0",
        screen: "hello",
        stateVersion: 0,
        root: {
          id: "greeting",
          type: "Text",
          props: { content: "Bonjour", emphasis: "heading" },
        },
      }),
    );

    assert.match(markup, /<h1[^>]*>Bonjour<\/h1>/);
  });
});

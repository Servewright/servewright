import { describe, it } from "node:test";
import assert from "node:assert/strict";
import { createRegistry, createRenderer, type ServewrightNode } from "@servewright/react";
import { renderToStaticMarkup } from "react-dom/server";
import { registerShadcnPrimitives } from "./index.js";

describe("registerShadcnPrimitives", () => {
  it("registers all standard primitives", () => {
    const registry = createRegistry();
    registerShadcnPrimitives(registry);
    const renderer = createRenderer(registry);

    const primitives = [
      { type: "Text", props: { content: "Bonjour", emphasis: "heading" }, expect: /<h1[^>]*>Bonjour<\/h1>/ },
      { type: "Container", props: { layout: "vertical" }, children: [{ id: "t", type: "Text", props: { content: "In" } }], expect: /data-servewright-type="Container"/ },
      { type: "Stat", props: { label: "Users", value: "42", delta: "+3" }, expect: /Users/ },
      { type: "Button", props: { label: "Go" }, expect: /Go/ },
    ] as Array<{
      type: string;
      props: Record<string, unknown>;
      children?: ServewrightNode[];
      expect: RegExp;
    }>;

    for (const primitive of primitives) {
      const markup = renderToStaticMarkup(
        renderer.render({
          servewrightVersion: "1.0",
          schemaVersion: "0.1.0",
          screen: "test",
          stateVersion: 0,
          root: {
            id: "root",
            type: primitive.type,
            props: primitive.props,
            children: "children" in primitive ? primitive.children : undefined,
          },
        }),
      );
      assert.match(markup, primitive.expect);
    }
  });
});

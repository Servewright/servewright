import { describe, it } from "node:test";
import assert from "node:assert/strict";
import { createElement } from "react";
import { renderToStaticMarkup } from "react-dom/server";
import {
  createRegistry,
  createRenderer,
  type PrimitiveComponent,
} from "./renderer.js";
import type { View } from "./types.js";
import { registerShadcnPrimitives } from "@servewright/react-shadcn";

const textComponent: PrimitiveComponent = (node) =>
  createElement("p", { "data-servewright-id": node.id }, String(node.props.content));

const helloView: View = {
  servewrightVersion: "1.0",
  schemaVersion: "0.1.0",
  screen: "hello",
  stateVersion: 0,
  root: {
    id: "greeting",
    type: "Text",
    props: { content: "Bonjour" },
  },
};

describe("createRenderer", () => {
  it("renders Text primitive content", () => {
    const registry = createRegistry();
    registry.register("Text", textComponent);
    const renderer = createRenderer(registry);

    const markup = renderToStaticMarkup(renderer.render(helloView));

    assert.match(markup, /Bonjour/);
  });

  it("renders unknown primitive placeholder without throwing", () => {
    const registry = createRegistry();
    const renderer = createRenderer(registry);
    const unknownView: View = {
      ...helloView,
      root: {
        id: "mystery",
        type: "FutureWidget",
        props: {},
      },
    };

    const markup = renderToStaticMarkup(renderer.render(unknownView));

    assert.match(markup, /Unknown primitive: FutureWidget/);
    assert.match(markup, /data-servewright-unknown="FutureWidget"/);
  });

  it("renders nested composition hierarchy", () => {
    const registry = createRegistry();
    registerShadcnPrimitives(registry);
    const renderer = createRenderer(registry);

    const compositeView: View = {
      ...helloView,
      screen: "demo-form",
      root: {
        id: "signup-form",
        type: "Form",
        props: { actionTarget: "signup" },
        children: [
          {
            id: "personal-group",
            type: "Group",
            props: { label: "Personal" },
            children: [
              {
                id: "email",
                type: "TextInput",
                props: { label: "Email", placeholder: "you@example.com" },
              },
              {
                id: "submit",
                type: "Button",
                props: { label: "Submit", role: "submit" },
              },
            ],
          },
        ],
      },
    };

    const markup = renderToStaticMarkup(renderer.render(compositeView));

    assert.match(markup, /data-servewright-type="Form"/);
    assert.match(markup, /data-servewright-type="Group"/);
    assert.match(markup, /data-servewright-type="TextInput"/);
    assert.match(markup, /data-servewright-type="Button"/);
    assert.match(markup, /Personal/);
    assert.match(markup, /Submit/);
  });
});

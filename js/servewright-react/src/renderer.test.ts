import { describe, it } from "node:test";
import assert from "node:assert/strict";
import { createElement } from "react";
import { renderToStaticMarkup } from "react-dom/server";
import {
  createRegistry,
  createRenderer,
  type PrimitiveComponent,
  type Registry,
} from "./renderer.js";
import type { View } from "./types.js";

const textComponent: PrimitiveComponent = (node) =>
  createElement("p", { "data-servewright-id": node.id }, String(node.props.content));

function registerStubCompositionPrimitives(registry: Registry): void {
  registry.register("Form", (node, ctx) =>
    createElement(
      "form",
      { "data-servewright-type": "Form", "data-servewright-id": node.id },
      ...ctx.renderChildren(node.children),
    ),
  );
  registry.register("Group", (node, ctx) =>
    createElement(
      "fieldset",
      { "data-servewright-type": "Group", "data-servewright-id": node.id },
      node.props.label ? createElement("legend", null, String(node.props.label)) : null,
      ...ctx.renderChildren(node.children),
    ),
  );
  registry.register("TextInput", (node) =>
    createElement(
      "label",
      { "data-servewright-type": "TextInput", "data-servewright-id": node.id },
      String(node.props.label ?? ""),
      createElement("input", { readOnly: true, defaultValue: String(node.props.value ?? "") }),
    ),
  );
  registry.register("Button", (node) =>
    createElement(
      "button",
      { "data-servewright-type": "Button", "data-servewright-id": node.id },
      String(node.props.label ?? ""),
    ),
  );
}

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
    registerStubCompositionPrimitives(registry);
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

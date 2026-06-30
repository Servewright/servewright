import { describe, it } from "node:test";
import assert from "node:assert/strict";
import { createElement } from "react";
import { renderToStaticMarkup } from "react-dom/server";
import {
  ServewrightBindingContext,
  createRegistry,
  createRenderer,
  type BindingContextValue,
  type PrimitiveComponent,
} from "./index.js";

const textInputStub: PrimitiveComponent = (node) =>
  createElement(
    "label",
    { "data-servewright-type": "TextInput", "data-servewright-id": node.id },
    String(node.props.label ?? ""),
    createElement("input", { readOnly: true }),
    (node.props.errors as string[] | undefined)?.length
      ? createElement(
          "ul",
          { "data-servewright-errors": node.id },
          ...(node.props.errors as string[]).map((error) => createElement("li", { key: error }, error)),
        )
      : null,
  );

describe("interactive primitives", () => {
  it("renders server errors under the matching field", () => {
    const registry = createRegistry();
    registry.register("TextInput", textInputStub);
    const renderer = createRenderer(registry);

    const binding: BindingContextValue = {
      values: { email: "bad" },
      forceOnChange: new Set(["email"]),
      validating: new Set(),
      setFieldValue: () => undefined,
      onFieldBlur: () => undefined,
      onFieldChange: () => undefined,
      onFormSubmit: () => undefined,
      getFieldErrors: (fieldId: string) => (fieldId === "email" ? ["Invalid format"] : []),
      isValidating: () => false,
    };

    const markup = renderToStaticMarkup(
      createElement(
        ServewrightBindingContext.Provider,
        { value: binding },
        renderer.render({
          servewrightVersion: "1.0",
          schemaVersion: "0.1.0",
          screen: "demo-form",
          stateVersion: 1,
          root: {
            id: "email",
            type: "TextInput",
            props: { label: "Email", errors: ["Invalid format"] },
          },
        }),
      ),
    );

    assert.match(markup, /Invalid format/);
    assert.match(markup, /data-servewright-errors="email"/);
  });
});

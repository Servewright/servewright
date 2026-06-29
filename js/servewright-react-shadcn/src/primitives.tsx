import { createElement, type FormEvent } from "react";
import type { PrimitiveComponent, RenderContext } from "@servewright/react";
import { useServewrightBinding } from "@servewright/react";
import type { ServewrightNode } from "@servewright/react";

function nodeAttrs(type: string, id: string, extra: Record<string, string> = {}) {
  return {
    "data-servewright-type": type,
    "data-servewright-id": id,
    ...extra,
  };
}

function FormPrimitive({ node, ctx }: { node: ServewrightNode; ctx: RenderContext }) {
  const binding = useServewrightBinding();
  const actionTarget = String(node.props.actionTarget ?? "");

  return createElement(
    "form",
    {
      ...nodeAttrs("Form", node.id, { "data-servewright-action-target": actionTarget }),
      onSubmit: binding
        ? (event: FormEvent) => {
            event.preventDefault();
            binding.onFormSubmit(actionTarget);
          }
        : undefined,
    },
    ...ctx.renderChildren(node.children),
  );
}

function TextInputPrimitive({ node }: { node: ServewrightNode }) {
  const binding = useServewrightBinding();
  const props = node.props as {
    label: string;
    value?: string;
    placeholder?: string;
    required?: boolean;
    errors?: string[];
    validating?: boolean;
  };

  const value = binding?.values[node.id] ?? props.value ?? "";
  const errors = binding?.getFieldErrors(node.id) ?? props.errors ?? [];
  const validating = binding?.isValidating(node.id) ?? props.validating ?? false;
  const interactive = binding !== null;

  return createElement(
    "label",
    nodeAttrs("TextInput", node.id),
    props.label,
    createElement("input", {
        type: "text",
        readOnly: !interactive,
        ...(interactive
          ? { value }
          : { defaultValue: props.value ?? "" }),
        placeholder: props.placeholder,
      required: props.required ?? false,
      "aria-label": props.label,
      "aria-invalid": errors.length > 0,
      onChange: interactive
        ? (event: { target: { value: string } }) => binding.onFieldChange(node.id, event.target.value)
        : undefined,
      onBlur: interactive ? () => binding.onFieldBlur(node.id) : undefined,
    }),
    validating ? createElement("span", { "data-servewright-validating": "true" }, "Validating…") : null,
    errors.length > 0
      ? createElement(
          "ul",
          { "data-servewright-errors": node.id },
          ...errors.map((error: string) => createElement("li", { key: error }, error)),
        )
      : null,
  );
}

function ButtonPrimitive({ node }: { node: ServewrightNode }) {
  const binding = useServewrightBinding();
  const props = node.props as { label: string; role?: string };
  const role = props.role ?? "button";
  const interactive = binding !== null;

  return createElement(
    "button",
    {
      ...nodeAttrs("Button", node.id, { "data-servewright-role": role }),
      type: role === "submit" ? "submit" : "button",
      disabled: !interactive && role !== "submit" ? true : false,
    },
    props.label,
  );
}

export function createTextComponent(): PrimitiveComponent {
  return (node) => {
    const props = node.props as { content: string; emphasis?: string };
    const emphasis = props.emphasis ?? "body";
    const tag = emphasis === "heading" ? "h1" : emphasis === "caption" ? "small" : emphasis === "muted" ? "span" : "p";
    return createElement(tag, nodeAttrs("Text", node.id, { "data-servewright-emphasis": emphasis }), props.content);
  };
}

export function createContainerComponent(): PrimitiveComponent {
  return (node, ctx) =>
    createElement(
      "div",
      nodeAttrs("Container", node.id, {
        "data-servewright-layout": String(node.props.layout ?? "vertical"),
      }),
      ...ctx.renderChildren(node.children),
    );
}

export function createFormComponent(): PrimitiveComponent {
  return (node, ctx) => createElement(FormPrimitive, { node, ctx });
}

export function createGroupComponent(): PrimitiveComponent {
  return (node, ctx) =>
    createElement(
      "fieldset",
      nodeAttrs("Group", node.id),
      node.props.label ? createElement("legend", null, String(node.props.label)) : null,
      ...ctx.renderChildren(node.children),
    );
}

export function createTextInputComponent(): PrimitiveComponent {
  return (node) => createElement(TextInputPrimitive, { node });
}

export function createSelectComponent(): PrimitiveComponent {
  return (node) => {
    const props = node.props as {
      label: string;
      value?: string;
      options: Array<{ value: string; label: string }>;
    };
    return createElement(
      "label",
      nodeAttrs("Select", node.id),
      props.label,
      createElement(
        "select",
        { defaultValue: props.value, disabled: true },
        props.options.map((option) =>
          createElement("option", { key: option.value, value: option.value }, option.label),
        ),
      ),
    );
  };
}

export function createCheckboxComponent(): PrimitiveComponent {
  return (node) => {
    const props = node.props as { label: string; checked?: boolean };
    return createElement(
      "label",
      nodeAttrs("Checkbox", node.id),
      createElement("input", {
        type: "checkbox",
        readOnly: true,
        defaultChecked: props.checked ?? false,
      }),
      props.label,
    );
  };
}

export function createButtonComponent(): PrimitiveComponent {
  return (node) => createElement(ButtonPrimitive, { node });
}

export function createStatComponent(): PrimitiveComponent {
  return (node) => {
    const props = node.props as { label: string; value: string; delta?: string };
    return createElement(
      "dl",
      nodeAttrs("Stat", node.id),
      createElement("dt", null, props.label),
      createElement("dd", null, props.value),
      props.delta ? createElement("dd", null, props.delta) : null,
    );
  };
}

export function createTableComponent(): PrimitiveComponent {
  return (node) => {
    const props = node.props as {
      columns: Array<{ key: string; label: string }>;
      rows: Array<{ cells: Record<string, string> }>;
    };
    return createElement(
      "table",
      nodeAttrs("Table", node.id),
      createElement(
        "thead",
        null,
        createElement(
          "tr",
          null,
          props.columns.map((column) =>
            createElement("th", { key: column.key }, column.label),
          ),
        ),
      ),
      createElement(
        "tbody",
        null,
        props.rows.map((row, index) =>
          createElement(
            "tr",
            { key: index },
            props.columns.map((column) =>
              createElement("td", { key: column.key }, row.cells[column.key] ?? ""),
            ),
          ),
        ),
      ),
    );
  };
}

export function registerShadcnPrimitives(registry: {
  register(type: string, component: PrimitiveComponent): void;
}): void {
  registry.register("Text", createTextComponent());
  registry.register("Container", createContainerComponent());
  registry.register("Form", createFormComponent());
  registry.register("Group", createGroupComponent());
  registry.register("TextInput", createTextInputComponent());
  registry.register("Select", createSelectComponent());
  registry.register("Checkbox", createCheckboxComponent());
  registry.register("Button", createButtonComponent());
  registry.register("Stat", createStatComponent());
  registry.register("Table", createTableComponent());
}

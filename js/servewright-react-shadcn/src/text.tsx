import { createElement } from "react";
import type { PrimitiveComponent } from "@servewright/react";
import type { TextProps } from "@servewright/react";

const EMPHASIS_TAGS = {
  body: "p",
  heading: "h1",
  caption: "small",
  muted: "span",
} as const;

export function createTextComponent(): PrimitiveComponent {
  return (node) => {
    const props = node.props as unknown as TextProps;
    const emphasis = props.emphasis ?? "body";
    const tag = EMPHASIS_TAGS[emphasis] ?? "p";
    return createElement(
      tag,
      {
        "data-servewright-type": "Text",
        "data-servewright-id": node.id,
        "data-servewright-emphasis": emphasis,
      },
      props.content,
    );
  };
}

export function registerShadcnPrimitives(registry: {
  register(type: string, component: PrimitiveComponent): void;
}): void {
  registry.register("Text", createTextComponent());
}

import { createElement, type ReactElement } from "react";
import type { ServewrightNode, View } from "./types.js";

export interface RenderContext {
  renderChild: (node: ServewrightNode) => ReactElement;
  renderChildren: (nodes?: ServewrightNode[]) => ReactElement[];
}

export type PrimitiveComponent = (
  node: ServewrightNode,
  ctx: RenderContext,
) => ReactElement;

export interface Registry {
  register(type: string, component: PrimitiveComponent): void;
  resolve(type: string): PrimitiveComponent | undefined;
}

export function createRegistry(): Registry {
  const components = new Map<string, PrimitiveComponent>();

  return {
    register(type, component) {
      components.set(type, component);
    },
    resolve(type) {
      return components.get(type);
    },
  };
}

export interface Renderer {
  render(view: View): ReactElement;
}

export function createRenderer(registry: Registry): Renderer {
  const renderNode = (node: ServewrightNode): ReactElement => {
    const component = registry.resolve(node.type);
    if (!component) {
      return createUnknownPlaceholder(node);
    }
    const ctx: RenderContext = {
      renderChild: renderNode,
      renderChildren: (nodes) => (nodes ?? []).map(renderNode),
    };
    return component(node, ctx);
  };

  return {
    render(view) {
      return renderNode(view.root);
    },
  };
}

function createUnknownPlaceholder(node: ServewrightNode): ReactElement {
  return createElement(
    "div",
    {
      "data-servewright-unknown": node.type,
      "data-servewright-id": node.id,
    },
    `Unknown primitive: ${node.type}`,
  );
}

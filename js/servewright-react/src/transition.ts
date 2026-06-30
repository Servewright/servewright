import type { ServewrightNode, Transition, View } from "./types.js";

export type Patch =
  | { op: "replace"; target: string; node: ServewrightNode }
  | { op: "insert"; parent: string; index: number; node: ServewrightNode }
  | { op: "remove"; target: string }
  | { op: "setError"; target: string; errors: string[] }
  | { op: "setLoading"; target: string; loading: boolean };

export class TransitionDesyncError extends Error {
  constructor(expectedBasedOn: number, actualStateVersion: number) {
    super(`Transition basedOn=${expectedBasedOn} does not match client stateVersion=${actualStateVersion}`);
    this.name = "TransitionDesyncError";
  }
}

export function applyTransition(
  view: View,
  transition: Transition,
  dirtyFields: Set<string> = new Set(),
): View {
  if (transition.basedOn !== view.stateVersion) {
    throw new TransitionDesyncError(transition.basedOn, view.stateVersion);
  }

  let root = view.root;
  for (const patch of transition.patches as Patch[]) {
    root = applyPatch(root, patch, dirtyFields);
  }

  return {
    ...view,
    stateVersion: transition.stateVersion,
    root,
  };
}

function applyPatch(node: ServewrightNode, patch: Patch, dirtyFields: Set<string>): ServewrightNode {
  switch (patch.op) {
    case "replace":
      if (node.id === patch.target) {
        if (dirtyFields.has(patch.target) && isInputNode(patch.node)) {
          return preserveLocalValue(patch.node, node);
        }
        return patch.node;
      }
      return {
        ...node,
        children: node.children?.map((child) => applyPatch(child, patch, dirtyFields)),
      };
    case "insert":
      if (node.id === patch.parent) {
        const children = [...(node.children ?? [])];
        children.splice(patch.index, 0, patch.node);
        return { ...node, children };
      }
      return {
        ...node,
        children: node.children?.map((child) => applyPatch(child, patch, dirtyFields)),
      };
    case "remove":
      if (node.id === patch.target) {
        return node;
      }
      return {
        ...node,
        children: node.children
          ?.filter((child) => child.id !== patch.target)
          .map((child) => applyPatch(child, patch, dirtyFields)),
      };
    case "setError":
      if (node.id === patch.target) {
        return {
          ...node,
          props: {
            ...node.props,
            errors: patch.errors,
          },
        };
      }
      return {
        ...node,
        children: node.children?.map((child) => applyPatch(child, patch, dirtyFields)),
      };
    case "setLoading":
      if (node.id === patch.target) {
        const props = { ...node.props };
        if (patch.loading) {
          props.loading = true;
        } else {
          delete props.loading;
        }
        return { ...node, props };
      }
      return {
        ...node,
        children: node.children?.map((child) => applyPatch(child, patch, dirtyFields)),
      };
  }
}

function isInputNode(node: ServewrightNode): boolean {
  return node.type === "TextInput" || node.type === "Select" || node.type === "Checkbox";
}

function preserveLocalValue(incoming: ServewrightNode, current: ServewrightNode): ServewrightNode {
  if (!isInputNode(incoming) || !isInputNode(current)) {
    return incoming;
  }
  return {
    ...incoming,
    props: {
      ...incoming.props,
      value: current.props.value,
    },
  };
}

export function collectDirtyFields(
  view: View,
  localValues: Record<string, string>,
): Set<string> {
  const dirty = new Set<string>();
  for (const [fieldId, localValue] of Object.entries(localValues)) {
    const serverValue = findNodeById(view.root, fieldId)?.props.value;
    if (String(serverValue ?? "") !== localValue) {
      dirty.add(fieldId);
    }
  }
  return dirty;
}

function findNodeById(node: ServewrightNode, id: string): ServewrightNode | undefined {
  if (node.id === id) {
    return node;
  }
  for (const child of node.children ?? []) {
    const found = findNodeById(child, id);
    if (found) {
      return found;
    }
  }
  return undefined;
}

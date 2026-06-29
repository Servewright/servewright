import type { FieldTrigger, ServewrightNode, TextInputProps } from "./types.js";

export function validateTextInput(props: TextInputProps, value: string): string[] {
  const errors: string[] = [];
  const normalized = value ?? "";

  if (props.required && normalized.trim().length === 0) {
    errors.push("Required");
  }

  if (props.minLength !== undefined && normalized.length < props.minLength) {
    errors.push(`Minimum length is ${props.minLength}`);
  }

  if (props.pattern && normalized.length > 0) {
    const regex = new RegExp(props.pattern);
    if (!regex.test(normalized)) {
      errors.push("Invalid format");
    }
  }

  return errors;
}

export function validateInputNode(node: ServewrightNode, value: string): string[] {
  if (node.type === "TextInput") {
    return validateTextInput(node.props as unknown as TextInputProps, value);
  }
  return [];
}

export function collectInputNodes(root: ServewrightNode): ServewrightNode[] {
  const inputs: ServewrightNode[] = [];
  walk(root, inputs);
  return inputs;
}

function walk(node: ServewrightNode, inputs: ServewrightNode[]): void {
  if (node.type === "TextInput" || node.type === "Select" || node.type === "Checkbox") {
    inputs.push(node);
  }
  for (const child of node.children ?? []) {
    walk(child, inputs);
  }
}

export function findFormByActionTarget(root: ServewrightNode, target: string): ServewrightNode | undefined {
  if (root.type === "Form" && root.props.actionTarget === target) {
    return root;
  }
  for (const child of root.children ?? []) {
    const found = findFormByActionTarget(child, target);
    if (found) {
      return found;
    }
  }
  return undefined;
}

export function collectFormPayload(formRoot: ServewrightNode, values: Record<string, string>): Record<string, string> {
  const payload: Record<string, string> = {};
  for (const field of collectInputNodes(formRoot)) {
    payload[field.id] = values[field.id] ?? String(field.props.value ?? "");
  }
  return payload;
}

export function mergeLocalValues(root: ServewrightNode, values: Record<string, string>): ServewrightNode {
  const props = { ...root.props };
  if (root.type === "TextInput" || root.type === "Select" || root.type === "Checkbox") {
    if (values[root.id] !== undefined) {
      if (root.type === "Checkbox") {
        props.checked = values[root.id] === "true";
      } else {
        props.value = values[root.id];
      }
    }
  }

  return {
    ...root,
    props,
    children: root.children?.map((child) => mergeLocalValues(child, values)),
  };
}

export function extractInitialValues(root: ServewrightNode): Record<string, string> {
  const values: Record<string, string> = {};
  for (const field of collectInputNodes(root)) {
    if (field.type === "Checkbox") {
      values[field.id] = String(field.props.checked ?? false);
    } else {
      values[field.id] = String(field.props.value ?? "");
    }
  }
  return values;
}

export function effectiveTrigger(node: ServewrightNode, forceOnChange: boolean): FieldTrigger {
  if (forceOnChange) {
    return "onChange";
  }
  const trigger = (node.props as unknown as TextInputProps).trigger;
  return trigger ?? "onBlur";
}

export function findFieldContext(
  root: ServewrightNode,
  fieldId: string,
): { form: ServewrightNode; field: ServewrightNode; actionTarget: string } | undefined {
  return findFieldContextRecursive(root, fieldId, undefined);
}

function findFieldContextRecursive(
  node: ServewrightNode,
  fieldId: string,
  currentForm: ServewrightNode | undefined,
): { form: ServewrightNode; field: ServewrightNode; actionTarget: string } | undefined {
  const form = node.type === "Form" ? node : currentForm;

  if (node.id === fieldId && form) {
    return {
      form,
      field: node,
      actionTarget: String(form.props.actionTarget ?? ""),
    };
  }

  for (const child of node.children ?? []) {
    const found = findFieldContextRecursive(child, fieldId, form);
    if (found) {
      return found;
    }
  }

  return undefined;
}

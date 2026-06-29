export type FieldTrigger = "onSubmit" | "onBlur" | "onChange";

export interface Action {
  type: string;
  target: string;
  screen: string;
  stateVersion: number;
  payload: Record<string, unknown>;
}

export interface ActionResponse {
  view: View;
}

export interface View {
  servewrightVersion: string;
  schemaVersion: string;
  screen: string;
  stateVersion: number;
  root: ServewrightNode;
}

export interface ServewrightNode {
  id: string;
  type: string;
  props: NodeProps;
  children?: ServewrightNode[];
}

export interface NodeProps {
  [key: string]: unknown;
}

export type TextEmphasis = "body" | "heading" | "caption" | "muted";

export interface TextProps {
  content: string;
  emphasis?: TextEmphasis;
}

export interface TextInputProps {
  label: string;
  value?: string;
  placeholder?: string;
  required?: boolean;
  minLength?: number;
  pattern?: string;
  trigger?: FieldTrigger;
  debounceMs?: number;
  asyncValidation?: boolean;
  errors?: string[];
  validating?: boolean;
}

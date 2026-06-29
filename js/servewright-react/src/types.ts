export type TextEmphasis = "body" | "heading" | "caption" | "muted";

export interface NodeProps {
  [key: string]: unknown;
}

export interface ServewrightNode {
  id: string;
  type: string;
  props: NodeProps;
  children?: ServewrightNode[];
}

export interface View {
  servewrightVersion: string;
  schemaVersion: string;
  screen: string;
  stateVersion: number;
  root: ServewrightNode;
}

export interface TextProps {
  content: string;
  emphasis?: TextEmphasis;
}

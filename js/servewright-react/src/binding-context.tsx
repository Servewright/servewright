import { createContext, useContext } from "react";
import type { Action, View } from "./types.js";
import type { Transport } from "./transport.js";

export interface BindingContextValue {
  values: Record<string, string>;
  forceOnChange: Set<string>;
  validating: Set<string>;
  setFieldValue: (fieldId: string, value: string) => void;
  onFieldBlur: (fieldId: string) => void;
  onFieldChange: (fieldId: string, value: string) => void;
  onFormSubmit: (actionTarget: string) => void;
  getFieldErrors: (fieldId: string) => string[];
  isValidating: (fieldId: string) => boolean;
}

export const ServewrightBindingContext = createContext<BindingContextValue | null>(null);

export function useServewrightBinding(): BindingContextValue | null {
  return useContext(ServewrightBindingContext);
}

export interface ServewrightViewOptions {
  actionUrl?: string;
  viewUrl?: string;
  onViewChange?: (view: View) => void;
  transport?: Transport;
}

export type { Action, View };

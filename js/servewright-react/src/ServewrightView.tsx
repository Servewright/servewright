"use client";

import {
  useCallback,
  useEffect,
  useMemo,
  useRef,
  useState,
  type ReactElement,
} from "react";
import { fetchView, postAction } from "./action.js";
import {
  collectFormPayload,
  effectiveTrigger,
  extractInitialValues,
  findFieldContext,
  findFormByActionTarget,
  mergeLocalValues,
  validateInputNode,
} from "./binding.js";
import {
  ServewrightBindingContext,
  type ServewrightViewOptions,
} from "./binding-context.js";
import { createRegistry, createRenderer, type Registry } from "./renderer.js";
import { SseTransport, type Transport } from "./transport.js";
import {
  TransitionDesyncError,
  applyTransition,
  collectDirtyFields,
} from "./transition.js";
import type { Action, Transition, View } from "./types.js";

export interface ServewrightViewProps extends ServewrightViewOptions {
  view: View;
  registry: Registry;
  transport?: Transport;
}

export function ServewrightView({
  view: initialView,
  registry,
  actionUrl = "/servewright/action",
  viewUrl = "/servewright/view",
  onViewChange,
  transport = new SseTransport(),
}: ServewrightViewProps): ReactElement {
  const [view, setView] = useState(initialView);
  const [values, setValues] = useState<Record<string, string>>(() =>
    extractInitialValues(initialView.root),
  );
  const [forceOnChange, setForceOnChange] = useState<Set<string>>(() => new Set());
  const [validating, setValidating] = useState<Set<string>>(() => new Set());
  const debounceTimers = useRef<Map<string, ReturnType<typeof setTimeout>>>(new Map());

  useEffect(() => {
    setView(initialView);
    setValues(extractInitialValues(initialView.root));
    setForceOnChange(new Set());
    setValidating(new Set());
  }, [initialView]);

  const renderer = useMemo(() => createRenderer(registry), [registry]);

  const resyncView = useCallback(async () => {
    const fresh = await fetchView(viewUrl, view.screen);
    setView(fresh);
    onViewChange?.(fresh);
  }, [onViewChange, view.screen, viewUrl]);

  const applyServerTransition = useCallback(
    (transition: Transition) => {
      try {
        const dirtyFields = collectDirtyFields(view, values);
        const next = applyTransition(view, transition, dirtyFields);
        setView(next);
        onViewChange?.(next);
      } catch (error) {
        if (error instanceof TransitionDesyncError) {
          void resyncView();
        } else {
          throw error;
        }
      }
    },
    [onViewChange, resyncView, values, view],
  );

  useEffect(() => {
    return transport.connect(view.screen, {
      onTransition: applyServerTransition,
    });
  }, [applyServerTransition, transport, view.screen]);

  const dispatchAction = useCallback(
    async (action: Action) => {
      const response = await postAction(actionUrl, action);
      if (response.transition) {
        applyServerTransition(response.transition);
        return;
      }
      if (response.view) {
        setView(response.view);
        setValues(extractInitialValues(response.view.root));
        onViewChange?.(response.view);
      }
    },
    [actionUrl, applyServerTransition, onViewChange],
  );

  const runAsyncValidation = useCallback(
    async (fieldId: string, actionTarget: string, payload: Record<string, string>) => {
      setValidating((current) => new Set(current).add(fieldId));
      try {
        await dispatchAction({
          type: "asyncValidate",
          target: actionTarget,
          screen: view.screen,
          stateVersion: view.stateVersion,
          payload: { [fieldId]: payload[fieldId] },
        });
      } finally {
        setValidating((current) => {
          const next = new Set(current);
          next.delete(fieldId);
          return next;
        });
      }
    },
    [dispatchAction, view.screen, view.stateVersion],
  );

  const setFieldValue = useCallback((fieldId: string, value: string) => {
    setValues((current) => ({ ...current, [fieldId]: value }));
  }, []);

  const onFieldBlur = useCallback(
    (fieldId: string) => {
      const context = findFieldContext(view.root, fieldId);
      if (!context) {
        return;
      }

      const value = values[fieldId] ?? "";
      const errors = validateInputNode(context.field, value);
      if (errors.length > 0) {
        setForceOnChange((current) => new Set(current).add(fieldId));
      }

      if (context.field.props.asyncValidation) {
        const payload = collectFormPayload(context.form, values);
        void runAsyncValidation(fieldId, context.actionTarget, payload);
      }
    },
    [runAsyncValidation, values, view.root],
  );

  const onFieldChange = useCallback(
    (fieldId: string, value: string) => {
      setFieldValue(fieldId, value);

      const context = findFieldContext(view.root, fieldId);
      if (!context) {
        return;
      }

      const trigger = effectiveTrigger(context.field, forceOnChange.has(fieldId));
      if (trigger !== "onChange") {
        return;
      }

      const debounceMs = (context.field.props.debounceMs as number | undefined) ?? 0;
      const existing = debounceTimers.current.get(fieldId);
      if (existing) {
        clearTimeout(existing);
      }

      const timer = setTimeout(() => {
        const errors = validateInputNode(context.field, value);
        if (errors.length > 0) {
          setForceOnChange((current) => new Set(current).add(fieldId));
        }
      }, debounceMs);
      debounceTimers.current.set(fieldId, timer);
    },
    [forceOnChange, setFieldValue, view.root],
  );

  const onFormSubmit = useCallback(
    (actionTarget: string) => {
      const form = findFormByActionTarget(view.root, actionTarget);
      if (!form) {
        return;
      }

      const payload = collectFormPayload(form, values);
      void dispatchAction({
        type: "submit",
        target: actionTarget,
        screen: view.screen,
        stateVersion: view.stateVersion,
        payload,
      });
    },
    [dispatchAction, values, view.root, view.screen, view.stateVersion],
  );

  const getFieldErrors = useCallback(
    (fieldId: string) => {
      const findNode = (node: View["root"]): string[] => {
        if (node.id === fieldId && Array.isArray(node.props.errors)) {
          return node.props.errors as string[];
        }
        for (const child of node.children ?? []) {
          const found = findNode(child);
          if (found.length > 0) {
            return found;
          }
        }
        return [];
      };
      return findNode(view.root);
    },
    [view.root],
  );

  const binding = useMemo(
    () => ({
      values,
      forceOnChange,
      validating,
      setFieldValue,
      onFieldBlur,
      onFieldChange,
      onFormSubmit,
      getFieldErrors,
      isValidating: (fieldId: string) => validating.has(fieldId),
    }),
    [
      forceOnChange,
      getFieldErrors,
      onFieldBlur,
      onFieldChange,
      onFormSubmit,
      setFieldValue,
      validating,
      values,
    ],
  );

  const displayView = useMemo(
    () => ({
      ...view,
      root: mergeLocalValues(view.root, values),
    }),
    [values, view],
  );

  return (
    <ServewrightBindingContext.Provider value={binding}>
      {renderer.render(displayView)}
    </ServewrightBindingContext.Provider>
  );
}

export function createDefaultRegistry(): Registry {
  return createRegistry();
}

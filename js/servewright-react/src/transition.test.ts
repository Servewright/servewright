import { describe, it } from "node:test";
import assert from "node:assert/strict";
import { applyTransition, collectDirtyFields, TransitionDesyncError } from "./transition.js";
import type { View } from "./types.js";

const baseView: View = {
  servewrightVersion: "1.0",
  schemaVersion: "0.1.0",
  screen: "demo-form",
  stateVersion: 0,
  root: {
    id: "signup-form",
    type: "Form",
    props: { actionTarget: "signup" },
    children: [
      {
        id: "personal-group",
        type: "Group",
        props: { label: "Personal" },
        children: [
          {
            id: "email",
            type: "TextInput",
            props: { label: "Email", value: "" },
          },
          {
            id: "username",
            type: "TextInput",
            props: { label: "Username", value: "" },
          },
        ],
      },
    ],
  },
};

describe("applyTransition", () => {
  it("applies setError to the matching field", () => {
    const updated = applyTransition(baseView, {
      basedOn: 0,
      stateVersion: 1,
      patches: [{ op: "setError", target: "email", errors: ["Invalid format"] }],
    });

    const email = updated.root.children![0].children![0];
    assert.deepEqual(email.props.errors, ["Invalid format"]);
  });

  it("preserves dirty local field when replace targets it", () => {
    const dirty = new Set(["email"]);
    const updated = applyTransition(
      baseView,
      {
        basedOn: 0,
        stateVersion: 1,
        patches: [
          {
            op: "replace",
            target: "email",
            node: {
              id: "email",
              type: "TextInput",
              props: { label: "Email", value: "server-value" },
            },
          },
        ],
      },
      dirty,
    );

    assert.equal(updated.root.children![0].children![0].props.value, "");
  });

  it("does not overwrite untouched dirty field on unrelated patch", () => {
    const localValues = { email: "typing", username: "" };
    const dirty = collectDirtyFields(baseView, localValues);
    const updated = applyTransition(
      baseView,
      {
        basedOn: 0,
        stateVersion: 1,
        patches: [{ op: "setError", target: "username", errors: ["Taken"] }],
      },
      dirty,
    );

    assert.deepEqual(updated.root.children![0].children![1].props.errors, ["Taken"]);
    assert.equal(updated.root.children![0].children![0].props.value, "");
  });

  it("throws TransitionDesyncError when basedOn mismatches stateVersion", () => {
    const staleView = { ...baseView, stateVersion: 1 };
    assert.throws(
      () =>
        applyTransition(staleView, {
          basedOn: 0,
          stateVersion: 2,
          patches: [{ op: "setError", target: "email", errors: ["Stale"] }],
        }),
      TransitionDesyncError,
    );
  });
});

import { describe, it } from "node:test";
import assert from "node:assert/strict";
import {
  collectFormPayload,
  effectiveTrigger,
  extractInitialValues,
  validateTextInput,
} from "./binding.js";
import type { ServewrightNode } from "./types.js";

const formRoot: ServewrightNode = {
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
          props: {
            label: "Email",
            required: true,
            pattern: "^[^@]+@[^@]+\\.[^@]+$",
            trigger: "onBlur",
          },
        },
      ],
    },
  ],
};

describe("binding", () => {
  it("extracts initial values from view tree", () => {
    const values = extractInitialValues(formRoot);
    assert.equal(values.email, "");
  });

  it("validates email pattern client-side", () => {
    const errors = validateTextInput(formRoot.children![0].children![0].props as never, "bad");
    assert.ok(errors.length > 0);
  });

  it("switches to onChange after invalid field", () => {
    const field = formRoot.children![0].children![0];
    assert.equal(effectiveTrigger(field, false), "onBlur");
    assert.equal(effectiveTrigger(field, true), "onChange");
  });

  it("collects payload bounded to form group", () => {
    const payload = collectFormPayload(formRoot, { email: "user@example.com" });
    assert.deepEqual(payload, { email: "user@example.com" });
  });
});

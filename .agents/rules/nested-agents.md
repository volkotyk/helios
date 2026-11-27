# Nested AGENTS Rules

Add a nested `AGENTS.md` only when a subdirectory has rules that differ from the root.

## When to add one

- A folder has different setup or test commands (for example `ha/` with `python -m pytest -q ha/tests`).
- A generated or vendored area needs a "do not edit" rule (for example the vendored assets under `app/src/main/assets/` and `app/src/main/jniLibs/`).
- A folder uses a different language or deployment target (for example the Python tools in `tools/`).
- A high-risk folder needs extra guardrails.

Helios has no nested `AGENTS.md` today. A nested file inside an upstream folder is a fork-only file in that folder. Prefer a section in a reference page when it is sufficient.

## How to write one

- Keep it short and local to the folder.
- State only the rules that differ from the root `AGENTS.md`.
- Link to root references. Do not copy long sections.
- Add it to `.agents/index.md`.

## Agent behavior

- The root `AGENTS.md` applies everywhere.
- The nearest nested `AGENTS.md` applies to files under its directory.
- When two rules conflict, follow the more specific nested rule, unless it breaks a root security or safety rule.

# Source Traceability

Each durable KB claim must point to a raw source.

## Required

- Each new page in `.agents/references/` starts with this frontmatter:

  ```yaml
  ---
  title: "Page Title"
  type: entity | concept | source-summary | comparison | synthesis
  sources:
    - "app/src/main/java/pl/mateusz/helios/Updater.java"
  related:
    - "System_Map.md"
  updated: "YYYY-MM-DD"
  confidence: high | medium | low
  ---
  ```

- A source-backed claim links to a code path, a `docs/SPEC-*.md` section, an issue, a pull request, or an external reference.
- Mark a claim `confidence: low` until a command output or the source confirms it.
- Summarize external material in your own words and link it. Do not paste long copyrighted text.

## Confidence levels

- `high`: a stable invariant, enforced by tests or tooling, or confirmed in the source.
- `medium`: verified current behavior that is likely to change.
- `low`: a hypothesis, an incomplete survey, or an external dependency that can drift.

## Write-back triggers

Write durable synthesis back when a task produces:

- a root-cause analysis
- an architectural decision
- a new workflow or command
- a security finding
- a release lesson
- a repeated debugging pattern
- a role, rule or skill change
- a new fork-only seam or a new conflict with upstream

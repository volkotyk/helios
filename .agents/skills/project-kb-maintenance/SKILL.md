---
name: project-kb-maintenance
description: Use this skill when you change AGENTS.md, CLAUDE.md, .agents/ (roles, rules, references, skills, memory, log) or .claude/skills/, or when a task produces durable Helios knowledge that a future agent needs. It keeps the KB source-backed, linked, and lint-clean.
---

# project-kb-maintenance

Use this skill when you update `AGENTS.md`, `.agents/roles/`, `.agents/rules/`, `.agents/references/`, `.agents/skills/`, `.agents/memory/CURRENT.md` or `.agents/log.md`.

## Triggers

- "update AGENTS"
- "add a role", "add a rule", "add a skill"
- "document this pattern"
- any task that creates durable project knowledge

## Workflow

1. Find the page that owns the fact. Use the table in [kb_maintenance_rules.md](../../rules/kb_maintenance_rules.md) § Where a change goes.
2. Read that page and its related pages before you edit.
3. Verify each new claim against the source. Mark an unverified claim `confidence: low`.
4. Give a new reference page the frontmatter from [source-traceability.md](../../rules/source-traceability.md).
5. Link a new page from `.agents/index.md`. Add a new role or skill to [role-activation.md](../../rules/role-activation.md).
6. After you add, rename or remove a skill, or change its frontmatter, regenerate the Claude Code stubs:
   ```powershell
   pwsh -NoProfile -File tools/sync_claude_skill_stubs.ps1
   ```
7. Add a newest-first entry to `.agents/log.md`.
8. Run the KB lint and fix every error and warning:
   ```powershell
   pwsh -NoProfile -File tools/check_agent_kb.ps1
   ```

## Output

- Changed KB files.
- The durable knowledge that the change records.
- Link, index and stub updates.
- KB lint result.

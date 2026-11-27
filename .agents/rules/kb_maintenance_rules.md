# Knowledge Base Maintenance Rules

`AGENTS.md` owns the always-on rules. If this page and `AGENTS.md` disagree, `AGENTS.md` wins.

## Layers

| Layer | Location | Use |
|---|---|---|
| Source of truth | Code, tests, `docs/SPEC-*.md`, git history, the clock | Verify every claim here |
| Wiki | `.agents/references/`, `.agents/roles/`, `.agents/rules/`, `.agents/skills/` | Compiled knowledge. Correct a page when the code disagrees with it |
| Router | `AGENTS.md` | Routes and always-on rules only |
| Index | `.agents/index.md` | Catalog of every KB page |

Keep every KB page reachable from `AGENTS.md`, directly or through `.agents/index.md`.

`docs/` belongs to upstream and holds the Polish SPEC and PLAN files. Read it as a source. Do not write agent notes into it.

## Where a change goes

| Change | Page |
|---|---|
| New class, or a changed responsibility of a main component | [System_Map.md](../references/System_Map.md) |
| New setup step or developer command | [Setup_And_Commands.md](../references/Setup_And_Commands.md) |
| Code style rule | [Code_Style.md](../references/Code_Style.md) |
| New test pattern or test command | [Testing_Guidelines.md](../references/Testing_Guidelines.md) |
| New lookup route | [Context_Navigation.md](../references/Context_Navigation.md) |
| New role or skill | [role-activation.md](role-activation.md) and `.agents/index.md` |
| Fork policy, remotes, merge procedure | [fork-upstream-sync.md](fork-upstream-sync.md) |
| Cross-session orientation, planned fork work | `.agents/memory/CURRENT.md` |
| Any meaningful change | `.agents/log.md` |

## Operations

- **Ingest**: Read the source. Update each page that owns a changed fact, and its links.
- **Query**: Search the KB first. Use the source to verify a claim or when a page is stale. Write a root cause or a decision with lasting value into a page, not only into a commit message.
- **Lint**: After a major change, check for contradictions, stale commands, orphan pages and missing links. Run `tools/check_agent_kb.ps1`.

## Page format

Use the frontmatter in [source-traceability.md](source-traceability.md) for `.agents/index.md`, `.agents/memory/CURRENT.md` and every page in `.agents/references/`.

## Log

Put the newest entry at the top of `.agents/log.md`:

```markdown
## [YYYY-MM-DD] type | topic
- key takeaway
- roles: Android Developer, QA Engineer
- fork-only: yes | no; upstream files touched: none | app/src/main/java/pl/mateusz/helios/I18n.java
- pages: AGENTS.md, .agents/references/System_Map.md
```

`type` names the work, for example `init`, `feature`, `fix`, `test`, `docs`, `release`, `sync`, `ingest`, `lint` or `decision`. The `fork-only` line is mandatory: it is the record that the next upstream merge uses.

`.agents/memory/CURRENT.md` holds orientation only and stays under 8 KB. Put dated status in `.agents/log.md`.

## Host wiring

- `AGENTS.md` is the only instruction file. `CLAUDE.md` holds exactly one line, `@AGENTS.md`, so that Claude Code imports the same file. Do not add other content to `CLAUDE.md`.
- `.agents/skills/<name>/SKILL.md` is the only copy of a skill. Claude Code reads `.claude/skills/<name>/SKILL.md`, which `tools/sync_claude_skill_stubs.ps1` generates. Do not edit a stub by hand.
- `tools/check_agent_kb.ps1` checks the required files, `CLAUDE.md`, the stubs, local links, frontmatter, role and skill routing, placeholders and the log order.

## Update checklist

1. Update the page that owns the fact.
2. Update `AGENTS.md` only when a route or an always-on rule changes.
3. Update `.agents/index.md` when you add a page, role, rule or skill.
4. Run `tools/sync_claude_skill_stubs.ps1` when a skill changes.
5. Add the `.agents/log.md` entry.
6. Run `pwsh -NoProfile -File tools/check_agent_kb.ps1` and fix every error and warning.

## Primitive audit

Each rule, role and skill costs context on the tasks that load it. When you touch one, answer two questions:

1. Which failure does it prevent? Name the failure.
2. Does the failure still occur in this project?

Delete a primitive that has no named failure.

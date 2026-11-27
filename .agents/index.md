---
title: "Agent Knowledge Base Index"
type: synthesis
sources:
  - "AGENTS.md"
  - "repository tree"
related:
  - "memory/CURRENT.md"
  - "references/Context_Navigation.md"
updated: "2026-11-27"
confidence: high
---

# Agent Knowledge Base Index

`AGENTS.md` is the operating contract. This page is the catalog.

## Startup pack

1. [AGENTS.md](../AGENTS.md)
2. [Current Agent Memory](memory/CURRENT.md)
3. [System Map](references/System_Map.md)
4. [Context Navigation](references/Context_Navigation.md)
5. Top entries of the [Agent Log](log.md)

## References

- [System Map](references/System_Map.md)
- [Setup And Commands](references/Setup_And_Commands.md)
- [Code Style](references/Code_Style.md)
- [Testing Guidelines](references/Testing_Guidelines.md)
- [Context Navigation](references/Context_Navigation.md)

## Project sources

- [README](../README.md) and [Install guide](../docs/INSTALL.md)
- `docs/SPEC-*.md` and `docs/PLAN-*.md` (Polish feature contracts and plans)
- [NOTICE](../NOTICE)

## Rules

- [Agent Operating Rules](rules/agent-operating-rules.md)
- [Role Activation](rules/role-activation.md)
- [Fork And Upstream Sync](rules/fork-upstream-sync.md)
- [Tool Safety](rules/tool-safety.md)
- [Source Traceability](rules/source-traceability.md)
- [KB Maintenance Rules](rules/kb_maintenance_rules.md)
- [Nested AGENTS Rules](rules/nested-agents.md)

## Roles

- [Project Manager](roles/project-manager.md)
- [Architect](roles/architect.md)
- [Android Developer](roles/android-developer.md)
- [Frontend Developer](roles/frontend-developer.md)
- [Localization Translator](roles/localization-translator.md)
- [DevOps / Build Engineer](roles/devops-build-engineer.md)
- [Security Analyst](roles/security-analyst.md)
- [QA Engineer](roles/qa-engineer.md)
- [TDD Engineer](roles/tdd-engineer.md)
- [Code Reviewer](roles/code-reviewer.md)
- [Critic](roles/critic.md)
- [Debugger](roles/debugger.md)
- [Android Logcat Debugger](roles/android-logcat-debugger.md)
- [Technical Writer](roles/technical-writer.md)

## Skills

- [anxiety](skills/anxiety/SKILL.md)
- [project-android-development](skills/project-android-development/SKILL.md)
- [project-debugging](skills/project-debugging/SKILL.md)
- [project-logcat-debugging](skills/project-logcat-debugging/SKILL.md)
- [project-release-checklist](skills/project-release-checklist/SKILL.md)
- [project-kb-maintenance](skills/project-kb-maintenance/SKILL.md)
- [gh-fix-ci](skills/gh-fix-ci/SKILL.md)
- [dependency-auditor](skills/dependency-auditor/SKILL.md)
- [security-threat-model](skills/security-threat-model/SKILL.md)
- [Third-party skill sources and licenses](skills/THIRD_PARTY_SKILLS.md)

## Tools

- `tools/check_agent_kb.ps1`: KB lint.
- `tools/sync_claude_skill_stubs.ps1`: generates `.claude/skills/<name>/SKILL.md` stubs.

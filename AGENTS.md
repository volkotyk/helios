# AGENTS.md - Helios

> **Helios**: a native Home Assistant dashboard and voice satellite for the Lenovo Smart Clock 2 (Android 10, no WebView).
> Stack: Java 8, Android platform APIs only (no AndroidX, no Material, no Hilt), Gradle 8.14.3, AGP 8.13.2, JUnit 4.

> [!IMPORTANT]
> **Every meaningful change gets a newest-first entry in `.agents/log.md`**, with the `fork-only` line.
> **Read before you edit.** Read the role files, rules and references for the touched paths before the first edit. A link is not a read. [role-activation.md](.agents/rules/role-activation.md) owns the routing.
> **This is a fork.** Keep the diff against upstream `SychPL/helios` small. See [Fork policy](#fork-policy).

This file is the single instruction file for every agent host. `CLAUDE.md` contains only `@AGENTS.md`, so Claude Code loads this file. Keep this file to routes and always-on rules.

---

## Project overview

* **Device**: Lenovo Smart Clock 2, Android 10 (API 29), landscape. Code works in 800 x 480 screen units. Helios is the HOME launcher and is sideloaded, never published on Google Play.
* **Build**: Java 8 language level, minSdk 27, targetSdk 29 on purpose, compileSdk 35, build-tools 36.0.0. Runtime libraries: `Java-WebSocket`, `slf4j`.
* **Sources**: package `pl.mateusz.helios`, one folder `app/src/main/java/pl/mateusz/helios/` (about 75 files). JVM tests in `app/src/test/java/pl/mateusz/helios/` (JUnit 4 + `org.json`). UI is drawn in code with custom Views; there are no XML layouts.
* **i18n**: Polish source strings are the keys and the fallback. Code calls `I18n.t("polski tekst")`. Catalogues: `app/src/main/assets/i18n/<lang>.json` (`en`, `de`, and the fork adds `uk`). See `I18n.java`, `Translations.java`, `TranslationsTest.java`.
* **Updater**: `Updater.java` and `ReleaseInfo.java` install the single asset `helios-X.Y.Z.apk` of a final GitHub release `vX.Y.Z`, after a package and version check. The APK signing key must stay the same across releases.
* **Specs**: `docs/SPEC-*.md` and `docs/PLAN-*.md` (Polish) are the feature contract. Each class Javadoc names its SPEC section. HA YAML is in `ha/`. Python and PowerShell helpers are in `tools/`.
* **Map**: [System_Map.md](.agents/references/System_Map.md).

---

## Style and stance

Apply these rules to every answer, plan, review, commit message and code comment. The `anxiety` skill holds the full guide; load it only on an explicit request.

- Be pessimistic and self-critical. Lead with the highest risk. State what you verified and what you only assumed.
- Write English in ASD-STE100: one term for one concept, active voice, imperative for instructions, short sentences, simple tenses, no idioms or metaphors.
- Put a warning before the step that causes the risk. Use a list or a table for conditions and trade-offs.
- Answer in the language of the user, with the same principles. Keep code, identifiers and paths in their original form.
- Keep sarcasm literal, and keep it out of warnings, risk statements and procedures.

---

## Read before you edit

This rule applies to every agent, session and subagent, before the first edit of a task.

- Select the roles in [role-activation.md](.agents/rules/role-activation.md). Read each role file and the listed references. Invoke the matching skills.
- Automatic loading of `AGENTS.md` does not satisfy this rule.
- Name the roles, references, rules and skills in your first plan message.
- A session that starts a subagent puts this rule in the brief.
- A project file wins over a default habit.

---

## Fork policy

[fork-upstream-sync.md](.agents/rules/fork-upstream-sync.md) owns the details and the merge procedure.

- Remotes: `origin` = `volkotyk/helios`, `upstream` = `SychPL/helios`, `english-ui` = `markgraham924/helios` (read only).
- Keep the diff against `upstream/main` minimal and isolated, so that `git merge upstream/main` stays cheap.
- Prefer a new file or a narrow seam to a rewrite of upstream code. Do not reformat upstream code.
- Record every fork-only change in `.agents/log.md`, with the upstream files that it touches.

---

## Roles

[role-activation.md](.agents/rules/role-activation.md) is the only routing table for roles and skills. A role is a working perspective, not a separate agent.

---

## Workflow

1. **Discovery**: read the intent, the dirty state, the roles, `.agents/memory/CURRENT.md` and the SPEC section.
2. **Pre-edit design gate**: state the current behavior, the desired behavior, the affected components, and the focused verification. If you cannot state one of them, do more discovery. For a bug fix, find every caller of the function and fix the shared function once.
3. **Implementation**: make the smallest change that keeps existing contracts. Match the style of the file ([Code_Style.md](.agents/references/Code_Style.md)).
4. **Smallest test first**: run the narrowest test that can fail, then the full suite.
5. **Pre-final critic gate**: for a substantial plan, an architecture answer or a change across components, apply the [Critic](.agents/roles/critic.md) once. Check the weakest assumption, the contract that can break, the missing verification, and whether the result is still simpler than the problem. Fix blocking objections. Do not add a second review pass for routine edits.
6. **KB update**: add the `.agents/log.md` entry and update the page that owns each changed fact.

* **KISS / SOLID**: Use SOLID as a boundary check, not as ceremony. Extend an existing seam before you add an abstraction. Add an abstraction only when it removes real duplication, protects a boundary, or makes a failure path testable. Put decisions in pure classes; keep Android calls in glue classes.
* **Evidence**: Mark a root cause, a count or a capability claim as a hypothesis until a command output or the source confirms it.

---

## Testing

```powershell
.\gradlew.bat testDebugUnitTest assembleDebug lintDebug
.\gradlew.bat testDebugUnitTest --tests pl.mateusz.helios.TranslationsTest
python -m pytest -q ha/tests
```

On Linux or macOS, use `./gradlew`. [Testing_Guidelines.md](.agents/references/Testing_Guidelines.md) says when a test is mandatory. [Setup_And_Commands.md](.agents/references/Setup_And_Commands.md) has setup and device commands. Report every check that did not run.

---

## Security

- Secrets: HA and Music Assistant tokens, the stored connection, `ha.json`, `ma.json`, `provision.json`, `bridge.json`, everything under `.local/`, and any `*.keystore` or `*.jks`. Never commit them. Never print them.
- Never log a token: not in logcat, not in `assist-events.jsonl`, not in telemetry, not in the `diagnostics_url` post.
- Security Analyst review is required for pairing, the device channel, the updater, the tools bridge, manifest permissions and release signing. See [security-analyst.md](.agents/roles/security-analyst.md).
- ADB over Wi-Fi exposes the clock to the whole LAN. Turn it off after a device session.

---

## Git

- The user is the only author of every commit and pull request. Do not add `Co-Authored-By`, `Generated with` or any other AI attribution line or trailer.
- Do not change the git identity. Commit, push or open a pull request only when the user asks.

---

## Knowledge base

- [.agents/index.md](.agents/index.md): catalog of every KB page.
- [.agents/memory/CURRENT.md](.agents/memory/CURRENT.md): orientation, planned fork work, open risks.
- `.agents/rules/`, `.agents/roles/`, `.agents/references/`, `.agents/skills/`: rules, roles, compiled knowledge, playbooks.
- `.claude/skills/`: generated stubs. Run `pwsh -NoProfile -File tools/sync_claude_skill_stubs.ps1` after a skill change.
- [kb_maintenance_rules.md](.agents/rules/kb_maintenance_rules.md): where each fact goes. Run `pwsh -NoProfile -File tools/check_agent_kb.ps1` after a KB change.

## Log

Every meaningful change gets a newest-first entry in [.agents/log.md](.agents/log.md). The format is in [kb_maintenance_rules.md](.agents/rules/kb_maintenance_rules.md) § Log.

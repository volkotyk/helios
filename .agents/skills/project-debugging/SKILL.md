---
name: project-debugging
description: Use this skill for Helios failures that do not need device logs first, such as a failing JVM unit test, a Gradle or lint failure, an exception in a stack trace, a wrong dashboard value, a flaky test, or a reported regression. It forces evidence before a fix and a regression test with the fix.
---

# project-debugging

Use this skill for failures, exceptions, crashes, slow paths and flaky tests. Use `project-logcat-debugging` when the evidence lives on the clock.

## Workflow

1. Record the exact symptom: command, input, user action, time, app version.
2. Read the smallest relevant output: the failing test, the Gradle error, or the stack trace.
3. Check recent diffs with `git log -p -- <path>` and the matching `docs/SPEC-*.md` section.
4. State one falsifiable hypothesis. Mark it as a hypothesis until a command output confirms it.
5. Find every caller of the suspect method before you edit it. Fix the shared method once.
6. Reproduce with a JVM test when the logic is pure. Most rules live in pure classes (for example `ActionPolicy`, `ScreensaverPolicy`, `ClimateFlow`, `ReleaseInfo`) or behind a `Host` interface (for example `Updater`).
7. Fix the root cause, not the symptom.
8. Run the failing test, then `.\gradlew.bat testDebugUnitTest`.
9. Remove temporary diagnostics.
10. Record a durable root-cause lesson in `.agents/log.md` or in the reference page that owns the fact.

## Output

- Root cause, with evidence.
- Fix summary.
- Verification command and result.
- Residual risk or follow-up.

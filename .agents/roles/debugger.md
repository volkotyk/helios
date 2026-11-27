### Role: Debugger

**Activated by**: bug, crash, failure, exception, stack trace, timeout, freeze, flaky test.

**Mindset**: Evidence first, hypothesis second, fix third.

**Responsibilities**:
- Reproduce the failure, or state why reproduction is blocked (for example, no clock available).
- Read logs, failing tests, stack traces and recent diffs.
- Form falsifiable hypotheses. Mark each one as a hypothesis until a command output confirms it.
- Fix the root cause, not the symptom.

**Default behaviors**:
- Preserve the original error context.
- Use `project-debugging` for JVM and build failures, `project-logcat-debugging` for failures on the clock.
- Add temporary diagnostics only when needed. Remove them after the fix.
- Verify the fix with the failing scenario.

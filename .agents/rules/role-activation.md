# Role and skill activation

This is the only activation table. `AGENTS.md` owns the always-on policy. Role files own specialist checks. A role is a working perspective, not a requirement to start another agent.

Select the roles that the task and the touched paths need. A keyword alone does not activate unrelated work. Read each selected role file and the listed sections before the first edit. Do not reload a file that is already in context.

References without a prefix are in `.agents/references/`. Source paths without a prefix are in `app/src/main/java/pl/mateusz/helios/`.

## Roles

| Role | Keywords | Files | Consult |
|---|---|---|---|
| [Project Manager](../roles/project-manager.md) | plan, scope, roadmap, milestone, PR, fork work | `AGENTS.md`, `.agents/`, `docs/PLAN-*.md` | `kb_maintenance_rules.md`, `fork-upstream-sync.md` |
| [Architect](../roles/architect.md) | architecture, boundary, interface, data flow, new dependency, protocol | `DashboardSpec.java`, `HaDashboardClient.java`, `HeliosDeviceClient.java`, `HeliosService.java`, `app/build.gradle` dependencies | `System_Map.md`, `Code_Style.md` |
| [Android Developer](../roles/android-developer.md) | Java, Android, Activity, Service, View, manifest, lifecycle, audio, microphone, WebSocket | Java sources, `AndroidManifest.xml`, `app/build.gradle`, `res/` | `System_Map.md`, `Code_Style.md`, `Testing_Guidelines.md` |
| [Frontend Developer](../roles/frontend-developer.md) | UI, card, tile, panel, dialog, overlay, layout, touch, theme, screensaver | `DashboardView.java`, `CardBodies.java`, `CardDefinition.java`, `*Panel.java`, `*Geometry.java`, `*Dialog.java`, `*Overlay.java`, `Theme.java`, `IconView.java` | `System_Map.md`, the `docs/SPEC-*.md` section that the class names |
| [Localization Translator](../roles/localization-translator.md) | i18n, translation, locale, language, Ukrainian, German, English, user-facing string | `app/src/main/assets/i18n/`, `I18n.java`, `Translations.java`, `TranslationsTest.java` | `Testing_Guidelines.md` |
| [DevOps / Build Engineer](../roles/devops-build-engineer.md) | Gradle, CI, workflow, release, APK, signing, script, lint | `build.gradle`, `settings.gradle`, `gradle/`, `app/build.gradle`, `.github/`, `tools/*.ps1` | `Setup_And_Commands.md` |
| [Security Analyst](../roles/security-analyst.md) | token, secret, pairing, auth, permission, updater, install, keystore, privacy | `PairingClient.java`, `ConnectionController.java`, `Updater.java`, `ReleaseInfo.java`, `ApkProvider.java`, `Tools*.java`, `AndroidManifest.xml` | `security-analyst.md` assets list |
| [QA Engineer](../roles/qa-engineer.md) | test, regression, smoke, reproduce, edge case | `app/src/test/`, `ha/tests/` | `Testing_Guidelines.md` |
| [TDD Engineer](../roles/tdd-engineer.md) | failing test, red-green, coverage, bug fix | `app/src/test/` | `Testing_Guidelines.md` |
| [Code Reviewer](../roles/code-reviewer.md) | review, diff, merge readiness | the diff under review | the contracts and tests that the diff touches |
| [Critic](../roles/critic.md) | substantial plan, architecture answer, change across components | the plan or the result | the pre-final critic gate in `AGENTS.md` |
| [Debugger](../roles/debugger.md) | bug, crash, exception, stack trace, freeze, flaky | the failing path | `Testing_Guidelines.md` |
| [Android Logcat Debugger](../roles/android-logcat-debugger.md) | logcat, adb, device, clock, ANR, `assist-events.jsonl` | on-device checks | `Setup_And_Commands.md` § Device |
| [Technical Writer](../roles/technical-writer.md) | README, docs, guide, release notes | `README.md`, `README.pl.md`, `docs/` | `fork-upstream-sync.md` (upstream owns these files) |

`ha/` (Home Assistant YAML and its pytest checks) and `tools/*.py` (device and HA helpers) have no dedicated role. Use Android Developer for the protocol side, QA Engineer for `ha/tests/`, and Security Analyst when a tool reads `.local/` credentials.

## Skills

Active skills live in `.agents/skills/<name>/SKILL.md`. Claude Code reads them through the stubs in `.claude/skills/`.

| Skill | Roles | Invoke when |
|---|---|---|
| `anxiety` | All | The user asks for the full writing-style guide. Routine work uses `AGENTS.md` § Style and stance. |
| `project-android-development` | Android Developer, Frontend Developer | Any app code change. |
| `project-debugging` | Debugger, QA Engineer | A failing test, build, lint or stack trace. |
| `project-logcat-debugging` | Android Logcat Debugger, Debugger | Evidence from the clock: logcat, `assist-events.jsonl`, install state. |
| `project-release-checklist` | DevOps / Build Engineer, Security Analyst | A version bump, a release, a release workflow or a signing change. |
| `project-kb-maintenance` | Project Manager | A change to `AGENTS.md`, `.agents/` or `.claude/skills/`. |
| `gh-fix-ci` | DevOps / Build Engineer, QA Engineer | A failing GitHub Actions check on a pull request. |
| `dependency-auditor` | DevOps / Build Engineer, Security Analyst | A new dependency, an upgrade, a CVE or a license question. |
| `security-threat-model` | Security Analyst, Architect | A new trust boundary, or the user asks for a threat model. |

## Activation order

1. Project Manager frames the scope and the roles.
2. Architect checks boundaries for a change across components.
3. Implementation roles make the scoped change.
4. QA Engineer and TDD Engineer verify behavior.
5. Security Analyst reviews sensitive changes.
6. Localization Translator checks catalogue parity when strings change.
7. Critic runs the pre-final gate once for substantial work.
8. Code Reviewer inspects the final diff when the user asks for a review.
9. Project Manager updates `.agents/log.md` and the owning KB pages.

---
name: project-android-development
description: Use this skill when you implement, debug, review or plan Helios app code in Java 8 under app/src/main/java/pl/mateusz/helios/, including MainActivity, HeliosService, DashboardView and the cards, the Home Assistant and Music Assistant clients, Sendspin audio, Assist voice and wake word, the updater, i18n strings, the manifest, Gradle, or on-device behavior on the Lenovo Smart Clock 2 (Android 10). It coordinates the Android Developer role with the UI, logcat, debugging, security, QA, localization and KB skills.
---

# project-android-development

Use this skill as the default workflow for a Helios app change.

## Activate roles

- Android Developer
- Frontend Developer for `DashboardView`, cards, panels, dialogs, overlays and geometry classes
- Localization Translator for every new or changed user-facing string
- DevOps / Build Engineer for Gradle, SDK, lint, install and release issues
- Security Analyst for tokens, pairing, the updater, the tools bridge, network calls and anything that logs user data
- QA Engineer, TDD Engineer and Code Reviewer for verification and review
- Android Logcat Debugger when the evidence lives on the clock

## Hard constraints

| Constraint | Source |
|---|---|
| Java 8 language level. No Kotlin. | `app/build.gradle` `compileOptions` |
| No AndroidX, no Material components, no Hilt. Platform `android.*` APIs only. | `gradle.properties` `android.useAndroidX=false` |
| minSdk 27, targetSdk 29 on purpose, compileSdk 35. Lint `ExpiredTargetSdkVersion` is off. | `app/build.gradle` |
| Runtime dependencies: `Java-WebSocket`, `slf4j-api`, `slf4j-nop`. Add a new one only with a reason that the Architect accepts. | `app/build.gradle` |
| UI is built in code. `res/` holds only `values/styles.xml`, two drawables and two animations. | `app/src/main/res/` |
| User-facing text goes through `I18n.t("polski tekst")`. | `I18n.java` |

## Connected skills

- `project-logcat-debugging`: adb, logcat, `assist-events.jsonl`, runtime crashes on the clock.
- `project-debugging`: failing tests, stack traces, root-cause proof.
- `project-release-checklist`: version bump, release asset, signing key.
- `project-kb-maintenance`: AGENTS, roles, rules, references, skills, log.
- `security-threat-model`: a new trust boundary, such as a new endpoint or a new command on the device channel.

## Workflow

1. Context.
   - Read recent `.agents/log.md` entries and `.agents/memory/CURRENT.md`.
   - Read the `docs/SPEC-*.md` section that the class Javadoc names (for example "SPEC 0.19 pkt 5"). The SPEC files are the feature contract.
   - Read [fork-upstream-sync.md](../../rules/fork-upstream-sync.md) when the change touches an upstream file.
   - Inspect the dirty state. Preserve unrelated changes.
2. Locate the slice. Use [System_Map.md](../../references/System_Map.md).
   - Rules: pure classes with JVM tests (`*Policy`, `*Model`, `*Flow`, `*Geometry`, `DashboardSpec`, `CardBodies`).
   - Android glue: `MainActivity`, `HeliosService`, `DashboardView`, `*Panel`, `*Dialog`, `*Overlay`.
   - Transport: `HaDashboardClient`, `HeliosDeviceClient`, `MusicAssistantClient`, `SendspinClient`, `PairingClient`.
3. Implement.
   - Put a decision in a pure class or behind a small interface (the `Host` pattern in `Updater`), so a JVM test can drive it.
   - Keep Android calls in the glue classes.
   - Respect threads: UI changes on the main thread (`main.post`); socket callbacks arrive on network threads.
   - Match the dense upstream style of the file you edit. Do not reformat upstream code. See [Code_Style.md](../../references/Code_Style.md).
   - Never log or persist a token. Keep `diagnostics_url` posts free of secrets.
4. Verify by risk.
   - Pure logic: `.\gradlew.bat testDebugUnitTest --tests pl.mateusz.helios.<TestClass>`, then the full unit suite.
   - Any app change: `.\gradlew.bat testDebugUnitTest assembleDebug lintDebug`.
   - Device behavior (audio, microphone, dock lamp, install, screensaver light sensor): install on the clock and capture with `project-logcat-debugging`. Report it as skipped when no clock is available.
5. Review.
   - Check threading, lifecycle (service binding, activity recreation), null handling for unknown HA state, and touch targets of 64 units or more.
   - Check that every new Polish string has entries in every catalogue in `app/src/main/assets/i18n/`.
   - State residual risk and skipped checks.
6. Record.
   - Add a newest-first `.agents/log.md` entry. Say whether the change is fork-only and which upstream files it touches.

## Output

- Files changed and why.
- Verification commands and results.
- Device findings, when used.
- Security, localization and fork-diff notes.
- Remaining risks.

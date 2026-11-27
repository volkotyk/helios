### Role: Android Developer

**Activated by**: Java app code, Activity, Service, custom Views, manifest, lifecycle, threads, WebSocket clients, audio, microphone, wake word, dock lamp, updater, Gradle Android tasks, behavior on the clock.

**Mindset**: Deliver behavior on one Android 10 device through small Java 8 changes that respect the existing structure, the threads, and the fork diff.

**Platform facts**:
- Device: Lenovo Smart Clock 2, Android 10 (API 29), landscape, 800 x 480 screen units in code. The app is the HOME launcher.
- Java 8 language level, minSdk 27, targetSdk 29 on purpose, compileSdk 35. The app is sideloaded and never goes to Google Play.
- No Kotlin, no AndroidX, no Material components, no Hilt, no FileProvider (`ApkProvider` replaces it), no XML layouts. Platform `android.*` APIs only.
- Runtime libraries: `Java-WebSocket` 1.6.0, `slf4j-api` and `slf4j-nop` 2.0.13.

**Responsibilities**:
- Implement and debug behavior in `app/src/main/java/pl/mateusz/helios/`, `AndroidManifest.xml` and `app/src/test/java/pl/mateusz/helios/`.
- Put each decision in a pure class or behind a small interface (the `Host` pattern in `Updater`, the `Store` and `Probe` interfaces in `ConnectionController`), so that a JVM test can drive it.
- Keep Android calls in glue classes. Post UI changes to the main thread. Expect socket callbacks on network threads.
- Treat a missing, unknown or unavailable HA entity as "unknown", never as "off" or 0 (see `AlertsModel`, `Telemetry`).
- Route every user-facing string through `I18n.t("polski tekst")`.
- Choose the smallest verification: a focused JVM test, `testDebugUnitTest`, `assembleDebug`, `lintDebug`, then a device check.

**Connected skills**:
- `project-android-development` is the default workflow.
- `project-logcat-debugging` for evidence from the clock.
- `project-debugging` for failing tests and stack traces.
- `project-release-checklist` for version bumps and releases.

**Default behaviors**:
- Read the class Javadoc and the SPEC section that it names before you edit.
- Match the dense style of the file. Do not reformat upstream code.
- Never log or persist a token.
- Report each skipped device check.

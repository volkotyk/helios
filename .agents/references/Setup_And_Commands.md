---
title: "Setup And Commands"
type: entity
sources:
  - "README.md"
  - "docs/INSTALL.md"
  - "app/build.gradle"
  - "gradle/wrapper/gradle-wrapper.properties"
  - "ha/tests/test_bedroom_package.py"
related:
  - "Testing_Guidelines.md"
  - "../skills/project-release-checklist/SKILL.md"
updated: "2026-11-27"
confidence: medium
---

# Setup And Commands

Run commands from the repository root. The owner's shell is PowerShell on Windows: use `.\gradlew.bat`. On Linux or macOS, use `./gradlew` with the same arguments. On 2026-11-27, `testDebugUnitTest assembleDebug lintDebug` passed with JDK 21 on Windows (259 tests).

## Setup

- JDK: the JetBrains Runtime that ships with Android Studio. Set `JAVA_HOME` to it.
- Android SDK: platform 35 and build-tools 36.0.0. `local.properties` holds `sdk.dir`. Do not commit it.
- Gradle 8.14.3 through the wrapper. Android Gradle Plugin 8.13.2.

```powershell
.\gradlew.bat --version
```

## Build, test, lint

```powershell
.\gradlew.bat testDebugUnitTest assembleDebug lintDebug
.\gradlew.bat testDebugUnitTest --tests pl.mateusz.helios.TranslationsTest
```

The APK is `app/build/outputs/apk/debug/app-debug.apk`. Releases are debug builds on purpose.

> **Windows agent sessions:** Gradle can stop with `java.io.IOException: Unable to establish loopback connection` (`UnixDomainSockets.connect0`, `Invalid argument`). Give the JVM a short directory for its Unix domain sockets inside the ignored `.gradle/` folder:
>
> ```powershell
> New-Item -ItemType Directory -Force .gradle\uds | Out-Null
> $env:JAVA_TOOL_OPTIONS = "-Djdk.net.unixdomain.tmpdir=$PWD\.gradle\uds"
> .\gradlew.bat testDebugUnitTest
> ```

## CI (fork)

`.github/workflows/android.yml` runs `testDebugUnitTest lintDebug assembleDebug` on every pull request and push to `main`. A push to `main` also signs the APK and publishes the release `vX.Y.Z` with the asset `helios-X.Y.Z.apk`. The header of the workflow lists the required secrets and the version rule. To reproduce a release build locally, run:

```powershell
.\gradlew.bat assembleDebug -PheliosVersionName=0.21.7 -PheliosVersionCode=100007 -PheliosReleaseRepository=volkotyk/helios
```

## Device

> **Warning:** ADB over Wi-Fi lets every host on the LAN connect to the clock as `shell`. Turn it off after the session.

The stock clock has no ADB. ADB over Wi-Fi exists after `ROOT + ADB` in `SychPL/smartclock2tool` and disappears after a power cycle. `docs/INSTALL.md` § A describes the install path without ADB.

```powershell
adb connect "${env:CLOCK_IP}:5555"
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n pl.mateusz.helios/.MainActivity
adb shell pm grant pl.mateusz.helios android.permission.RECORD_AUDIO
```

Logcat and the event log: see `.agents/skills/project-logcat-debugging/SKILL.md`.

## Home Assistant YAML checks

```powershell
python -m pytest -q ha/tests
```

The tests need `pyyaml`, `jinja2` and `pytest`. They do not need a Home Assistant instance.

## Python tools

The scripts in `tools/` read the clock address from `CLOCK_IP` or `.local/bridge.json`, and HA credentials from files such as `.local/ha.json`. `.gitignore` ignores `.local/` and the names `ha.json`, `ma.json`, `provision.json` and `bridge.json` everywhere.

## Agent KB

```powershell
pwsh -NoProfile -File tools/sync_claude_skill_stubs.ps1
pwsh -NoProfile -File tools/sync_claude_skill_stubs.ps1 -Check
pwsh -NoProfile -File tools/check_agent_kb.ps1
pwsh -NoProfile -File .agents/skills/project-logcat-debugging/tests/check_capture_logcat.ps1
```

`check_agent_kb.ps1` also runs the stub check.

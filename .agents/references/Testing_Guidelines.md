---
title: "Testing Guidelines"
type: concept
sources:
  - "app/build.gradle"
  - "app/src/test/java/pl/mateusz/helios/"
  - "ha/tests/"
related:
  - "Setup_And_Commands.md"
  - "Code_Style.md"
updated: "2026-11-27"
confidence: medium
---

# Testing Guidelines

## Test layers

| Layer | Location | Command |
|---|---|---|
| JVM unit tests (JUnit 4.13.2, `org.json` 20240303) | `app/src/test/java/pl/mateusz/helios/` (about 46 files) | `.\gradlew.bat testDebugUnitTest` |
| Android lint | whole app | `.\gradlew.bat lintDebug` |
| HA YAML and Jinja | `ha/tests/` (pytest) | `python -m pytest -q ha/tests` |
| Device behavior | the clock | manual, with `project-logcat-debugging` |

There are no instrumentation tests (`app/src/androidTest/` does not exist) and no emulator setup.

## How the JVM tests reach the logic

- Most rules live in pure classes with no Android calls: policies, models, flows, geometry, `DashboardSpec`, `CardBodies`, `ReleaseInfo`.
- Classes with side effects hide them behind a small interface, for example `Updater.Host` or `ConnectionController.Store`, so a test can drive them.
- `TestHttp` is a minimal local HTTP server for tests. `com.sun.net.httpserver` is not on the Android unit-test classpath.
- `unitTests.returnDefaultValues = true` in `app/build.gradle`. An Android stub returns 0, false or null and does not throw. A test that passes through an Android call can pass for the wrong reason. Keep assertions on the pure part.

## When a test is mandatory

| Situation | Required coverage |
|---|---|
| New or changed rule in a pure class | Unit test for the rule and its edge cases |
| Bug fix | Regression test that fails before the fix |
| New card type or tap action | `CardBodies`, `CardDefinition` and `ActionPolicy` tests |
| New touch target | `*Geometry` test, at least 64 x 64 units |
| Dashboard schema change | `DashboardSpec` tests, including rejection of invalid documents |
| Updater or release contract change | `ReleaseInfoTest`, `UpdaterTest` |
| i18n code change | `TranslationsTest`, plus the key-parity check in the localization role |
| Audio, microphone, dock lamp, light sensor | Manual device check, reported as skipped when no clock is available |

## Reporting

Each final task note states:

- the tests that ran, with results
- the tests that did not run, and why
- residual risk
- whether a clock was available for device checks

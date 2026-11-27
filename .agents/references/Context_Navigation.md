---
title: "Context Navigation"
type: synthesis
sources:
  - "AGENTS.md"
  - "repository tree"
related:
  - "../index.md"
  - "System_Map.md"
updated: "2026-11-27"
confidence: high
---

# Context Navigation

Use this order to save tokens and to avoid stale assumptions.

1. `AGENTS.md`.
2. `.agents/memory/CURRENT.md`.
3. `.agents/references/System_Map.md`.
4. The role files that [role-activation.md](../rules/role-activation.md) selects for the task.
5. The top entries of `.agents/log.md`.
6. The `docs/SPEC-*.md` section that the class Javadoc names.
7. Raw source files, in targeted ranges.
8. Tests, Gradle output, logs or device behavior when correctness matters.

## Search guidance

- Use `rg` with exact identifiers from errors, tests, logs and Gradle output.
- Search for the Polish text when you start from a visible string. The Polish string is the `I18n.t` key in the code and in every catalogue.
- Search `docs/SPEC-*.md` for `pkt` numbers that a Javadoc cites.
- Read a small range around the target before you edit.

## Common routes

| Question | Start here |
|---|---|
| What a card shows or does on tap | `CardDefinition.java`, `CardBodies.java`, `ActionPolicy.java` |
| Dashboard schema and validation | `DashboardSpec.java`, `docs/ha-dashboard.md` |
| HA connection, pairing, reconnect | `HaDashboardClient.java`, `PairingClient.java`, `ConnectionController.java`, `docs/SPEC-0.10-onboarding.md` |
| Device channel commands and telemetry | `HeliosDeviceClient.java`, `Telemetry.java`, `docs/ha-integration.md` |
| Music | `MusicAssistantClient.java`, `SendspinClient.java`, `MusicSession.java`, `docs/SPEC-0.6-music-assistant.md`, `docs/SPEC-0.11-music-fullscreen.md` |
| Voice and wake word | `AssistClient.java`, `WakeWordListener.java`, `AdaptiveDecimator.java` |
| Screensaver | `ScreensaverPolicy.java`, `docs/SPEC-0.13-wygaszacz.md` |
| Updater and releases | `Updater.java`, `ReleaseInfo.java`, `.agents/skills/project-release-checklist/SKILL.md` |
| Translations | `I18n.java`, `Translations.java`, `app/src/main/assets/i18n/` |
| Install on the clock | `docs/INSTALL.md` |
| Fork policy and merges | `.agents/rules/fork-upstream-sync.md` |

## Write-back guidance

When discovery produces reusable synthesis, add it to the page that owns the fact and add a `.agents/log.md` entry.

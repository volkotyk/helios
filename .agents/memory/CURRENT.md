---
title: "Current Agent Memory"
type: synthesis
sources:
  - "git remote -v"
  - "git log upstream/main"
  - "app/build.gradle"
  - "app/src/main/java/pl/mateusz/helios/ReleaseInfo.java"
  - ".github/workflows/android.yml"
related:
  - "../index.md"
  - "../rules/fork-upstream-sync.md"
  - "../references/System_Map.md"
updated: "2026-11-27"
confidence: medium
---

# Current Agent Memory

Orientation only. Dated status goes to `.agents/log.md`. Keep this file under 8 KB.

## Fork state

- Repository: `volkotyk/helios`, a fork of `SychPL/helios`. Remotes and the merge procedure: [fork-upstream-sync.md](../rules/fork-upstream-sync.md).
- On 2026-11-27, local `main`, `origin/main` and `upstream/main` are the same commit (`7033db6`, Helios 0.21.0, `versionCode` 47). Fork work happens on feature branches.
- Fork-only files: `AGENTS.md`, `CLAUDE.md`, `.agents/`, `.claude/skills/`, `.github/workflows/android.yml`, `tools/check_agent_kb.ps1`, `tools/sync_claude_skill_stubs.ps1`, `app/src/main/assets/i18n/uk.json`, `TopBar.java`, `TopBarTest.java`.
- Upstream files with fork seams: [fork-upstream-sync.md](../rules/fork-upstream-sync.md) § Known conflict points.
- Other agents can work in the same checkout in parallel. Expect dirty or staged files that are not yours. Do not revert them.

## Fork features (branch `feat/fork-setup`, 2026-11-27)

| Feature | Where | State |
|---|---|---|
| Ukrainian UI | `uk.json`, `Translations.CATALOGUES` | Unit tests pass. Not checked on a clock. |
| Hideable top bar | `TopBar`, `DashboardView`, menu entry `Górny pasek` (pref `top_bar_hidden`, default off) | Collapsed: the grid takes the full height, a hold of `TopBar.HOLD_MS` anywhere opens the menu, an issue or a fresh status message shows the bar over the grid. Not checked on a clock. |
| CI release | `.github/workflows/android.yml`, fork block in `app/build.gradle` | Needs the four signing secrets before the first release. |
| Assist speech watchdog | `SpeechPlaybackWatchdog` (from `english-ui` `fda65d1`) | A long TTS answer is no longer cut at 30 s. |

## Open risks

- The signing secrets are not set yet. A push to `main` then fails at "Sign APK". A clock with an upstream (SychPL-signed) build cannot update to a fork build: uninstall it first.
- With the bar collapsed, the climate panel and the fullscreen music view keep their upstream position under the bar height, so a 52-unit strip of the grid stays visible above them.
- `AlertsDrawer` picks the plural form for 2-4 only. Polish and Ukrainian are wrong for 22-24, Ukrainian also for 21, 31. Upstream code; not changed.

## Startup pack

1. `AGENTS.md`
2. this file
3. [System_Map.md](../references/System_Map.md)
4. the role files from [role-activation.md](../rules/role-activation.md)
5. the top entries of [log.md](../log.md)

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
- Fork-only files: `AGENTS.md`, `CLAUDE.md`, `.agents/`, `.claude/skills/`, `.github/workflows/android.yml`, `tools/check_agent_kb.ps1`, `tools/sync_claude_skill_stubs.ps1`, `app/src/main/assets/i18n/uk.json`, `TopBar.java`, `TopBarTest.java`, `UpdateCheck.java`, `UpdateWatch.java`, `UpdateCheckTest.java`.
- Upstream files with fork seams: [fork-upstream-sync.md](../rules/fork-upstream-sync.md) § Known conflict points.
- Other agents can work in the same checkout in parallel. Expect dirty or staged files that are not yours. Do not revert them.

## Fork features (branch `feat/fork-setup`, 2026-11-27)

| Feature | Where | State |
|---|---|---|
| Ukrainian UI | `uk.json`, `Translations.CATALOGUES` | Unit tests pass. Not checked on a clock. |
| Hideable top bar | `TopBar`, `DashboardView`, menu entry `Górny pasek` (pref `top_bar_hidden`, default off) | Collapsed: the grid takes the full height, a hold of `TopBar.HOLD_MS` anywhere opens the menu, an issue or a fresh status message shows the bar over the grid. Not checked on a clock. |
| CI release | `.github/workflows/android.yml`, fork block in `app/build.gradle` | Signing secrets set on 2026-11-27. Release certificate SHA-256: `d51bedb4f1df539b9148a342db8d703694bf956f43f3af460dc5cd377c46aa19` (`CN=Helios, O=volkotyk/helios`). The owner keeps the keystore and its passwords outside the repository. |
| Automatic update check | `UpdateCheck`, `UpdateWatch`, `Updater.latest`, bar button in `DashboardView`, menu entry `Sprawdzanie aktualizacji` (pref `update_check_off`, default on) | Metadata only, 2 min after service start, then every 6 h; failures back off from 15 min up to 6 h. A tap on `Aktualizuj X.Y.Z` runs `Updater.run()`. Collapsed bar: the bar comes back for 8 s on each find; the menu entry names the version. Unit tests pass. Not checked on a clock. |
| Assist speech watchdog | `SpeechPlaybackWatchdog` (from `english-ui` `fda65d1`) | A long TTS answer is no longer cut at 30 s. |

## Open risks

- A clock with an upstream (SychPL-signed) build cannot update to a fork build: uninstall it first.
- A lost keystore or password ends in-app updates for every fork clock. Only the owner's offline copy exists; GitHub secrets cannot be read back.
- With the bar collapsed, the climate panel and the fullscreen music view keep their upstream position under the bar height, so a 52-unit strip of the grid stays visible above them.
- `AlertsDrawer` picks the plural form for 2-4 only. Polish and Ukrainian are wrong for 22-24, Ukrainian also for 21, 31. Upstream code; not changed.

## Startup pack

1. `AGENTS.md`
2. this file
3. [System_Map.md](../references/System_Map.md)
4. the role files from [role-activation.md](../rules/role-activation.md)
5. the top entries of [log.md](../log.md)

---
title: "Fork And Upstream Sync"
type: concept
sources:
  - "git remote -v"
  - "app/src/main/java/pl/mateusz/helios/ReleaseInfo.java"
  - "NOTICE"
related:
  - "../memory/CURRENT.md"
  - "kb_maintenance_rules.md"
updated: "2026-11-27"
confidence: high
---

# Fork And Upstream Sync

This repository is `volkotyk/helios`, a fork of `SychPL/helios`. The fork exists to add a small set of changes. It must stay cheap to merge `upstream/main`.

## Remotes

| Remote | Repository | Use |
|---|---|---|
| `origin` | `volkotyk/helios` | Push fork branches. Open pull requests here. |
| `upstream` | `SychPL/helios` | Read only. Merge `upstream/main` into the fork. |
| `english-ui` | `markgraham924/helios` | Read only reference. Its default branch is `english-ui`. Upstream already adapted its English UI and three cards in 0.21 (see `NOTICE`). |

Never push to `upstream` or `english-ui`. A pull request to upstream is a separate decision of the user.

## Rules

1. Keep the diff against `upstream/main` small and isolated.
2. Prefer a new file or a narrow seam to a rewrite of upstream code. For example, add a catalogue file and one registration line, not a new i18n system.
3. Do not reformat, rename or reorder upstream code. Match the style of the file you edit.
4. Keep fork-only tooling in fork-only paths: `AGENTS.md`, `CLAUDE.md`, `.agents/`, `.claude/`, `tools/*.ps1` that upstream does not have, and new workflow files.
5. Record every fork-only change in `.agents/log.md` with the `fork-only` line from [kb_maintenance_rules.md](kb_maintenance_rules.md). List each upstream file that the change touches.
6. Before you edit an upstream file, check whether upstream changed it recently: `git log --oneline -5 upstream/main -- <path>`. Recent upstream activity means a likely conflict.
7. Take code from `english-ui` only with attribution in `NOTICE`, and only when upstream does not already have it.

## Known conflict points

| Upstream file | Why the fork touches it | Confidence |
|---|---|---|
| `I18n.java`, `Translations.java`, `TranslationsTest.java` | Languages come from `Translations.CATALOGUES` (fork adds `uk`). Re-apply the list if upstream adds a language. | high |
| `NavigationMenu.java` | `uk` label in `languageLabel`; the top bar entry and `Actions.topBarLabel/topBarToggle`; the update entry label `Actions.updateLabel` and the check toggle `Actions.updateCheckLabel/updateCheckToggle`. | high |
| `MainActivity.java` | `topBarHidden()`, the `Actions` methods for the top bar and the update check, `dashboard.setBarCollapsed(...)` and `dashboard.onUpdateTap(...)` after `onBrandHold`, `updateWatch().setListener(...)` in `onServiceConnected` and `setListener(null)` in `onDestroy`. | high |
| `DashboardView.java` | `arrange` uses `TopBar.gridTop/dotsTop` and calls `arrangeBadge`; `dispatchTouchEvent` calls `trackHold`; `setMessage`, `refreshStatus`, `refreshBar`, `onBrandHold` and `buildPage` call into the bar logic; `setUpdate`, `onUpdateTap`, `arrangeBadge` and the `updateBadge` field are fork-only. The `*Geometry` classes are not changed. | high |
| `HeliosService.java` | `updateWatch` field and getter, start at the end of `onCreate`, stop at the start of `onDestroy`. | high |
| `Updater.java` | `latest()` after `run()`: a metadata-only read for `UpdateCheck`. The rest of the updater is not changed. | high |
| `AssistClient.java` | `play` uses `SpeechPlaybackWatchdog` (ported from `english-ui`, see `NOTICE`). | high |
| `ReleaseInfo.java`, `ReleaseSourceTest.java` | `HELIOS` reads `BuildConfig.RELEASE_REPOSITORY`; the test expects that value. | high |
| `app/build.gradle` | The fork block at the end of the file. The upstream `versionCode` and `versionName` lines stay unchanged: take the upstream values. | high |
| `en.json`, `de.json` | Fork keys `Górny pasek`, `ukryty`, `widoczny`, `Aktualizuj`, `Zainstaluj nową wersję Heliosa: `, `Sprawdzanie aktualizacji`, `włączone`, `wyłączone`. | high |
| `NOTICE` | Attribution for the ported `english-ui` commit. | high |

## Merge procedure

> **Warning:** Start from a clean working tree. A merge on top of uncommitted work mixes two changes in one conflict.

1. Check the state:
   ```powershell
   git status --short
   git fetch upstream
   git fetch origin
   ```
2. Update the local `main` from the fork:
   ```powershell
   git switch main
   git merge --ff-only origin/main
   ```
3. Create a sync branch:
   ```powershell
   git switch -c sync/upstream-YYYY-MM-DD
   ```
4. Read what comes in:
   ```powershell
   git log --oneline main..upstream/main
   git diff --stat main...upstream/main
   ```
5. Merge. Do not rebase or squash, so that the next merge base stays correct.
   ```powershell
   git merge --no-ff upstream/main
   ```
6. Resolve each conflict:
   - In upstream code, take the upstream version, then re-apply the fork seam.
   - In a fork-only file, keep the fork version.
   - In `app/build.gradle`, take the upstream `versionCode` and `versionName`. Keep the fork block at the end.
   - In an i18n catalogue, keep every key from both sides. Then check key parity across all catalogues.
7. Verify:
   ```powershell
   .\gradlew.bat testDebugUnitTest assembleDebug lintDebug
   pwsh -NoProfile -File tools/check_agent_kb.ps1
   ```
8. Add a `sync` entry to `.agents/log.md` with the upstream commit range and the conflicts.
9. Push the branch to `origin` and open a pull request against `origin/main` only when the user asks.

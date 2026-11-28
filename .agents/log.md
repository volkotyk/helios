# Agent Log

Newest entries go at the top. Format: [kb_maintenance_rules.md](rules/kb_maintenance_rules.md) § Log.

## [2026-11-28] feature | larger tile name over a small state (test release for the auto-update)
- New pure class `TileEmphasis` (+ `TileEmphasisTest`): a FIT tile of type `entity`, `tile` or `light` whose entity domain is `automation`, `script`, `scene`, `switch`, `input_boolean`, `light`, `fan`, `button` or `input_button`, without `attribute`, shows its name at 19..26 units (fitted to the width beside the icon, text color) and its state at 17. Sensors, binary sensors, locks, covers and attribute readings keep the upstream sizes.
- Purpose: the owner asked for it, and its release is the first one that a clock on 0.21.186 should find through the automatic update check.
- Verified: `testDebugUnitTest assembleDebug lintDebug` with the CI properties. Not checked on a clock: long names, German and Ukrainian names, the 3x3 square layout.
- roles: Frontend Developer, Android Developer, QA Engineer
- fork-only: yes; upstream files touched: `DashboardView.java`

## [2026-11-27] feature | automatic update check with a one-tap install
- New pure class `UpdateCheck` (+ `UpdateCheckTest`): first check 2 min after the service starts, then every 6 h; each failure doubles the wait from 15 min up to 6 h; never sooner than 2 min. It offers a stored version only while it is newer than `BuildConfig.VERSION_NAME`, so an installed update clears the offer.
- New glue `UpdateWatch` in `HeliosService`: calls the new `Updater.latest()` (metadata only, the same `ReleaseInfo.parse` and `Updater.decision`, no status, no download) on the `network` executor. Prefs in `helios`: `update_check_off`, `update_checked_at`, `update_check_failures`, `update_found`. The attempt time is stored before the request, so a restart loop cannot exceed the limit.
- `DashboardView`: button `Aktualizuj X.Y.Z` in the bar left of the HA icon; a tap runs `HeliosService.update()` (`Updater.run()`, all checks unchanged). Touch area: the bar and the gap under it, 60 units high (under the 64-unit guideline; the bar is 52). Collapsed bar: `TopBar.shown` has a 5-argument form, so each find brings the bar back for 8 s.
- `MainActivity.onDestroy` clears the update listener next to the music and appearance listeners, so the service does not keep the old dashboard.
- Menu: `Aktualizacja Heliosa` reads `Zainstaluj nową wersję Heliosa: X.Y.Z` while there is an offer; new entry `Sprawdzanie aktualizacji: włączone/wyłączone`.
- Security review: no new host, permission or install path. A non-200 answer (also a GitHub rate limit) reads as "nothing new" and keeps an earlier find; only an `IOException` or another exception counts as a failure.
- Verified: `testDebugUnitTest assembleDebug lintDebug`, also with `-PheliosVersionName=0.21.999 -PheliosVersionCode=100999 -PheliosReleaseRepository=volkotyk/helios`. Not checked on a clock: the bar button, the system install confirmation, the behavior after reboot.
- roles: Android Developer, Frontend Developer, Security Analyst, Localization Translator, QA Engineer, Critic
- fork-only: yes; upstream files touched: `Updater.java`, `HeliosService.java`, `MainActivity.java`, `DashboardView.java`, `NavigationMenu.java`, `en.json`, `de.json`
- pages: `.agents/memory/CURRENT.md`, `.agents/rules/fork-upstream-sync.md`, `.agents/references/System_Map.md`

## [2026-11-27] fix | first main CI run failed in ReleaseSourceTest
- Root cause: `ReleaseSourceTest` compared `ReleaseInfo.HELIOS.apiUrl()` with the literal `SychPL/helios`; the main build passes `-PheliosReleaseRepository=volkotyk/helios`. The PR run did not pass the properties, so it stayed green. No release was published.
- Fix: the test expects `BuildConfig.RELEASE_REPOSITORY`. Pull request runs now use the same Gradle properties as main; only signing and publishing differ.
- Verified locally: the full CI command with the release properties, and the tests without them.
- roles: Debugger, DevOps / Build Engineer, QA Engineer
- fork-only: yes; upstream files touched: `ReleaseSourceTest.java`

## [2026-11-27] release | fork signing key and CI secrets
- Generated an RSA 4096 PKCS12 key (alias `helios`, 10000 days, `CN=Helios, O=volkotyk/helios`). Certificate SHA-256 `d51bedb4f1df539b9148a342db8d703694bf956f43f3af460dc5cd377c46aa19`.
- Set `HELIOS_KEYSTORE_BASE64`, `HELIOS_KEYSTORE_PASSWORD`, `HELIOS_KEY_ALIAS`, `HELIOS_KEY_PASSWORD` on `volkotyk/helios`. Checked the base64 round trip and an `apksigner sign` + `verify` of the debug APK with the same flags as the workflow.
- The keystore and passwords stay outside the repository with the owner.
- roles: DevOps / Build Engineer, Security Analyst
- fork-only: yes; upstream files touched: none
- pages: `.agents/memory/CURRENT.md`, `.agents/skills/project-release-checklist/SKILL.md`

## [2026-11-27] release | CI workflow builds, signs and publishes the APK from main
- `.github/workflows/android.yml`: tests, lint and `assembleDebug` on pull requests and pushes; on `main` it re-signs the debug APK with the `HELIOS_KEYSTORE_*` secrets and creates release `vX.Y.Z` with `helios-X.Y.Z.apk`.
- Version per build: `X.Y` from `versionName`, patch = `git rev-list --count HEAD`, `versionCode` = 100000 + patch. A second run on the same commit skips the existing release.
- Fork block at the end of `app/build.gradle`: `-PheliosVersionName`, `-PheliosVersionCode`, `-PheliosReleaseRepository` (validated `owner/name`, written to `BuildConfig.RELEASE_REPOSITORY`). `ReleaseInfo.HELIOS` reads it, so fork releases reach fork clocks. Verified with `aapt2 dump badging` and the generated `BuildConfig`.
- Found: on this Windows host Gradle needs `-Djdk.net.unixdomain.tmpdir` (Setup_And_Commands.md).
- roles: DevOps / Build Engineer, Security Analyst, Critic
- fork-only: yes; upstream files touched: `app/build.gradle`, `ReleaseInfo.java`
- pages: `.agents/references/Setup_And_Commands.md`, `.agents/skills/project-release-checklist/SKILL.md`, `.agents/references/System_Map.md`

## [2026-11-27] feature | hideable top bar
- New pure class `TopBar` (+ `TopBarTest`). Menu entry `Górny pasek: widoczny/ukryty`, pref `top_bar_hidden`, default off (upstream behavior).
- Collapsed: the grid starts at the top edge; a still hold of 1 s anywhere opens the menu (the tile under the finger gets `ACTION_CANCEL`); an issue, or a status message for 8 s, shows the bar over the grid.
- Not changed: climate panel and fullscreen music geometry (they keep the strip under the bar height).
- roles: Android Developer, Frontend Developer, QA Engineer, Critic
- fork-only: yes; upstream files touched: `DashboardView.java`, `MainActivity.java`, `NavigationMenu.java`, `en.json`, `de.json`

## [2026-11-27] feature | Ukrainian interface
- New `app/src/main/assets/i18n/uk.json` (same key set as `en.json` and `de.json`). `Translations.CATALOGUES` replaces the hard-coded `en`/`de` pairs; `I18n.init` loads each catalogue separately, so one broken file does not drop the others.
- Menu cycle: auto, pl, en, de, uk. Locale `uk_UA` for dates and numbers.
- New test `everyCatalogueTranslatesTheSameKeys` checks key parity, empty values and edge spaces.
- roles: Localization Translator, Android Developer, QA Engineer
- fork-only: yes; upstream files touched: `I18n.java`, `Translations.java`, `TranslationsTest.java`, `NavigationMenu.java`

## [2026-11-27] fix | Assist speech no longer stops after 30 s (from english-ui)
- Ported `markgraham924/helios` `fda65d1`: `SpeechPlaybackWatchdog` with separate preparation (30 s), stall (45 s) and duration (+60 s, or 10 min when unknown) limits. Kept the upstream `I18n.t("Odpowiadam…")` state. Attribution in `NOTICE`.
- Reviewed and not ported: `1e20795` (wide clock tile, changes the upstream layout without a defect), `1fa0316` and `lenovo-spotify-toggle.sh` (a Magisk service for one rooted clock). Upstream 0.21 already has `995ec6b`, `c61dcaf`, `20a1b92`.
- roles: Android Developer, Code Reviewer
- fork-only: yes; upstream files touched: `AssistClient.java`, `NOTICE`

## [2026-11-27] init | agent KB adopted from CrabbyGo3 + Paramodo
- Added `AGENTS.md`, `CLAUDE.md` (`@AGENTS.md`), `.agents/` (index, memory, 7 rules, 14 roles, 5 references, 9 skills), `.claude/skills/` stubs, `tools/check_agent_kb.ps1` and `tools/sync_claude_skill_stubs.ps1`.
- Android structure from CrabbyGo3, retargeted from Kotlin, AndroidX and Hilt to Java 8 with platform APIs only. Style and stance, gates, Critic role and third-party skills from Paramodo.
- New rule `fork-upstream-sync.md`: small isolated diffs against `SychPL/helios`, remotes, merge procedure, known conflict points.
- Dropped Figma, AWS, CUDA, SketchUp, Ruby, Modus, Linear, Lemon Squeezy, SonarQube, Graphify, Kiro, OpenAI and Claude API, and ASO material. Removed the sample dependency manifests from `dependency-auditor`.
- Found: `ReleaseInfo.HELIOS` points the updater at `SychPL/helios`; no test checks i18n key parity; the `AlertsDrawer` plural rule is simplified (recorded in the localization role).
- roles: Project Manager, Architect, Technical Writer, Critic
- fork-only: yes; upstream files touched: none
- pages: all files listed above

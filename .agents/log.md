# Agent Log

Newest entries go at the top. Format: [kb_maintenance_rules.md](rules/kb_maintenance_rules.md) § Log.

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

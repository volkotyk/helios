---
name: project-release-checklist
description: Use this skill before you tag or publish a Helios APK release on GitHub, prepare a release workflow, or change versionCode, versionName, the release asset name, or the signing key. It checks the contract that the in-app updater (Updater.java, ReleaseInfo.java) enforces on every clock.
---

# project-release-checklist

Use this skill before a Helios release, or before a change to the release workflow.

## Warnings

- The signing key must stay the same for every release. Android refuses an update that is signed with a different key, and the clock then keeps the old version. Releases are debug builds (see `docs/INSTALL.md`). The fork workflow `.github/workflows/android.yml` re-signs the debug APK with the key from the `HELIOS_KEYSTORE_*` and `HELIOS_KEY_*` secrets.
- Never commit a keystore. `.gitignore` ignores `*.keystore` and `*.jks`.
- `ReleaseInfo.HELIOS` reads `BuildConfig.RELEASE_REPOSITORY`. A local build keeps `SychPL/helios`. The fork workflow passes `-PheliosReleaseRepository=<owner/name>` (see the fork block at the end of `app/build.gradle`).
- The fork workflow sets the version per build: `X.Y` from `versionName`, patch = commit count of `main`, `versionCode` = 100000 + patch. Do not rewrite `main` history: a lower commit count gives a lower version, and the updater then ignores the release.

## Updater contract

Source: `ReleaseInfo.java`, `Updater.java`, `ReleaseInfoTest.java`, `UpdaterTest.java`.

| Check | Rule |
|---|---|
| Release state | Not a draft, not a prerelease |
| Tag | `vX.Y.Z`, three numeric parts |
| Asset | Exactly one asset named `helios-X.Y.Z.apk` |
| Asset URL | `https` on `github.com` or `objects.githubusercontent.com`; redirects stay on `github.com` or `*.githubusercontent.com`, five hops at most |
| Size | Larger than 0 and at most 40 MiB (`ReleaseInfo.MAX_BYTES`) |
| Package | `pl.mateusz.helios` |
| Version | `versionName` equals `X.Y.Z` of the tag, and `versionCode` is higher than the installed one |

## Checklist

1. Bump `versionCode` and `versionName` in `app/build.gradle`. Keep `versionName` equal to the tag without the `v`.
2. Check the pairing with `ha-helios`: a feature release uses the same major and minor number (README § Version compatibility).
3. Run `.\gradlew.bat testDebugUnitTest assembleDebug lintDebug`. All three must pass.
4. Rename `app/build/outputs/apk/debug/app-debug.apk` to `helios-X.Y.Z.apk`. Upload only that one asset.
5. Confirm that the APK is signed with the same certificate as the previous release (`apksigner verify --print-certs`).
6. Install it over the previous version on the clock (`adb install -r`). A signature mismatch fails here, not on the user's clock.
7. Confirm that docs and setup commands are current.
8. Run `pwsh -NoProfile -File tools/check_agent_kb.ps1` when docs, commands or release rules change.
9. Add a `release` entry to `.agents/log.md`.

## Output

- Release readiness status.
- Commands run, with results.
- Blocking issues.
- Tag, asset name and certificate digest.

### Role: DevOps / Build Engineer

**Activated by**: Gradle, the wrapper, lint, CI workflows, release builds, APK signing, PowerShell tools.

**Mindset**: Reproducible builds, fast feedback, a stable signing key, and no secret in a log.

**Build facts**:
- Gradle 8.14.3 (wrapper), Android Gradle Plugin 8.13.2, build-tools 36.0.0, compileSdk 35.
- Groovy DSL: `build.gradle`, `settings.gradle`, `app/build.gradle`. No version catalog.
- `local.properties` points at the Android SDK. Do not commit it.
- Releases are debug APKs on purpose (`docs/INSTALL.md`). The updater accepts only the asset `helios-X.Y.Z.apk` of a final `vX.Y.Z` release.
- The repository has no `.github/` workflow today. A fork CI workflow is planned (see `.agents/memory/CURRENT.md`).

**Responsibilities**:
- Keep `./gradlew testDebugUnitTest assembleDebug lintDebug` green.
- Keep the release signing key stable across releases. A CI build must use the existing keystore from a secret, never a fresh debug keystore.
- Keep scripts portable and documented in [Setup_And_Commands.md](../references/Setup_And_Commands.md).
- Give CI tokens the smallest permissions.

**Default behaviors**:
- Use `project-release-checklist` before a release or a release-workflow change.
- Use `dependency-auditor` before a dependency change.
- Use `gh-fix-ci` for a failing GitHub Actions check.
- Put a workflow in a new fork-only file. Do not edit upstream build files for CI needs when a workflow step can do it.

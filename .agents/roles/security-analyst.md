### Role: Security Analyst

**Activated by**: tokens, secrets, pairing, permissions, the updater, package installs, the clock tools bridge, network calls, keystores, privacy.

**Mindset**: Assume that any host on the LAN can talk to the clock, and that a log or a screenshot can leak.

**Assets**:
- Home Assistant connection (URL and token). The app stores it after pairing in the `helios` shared preferences, key `connection` (SPEC 0.10).
- Music Assistant token. `MusicAssistantClient` sends it only to the configured host and never logs it.
- Local tool credentials: `ha.json`, `ma.json`, `provision.json`, `bridge.json` under `.local/`. `.gitignore` ignores these names everywhere.
- The APK signing key and any `*.keystore` or `*.jks` file.
- Voice audio and Assist text. `AssistClient` captures audio only during a user-triggered session.
- The app event log `files/assist-events.jsonl` and the optional `diagnostics_url` post in `MainActivity.onEvent`.

**Trust boundaries**:
- Clock and Home Assistant: WebSocket (`HaDashboardClient`), the `helios/*` device channel with an allowlisted command handler (`HeliosDeviceClient`), pairing over plain HTTP (`PairingClient`, `AddressInput` allows only http base URLs).
- Clock and Music Assistant: WebSocket API and Sendspin audio (`MusicAssistantClient`, `SendspinClient`).
- Clock and GitHub: release metadata and the APK download (`ReleaseInfo`, `Updater`), with host allowlists and a package and version check before install.
- Helios and the clock tools app: `ToolsBridge`, `ToolsTrust`, `ToolsCall`, and the read-only `ApkProvider`.
- ADB over Wi-Fi, when the user turns it on.

**Responsibilities**:
- Review every change that crosses a boundary above.
- Check that no token reaches a log, the event log, telemetry or `diagnostics_url`.
- Check that a new device-channel command is in the allowlist and is safe for any HA user who can send it.
- Check that the updater still verifies the package name, the version and the hosts.

**Default behaviors**:
- Flag secret defaults, sensitive logs and broad permissions.
- Check replay, spoofing, tampering and denial-of-service paths.
- Use `security-threat-model` for a new trust boundary.

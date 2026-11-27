### Role: Architect

**Mindset**: Keep the system understandable, keep the fork diff small, and state trade-offs honestly.

**Responsibilities**:
- Keep the split between pure rule classes (JVM-testable) and Android glue classes (`MainActivity`, `HeliosService`, Views).
- Review changes to contracts: the dashboard schema in `DashboardSpec`, the `helios/*` device channel in `HeliosDeviceClient`, the pairing API in `PairingClient`, the updater contract in `ReleaseInfo` and `Updater`.
- Keep `HeliosService` as the owner of the transport, the device channel, the dock binder and the volume poller, independent of the activity.
- Accept a new dependency only with a reason. The app has three runtime libraries and no AndroidX on purpose.
- Record durable decisions in `.agents/log.md` and in [System_Map.md](../references/System_Map.md).

**Default behaviors**:
- Prefer a simple explicit contract.
- Check whether a new abstraction removes real complexity. A seam that a JVM test needs is a valid reason.
- Prefer a new file or a narrow seam to a rewrite of upstream code.

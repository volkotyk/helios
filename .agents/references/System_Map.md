---
title: "System Map"
type: synthesis
sources:
  - "app/src/main/java/pl/mateusz/helios/ (class Javadoc headers)"
  - "app/src/main/AndroidManifest.xml"
  - "app/build.gradle"
  - "README.md"
related:
  - "Context_Navigation.md"
  - "Code_Style.md"
  - "Testing_Guidelines.md"
updated: "2026-11-27"
confidence: medium
---

# System Map

Helios is a Home Assistant dashboard and voice satellite for the Lenovo Smart Clock 2 (Android 10). It runs as the HOME launcher. All source files are in `app/src/main/java/pl/mateusz/helios/` (about 75 files, one package). Each class Javadoc names the `docs/SPEC-*.md` section that defines it. Read that section before you change the class.

## Process and lifecycle

| Class | Role |
|---|---|
| `HeliosApp` | `Application` subclass. |
| `MainActivity` | The HOME and LAUNCHER activity, landscape. Binds `HeliosService`, owns `DashboardView`, onboarding and pairing UI, voice sessions, the screensaver, and the event log `files/assist-events.jsonl`. |
| `HeliosService` | Foreground service. Owns the HA transport, the device channel, the dock binder and the volume poller, independent of the activity. Also owns the music session, Sendspin audio and the Music Assistant client. |
| `UpdateReceiver` | Manifest receiver for installer status; the state lives in prefs and in `PackageInstaller`. |
| `ApkProvider` | Read-only content provider for one file. It replaces AndroidX `FileProvider`. |

## Dashboard and cards

| Class | Role |
|---|---|
| `DashboardSpec` | The grid dashboard contract (`helios.version` 2 to 6). Pure data, validated as a whole. |
| `DashboardView` | Top bar plus a grid of tiles from `DashboardSpec`: 4 x 3 on the clock, 3 x 3 on a square screen. |
| `CardDefinition`, `CardBodies`, `ActionPolicy` | Card type schema, the pure text of each card, and the only place that turns a tap into an HA service call or a panel. |
| `EntityStates` | Decoder for HA `subscribe_entities` snapshots and diffs. |
| `ClimateModel`, `ClimateFlow`, `ClimatePanel`, `ClimatePanelGeometry` | Thermostat card and panel (SPEC 0.19). |
| `AlertsModel`, `AlertsDrawer` | Alerts card and its list (SPEC 0.20). |
| `CoverText`, `CoverPanelGeometry` | Covers card and panel (SPEC 0.9). |
| `Forecast`, `Tomorrow`, `WeatherLabels`, `DayPlan` | Weather and calendar data. |
| `DetailsDialog`, `NavigationMenu` | Tile details and the fixed local menu. The menu never comes from HA. |
| `Theme`, `Appearance`, `IconView`, `MdiIcons`, `TextFit`, `ClockLayout`, `PageSwipe`, `PageDots`, `CropMath`, `ArtworkLoader` | Colors, backgrounds, icons, text fitting and paging. |
| `ScreensaverPolicy` | When the panel gives way to the night clock (SPEC 0.13). |

## Home Assistant transport and pairing

| Class | Role |
|---|---|
| `HaDashboardClient` | One reconnecting WebSocket session to HA, shared by every module. |
| `HeliosDeviceClient`, `Telemetry` | The clock side of the `helios/*` channel: telemetry snapshots and an allowlisted command handler. |
| `HaDiscovery`, `AddressInput`, `PairingClient` | mDNS discovery, typed address parsing, and `/api/helios/pair` (SPEC 0.10). |
| `ConnectionController`, `ConnectionMerge` | Connection changes from pairing and `connection` events. |

## Music

| Class | Role |
|---|---|
| `MusicAssistantClient` | Music Assistant WebSocket API (2.10.3). |
| `SendspinClient`, `AudioSink`, `AudioTrackSink`, `ClockOffset` | Sendspin audio player for Music Assistant. |
| `MusicSession`, `QueuePause`, `DeviceVolume` | Transport state with audio focus, pause handling, device volume. |
| `MusicOverlay`, `MusicLibraryDialog`, `RecentPlays`, `AccentColor`, `OverlayGeometry`, `FullscreenGeometry` | Music UI. |

## Voice

| Class | Role |
|---|---|
| `AssistClient` | One user-triggered Assist session. Audio is captured only during the session. |
| `WakeWordListener`, `AdaptiveDecimator` | Local wake word. The decimator repairs the sample flood of the vendor audio HAL. |
| Native and model | `app/src/main/jniLibs/armeabi-v7a/libmicrowakeword.so`, `app/src/main/assets/wakeword/okay_nabu.tflite`, with licenses and `provenance.json`. |

## Device, updater, tools, i18n

| Class | Role |
|---|---|
| `DockController`, `LampMath` | Dock lamp and charger through the OEM accessory binder (`docs/lamp-control.md`). |
| `Updater`, `ReleaseInfo`, `Version` | GitHub Releases updater. `ReleaseInfo.HELIOS` reads `BuildConfig.RELEASE_REPOSITORY` (default `SychPL/helios`; the fork CI sets its own repository). |
| `ToolsBridge`, `ToolsCall`, `ToolsTrust`, `ToolsState`, `ToolsMenu` | Bridge to the clock tools app `pl.mateusz.clockadbprobe` from `SychPL/smartclock2tool` (SPEC 0.12). |
| `I18n`, `Translations` | Language choice and catalogues from `app/src/main/assets/i18n/`. Polish keys are the fallback. |

## Outside the app

| Path | Content |
|---|---|
| `app/src/test/java/pl/mateusz/helios/` | JVM unit tests (JUnit 4 + `org.json`). |
| `ha/` | Home Assistant YAML dashboards and packages, with pytest checks in `ha/tests/`. |
| `tools/` | Python and PowerShell helpers for the clock and HA. They read credentials from the ignored `.local/` directory. |
| `docs/` | Polish SPEC and PLAN files (the feature contract), `INSTALL.md`, HA integration notes. |
| `artifacts/`, `probes/` | Test results and device probes from upstream development. |

Related repositories: `SychPL/ha-helios` (the HA integration; a feature release shares the major and minor number with the app) and `SychPL/smartclock2tool` (root, ADB over Wi-Fi and the clock tools).

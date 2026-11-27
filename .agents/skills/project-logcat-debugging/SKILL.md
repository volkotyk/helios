---
name: project-logcat-debugging
description: Use this skill when the user asks to connect to the Lenovo Smart Clock 2 over adb, read logcat, read the Helios event log, or diagnose Helios runtime problems on the device, such as startup crashes, ANRs, microphone or wake-word failures, audio focus, Home Assistant or Music Assistant connection loss, updater installs, or dock lamp behavior. It resolves adb from project settings instead of PATH and turns noisy device logs into findings.
---

# project-logcat-debugging

Use this skill when a Helios investigation needs evidence from the device.

## Roles

- Android Logcat Debugger
- Debugger
- QA Engineer
- DevOps / Build Engineer when the SDK, the install or the adb connection is the problem
- Security Analyst when the evidence can contain Home Assistant or Music Assistant tokens, addresses or voice text

## Facts about the device

- The stock Smart Clock 2 has no ADB. ADB over Wi-Fi exists only after `ROOT + ADB` in `SychPL/smartclock2tool`. A power cycle removes it. See `docs/INSTALL.md` § B.
- While ADB over Wi-Fi is on, any host on the LAN can connect as `shell`. Tell the user to turn it off after the session.
- Helios writes almost nothing to logcat. In `app/src/main/java/` there is one `android.util.Log.e` call (tag `Helios`, in `I18n.java`). Crashes still appear as `AndroidRuntime` lines.
- The app event log is `files/assist-events.jsonl` in the app data directory. `MainActivity.onEvent` appends one JSON row per event and deletes the file above 2 000 000 bytes. The release APK is a debug build, so `run-as` can read it.

## Workflow

1. Read the current dirty state. Do not revert unrelated work.
2. Connect to the clock. The serial is `<ip>:5555`.
   ```powershell
   adb connect "${env:CLOCK_IP}:5555"
   ```
3. Let the helper resolve `adb.exe`. It reads `sdk.dir` from `local.properties`, then `ANDROID_HOME`, `ANDROID_SDK_ROOT`, `%LOCALAPPDATA%\Android\Sdk`, then `PATH`. It does not print `local.properties`.
   ```powershell
   .\.agents\skills\project-logcat-debugging\scripts\capture-logcat.ps1 -ListDevices
   ```
4. Check the install and running state. The default package is `pl.mateusz.helios`.
   ```powershell
   .\.agents\skills\project-logcat-debugging\scripts\capture-logcat.ps1 -DeviceSerial "${env:CLOCK_IP}:5555" -Seconds 0
   ```
5. For a startup problem, clear the buffer, launch `.MainActivity` and capture.
   ```powershell
   .\.agents\skills\project-logcat-debugging\scripts\capture-logcat.ps1 -DeviceSerial "${env:CLOCK_IP}:5555" -Clear -Launch -Seconds 12 -MinLevel W
   ```
6. For an interactive reproduction, clear the buffer, ask the user to do the action, then follow.
   ```powershell
   .\.agents\skills\project-logcat-debugging\scripts\capture-logcat.ps1 -DeviceSerial "${env:CLOCK_IP}:5555" -Clear -Follow -MinLevel I
   ```
7. Read the tail of the app event log. Do not copy the whole file.
   ```powershell
   adb -s "${env:CLOCK_IP}:5555" shell run-as pl.mateusz.helios tail -n 200 files/assist-events.jsonl
   ```
   Do not confuse it with `tools/read_assist_log.py`. That tool reads `helios_assist_test.jsonl` of the probe package `pl.mateusz.clockadbprobe`.
8. Put app-process findings first:
   - `AndroidRuntime`, `FATAL EXCEPTION`, `ANR`
   - stack frames under `pl.mateusz.helios`
   - `AudioRecord`, `AudioTrack`, audio focus and `PackageInstaller` errors
   - event-log rows such as `microphone_started`, `microphone_released`, `wake_error`, `service_error`, `screensaver_on`, `screensaver_off`, `dashboard_configured`
9. Report vendor and system noise separately: Google Assistant and Lenovo OEM services, graphics and HWUI warnings, Play services lines that do not name the Helios package.
10. Read source only after a concrete log line or stack frame points at it.
11. Verify a fix with the same capture flow. Record a durable lesson in `.agents/log.md`.

## Security and privacy

- Treat logcat and `assist-events.jsonl` as sensitive. They can contain addresses, entity names, voice text and connection detail.
- Summarize logs. Quote only the smallest lines that prove a finding.
- Never copy `ha.json`, `ma.json`, `provision.json` or `bridge.json` contents into a report.
- Keep a saved log only when the user asks. Put it under the ignored `.local/` directory and say that it is sensitive.

## Report format

- Device serial and package state.
- Reproduction action and capture command.
- Findings, ordered by severity.
- App defect or vendor and system noise.
- Likely root cause with source references.
- Verification result or the next command.

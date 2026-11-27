---
title: "Code Style"
type: concept
sources:
  - "app/src/main/java/pl/mateusz/helios/"
  - "app/src/test/java/pl/mateusz/helios/"
  - "app/build.gradle"
related:
  - "System_Map.md"
  - "Testing_Guidelines.md"
  - "../rules/fork-upstream-sync.md"
updated: "2026-11-27"
confidence: medium
---

# Code Style

## The upstream style, described honestly

The upstream code is dense. It optimizes for the number of lines on a screen, not for a formatter.

- Short methods sit on one line: `static int brightnessToLevel(int brightness){return ...;}`.
- One line often holds several statements, several field declarations (`final String state,time;`) or a whole small class.
- Spaces around operators and after commas are often missing.
- Classes are `final` and package-private by default. Utility classes have a private constructor. Small records are `static final` nested classes with `final` fields.
- Every class has a Javadoc header that states its contract and names its SPEC section, for example `(SPEC 0.19 pkt 5.1)`.
- Comments explain why, not what. `ponytail:` marks a deliberate simplification and the condition to revisit it.
- Tests use the same density: several asserts on one line, one method per behavior group.

## Rules for changes

- Match the style of the file you edit. Do not reformat, re-indent or reorder upstream code. A style-only diff makes every upstream merge expensive.
- A new fork-only file may use the same dense style or a plain conventional layout. Keep one style inside one file.
- Java 8 language level: no `var`, no records, no switch expressions, no text blocks. Lambdas, method references and `java.util.function` are in use (minSdk 27).
- Use `org.json` for JSON. Use `HttpURLConnection` for HTTP, with explicit timeouts and bounded bodies.
- Put decisions in pure classes or behind a small interface. Keep Android calls in glue classes.
- Route user-facing text through `I18n.t("polski tekst")`. The Polish string is the key.
- Treat an unknown value as unknown. Never turn a missing HA value into `false` or `0`.

## Error handling

- Fail loudly for programmer errors.
- Show the user a short, translated reason for network, pairing and configuration errors.
- Do not swallow an exception without a reason. The upstream code has a few `catch(Exception ignored)` blocks on best-effort paths (for example the diagnostics post). Do not copy that pattern into a decision path.
- Never put a token or a raw sensitive payload into a log, the event log or an exception message.

## Dependencies

- Prefer the platform and the three existing runtime libraries.
- A new dependency needs an Architect decision and a `dependency-auditor` check. AndroidX is out of scope (`android.useAndroidX=false`).

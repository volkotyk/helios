# Tool Safety

Use tools in a way that preserves the user's work and the project state.

## Filesystem

- Never revert user changes unless the user asks.
- Keep each edit inside the scope of the task.
- Prefer patch-style edits for existing files.
- Verify the resolved path before you delete a folder recursively.
- Put temporary files, logs and reports under the ignored `.local/` directory.

## Shell

- Prefer targeted commands to broad commands.
- Use fast search tools such as `rg` when they are available.
- Run the smallest relevant test first.
- Do not echo secrets or full sensitive payloads. Never print `local.properties`, `.local/*.json`, `ha.json`, `ma.json`, `provision.json` or `bridge.json`.

## Git

- Inspect `git status` before you stage or commit.
- Do not rewrite history unless the user asks.
- Name unrelated dirty files. Do not touch them.
- Do not commit `.claude/settings.local.json`, keystores, APKs or anything under `.local/`.

## Device

- Run a state-changing `adb` command (`install`, `uninstall`, `pm clear`, `pm grant`, `reboot`) only when the task needs it and the user expects it.
- ADB over Wi-Fi exposes the clock to every host on the LAN. Tell the user to turn it off after the session.

## Network and external sources

- Use primary sources for external facts.
- Check current documentation for fast-moving tools.
- Summarize linked material. Do not paste long verbatim excerpts.

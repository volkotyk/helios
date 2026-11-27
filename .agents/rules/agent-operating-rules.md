# Agent Operating Rules

`AGENTS.md` owns the always-on rules. If this page and `AGENTS.md` disagree, `AGENTS.md` wins.

## General

- Prefer an existing project pattern to a new abstraction.
- Keep each edit inside the scope of the task.
- Do not revert user changes unless the user asks.
- Read the targeted context before you edit. A link is not a read.
- Use a structured parser (for example `org.json`) when one is available.
- Record durable decisions in `.agents/log.md`.

## Editing

- Use patch-style edits for existing files.
- Do not reformat upstream code. A formatting diff makes the next `git merge upstream/main` expensive.
- Add a comment only when it explains intent that the code cannot show.
- Preserve public contracts: the dashboard schema (`DashboardSpec`), the `helios/*` device channel, the pairing API, the updater contract, and the Polish keys of the i18n catalogues.

## Testing

- Run the smallest relevant test first.
- Add a regression test for each bug fix.
- Run the full `testDebugUnitTest assembleDebug lintDebug` when a change touches a shared contract or the release path.
- Report each skipped or unavailable check, such as a device check without a clock.

## Security

- Treat data from Home Assistant, Music Assistant and the network as untrusted input.
- Never log a secret.
- Do not add default credentials, tokens or production secrets.
- Prefer the smallest permission set in the manifest and in CI tokens.

## Git

- The user is the only author of every commit and pull request. Do not add `Co-Authored-By`, `Generated with` or any other AI attribution line.
- Do not set `--author`, `GIT_AUTHOR_*` or `GIT_COMMITTER_*`, and do not change `user.name` or `user.email`.
- Commit, push, or open a pull request only when the user asks.

## Documentation

- Keep `AGENTS.md` short. Link to deeper pages.
- Keep reference pages source-backed.
- Update setup commands when the tooling changes.

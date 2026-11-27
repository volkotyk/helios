# Third-Party Skills

Helios copied these skills from the Paramodo agent KB on 2026-11-27. Paramodo copied them from the upstream repositories below. Keep the license file in each skill folder.

| Skill | Upstream | Upstream commit | License | Helios changes |
|---|---|---|---|---|
| `gh-fix-ci` | https://github.com/openai/skills | `49f948faa9258a0c61caceaf225e179651397431` | Apache License 2.0, [gh-fix-ci/LICENSE.txt](gh-fix-ci/LICENSE.txt) | Added a Helios note to `SKILL.md` |
| `security-threat-model` | https://github.com/openai/skills | `49f948faa9258a0c61caceaf225e179651397431` | Apache License 2.0, [security-threat-model/LICENSE.txt](security-threat-model/LICENSE.txt) | Added a Helios note to `SKILL.md` |
| `dependency-auditor` | https://github.com/alirezarezvani/claude-skills | `19392f7a08264ed00486a251f5b2098321771f94` | MIT, [dependency-auditor/LICENSE](dependency-auditor/LICENSE) | Added a Helios note; removed the sample manifests in `assets/` and `test-project/`; edited `README.md` § Sample Files |

Project-owned skills (`anxiety`, `project-*`) have no third-party license. `anxiety` comes from the Paramodo KB. The `project-*` skills come from the CrabbyGo KB and are rewritten for Helios.

Before you import another third-party skill, record its source, commit, license and review here.

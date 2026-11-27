# check_agent_kb.ps1 - lints the Helios agent knowledge base.
#
# Checks: required files, CLAUDE.md content, AGENTS.md size, local Markdown links in the agent
# surface (AGENTS.md, CLAUDE.md, .agents/, .claude/), frontmatter of KB pages, role and skill
# routing, placeholder tokens, log order, and the Claude Code skill stubs.
# Exit code 1 when an error exists. Warnings do not fail the run.
#
# Adapted from the CrabbyGo agent KB.
param(
  [string]$Root = (Split-Path -Parent $PSScriptRoot)
)

$ErrorActionPreference = 'Stop'
Set-StrictMode -Version Latest

$rootPath = (Resolve-Path -LiteralPath $Root).Path.TrimEnd('\', '/')
$errors = [System.Collections.Generic.List[string]]::new()
$warnings = [System.Collections.Generic.List[string]]::new()

function Get-Relative([string]$Path) {
  return $Path.Substring($rootPath.Length).TrimStart('\', '/').Replace('\', '/')
}

function Read-Text([string]$Path) {
  return [System.IO.File]::ReadAllText($Path).Replace("`r`n", "`n")
}

# 1. Required files.
$required = @(
  'AGENTS.md',
  'CLAUDE.md',
  '.agents/index.md',
  '.agents/log.md',
  '.agents/memory/CURRENT.md',
  '.agents/rules/agent-operating-rules.md',
  '.agents/rules/fork-upstream-sync.md',
  '.agents/rules/kb_maintenance_rules.md',
  '.agents/rules/role-activation.md',
  '.agents/rules/source-traceability.md',
  '.agents/rules/tool-safety.md',
  '.agents/references/Context_Navigation.md',
  '.agents/references/System_Map.md',
  'tools/sync_claude_skill_stubs.ps1'
)
foreach ($path in $required) {
  if (-not (Test-Path -LiteralPath (Join-Path $rootPath $path) -PathType Leaf)) {
    $errors.Add("Missing required file: $path")
  }
}

# 2. CLAUDE.md imports AGENTS.md and holds nothing else.
$claude = Join-Path $rootPath 'CLAUDE.md'
if (Test-Path -LiteralPath $claude -PathType Leaf) {
  if ((Read-Text $claude).Trim() -ne '@AGENTS.md') {
    $errors.Add('CLAUDE.md must contain exactly one line: @AGENTS.md')
  }
}

# 3. AGENTS.md stays short: it loads on every task.
$agents = Join-Path $rootPath 'AGENTS.md'
if (Test-Path -LiteralPath $agents -PathType Leaf) {
  $lineCount = (Read-Text $agents).TrimEnd("`n").Split("`n").Count
  if ($lineCount -ge 150) { $errors.Add("AGENTS.md has $lineCount lines; keep it under 150") }
}

# 4. Local Markdown links in the agent surface.
$surface = [System.Collections.Generic.List[System.IO.FileInfo]]::new()
foreach ($file in @('AGENTS.md', 'CLAUDE.md')) {
  $full = Join-Path $rootPath $file
  if (Test-Path -LiteralPath $full -PathType Leaf) { $surface.Add((Get-Item -LiteralPath $full)) }
}
foreach ($dir in @('.agents', '.claude')) {
  $full = Join-Path $rootPath $dir
  if (Test-Path -LiteralPath $full) {
    Get-ChildItem -Recurse -File -Force -LiteralPath $full -Filter *.md |
      Where-Object { $_.FullName -notmatch '[\\/](\.claude[\\/]worktrees|\.worktrees)[\\/]' } |
      ForEach-Object { $surface.Add($_) }
  }
}

foreach ($file in $surface) {
  $text = Read-Text $file.FullName
  foreach ($match in [regex]::Matches($text, '\[[^\]]+\]\(([^)\s]+)(?:\s+"[^"]*")?\)')) {
    $link = ($match.Groups[1].Value -split '#', 2)[0]
    if ([string]::IsNullOrWhiteSpace($link) -or $link -match '^[a-zA-Z][a-zA-Z0-9+.-]*:') { continue }
    $target = [System.IO.Path]::GetFullPath((Join-Path (Split-Path -Parent $file.FullName) $link))
    if (-not $target.StartsWith($rootPath, [System.StringComparison]::OrdinalIgnoreCase)) {
      $warnings.Add("Link points outside the repository: $(Get-Relative $file.FullName) -> $link")
      continue
    }
    if (-not (Test-Path -LiteralPath $target)) {
      $errors.Add("Broken link: $(Get-Relative $file.FullName) -> $link")
    }
  }
}

# 5. Frontmatter of KB pages.
$frontmatterPages = [System.Collections.Generic.List[string]]::new()
foreach ($path in @('.agents/index.md', '.agents/memory/CURRENT.md', '.agents/rules/fork-upstream-sync.md')) {
  $frontmatterPages.Add((Join-Path $rootPath $path))
}
$referenceDir = Join-Path $rootPath '.agents/references'
if (Test-Path -LiteralPath $referenceDir) {
  Get-ChildItem -File -LiteralPath $referenceDir -Filter *.md | ForEach-Object { $frontmatterPages.Add($_.FullName) }
}
foreach ($page in $frontmatterPages) {
  if (-not (Test-Path -LiteralPath $page -PathType Leaf)) { continue }
  $rel = Get-Relative $page
  $match = [regex]::Match((Read-Text $page), '\A---\n(.*?)\n---\n', [System.Text.RegularExpressions.RegexOptions]::Singleline)
  if (-not $match.Success) { $errors.Add("Missing frontmatter: $rel"); continue }
  foreach ($field in @('title', 'type', 'sources', 'related', 'updated', 'confidence')) {
    if ($match.Groups[1].Value -notmatch "(?m)^$field\s*:") { $errors.Add("Frontmatter missing '$field': $rel") }
  }
  if ($match.Groups[1].Value -notmatch '(?m)^confidence:\s*(high|medium|low)\s*$') {
    $errors.Add("Frontmatter 'confidence' must be high, medium or low: $rel")
  }
}

# 6. Every role and skill is routed and indexed.
$activationPath = Join-Path $rootPath '.agents/rules/role-activation.md'
$indexPath = Join-Path $rootPath '.agents/index.md'
if ((Test-Path -LiteralPath $activationPath) -and (Test-Path -LiteralPath $indexPath)) {
  $activation = Read-Text $activationPath
  $index = Read-Text $indexPath
  Get-ChildItem -File -LiteralPath (Join-Path $rootPath '.agents/roles') -Filter *.md -ErrorAction SilentlyContinue | ForEach-Object {
    if (-not $activation.Contains("../roles/$($_.Name)")) { $errors.Add("Role not routed in role-activation.md: $($_.Name)") }
    if (-not $index.Contains("roles/$($_.Name)")) { $errors.Add("Role not listed in .agents/index.md: $($_.Name)") }
  }
  Get-ChildItem -Directory -LiteralPath (Join-Path $rootPath '.agents/skills') -ErrorAction SilentlyContinue | ForEach-Object {
    if (-not (Test-Path -LiteralPath (Join-Path $_.FullName 'SKILL.md'))) { return }
    if (-not $activation.Contains("``$($_.Name)``")) { $errors.Add("Skill not routed in role-activation.md: $($_.Name)") }
    if (-not $index.Contains("skills/$($_.Name)/SKILL.md")) { $errors.Add("Skill not listed in .agents/index.md: $($_.Name)") }
  }
}

# 7. Placeholder tokens left from a template.
foreach ($file in $surface) {
  $text = Read-Text $file.FullName
  $count = [regex]::Matches($text, '<[A-Z0-9_]{2,}>|<project>|\{\{[a-zA-Z_ ]+\}\}').Count
  if ($count -gt 0) { $warnings.Add("Placeholder tokens ($count): $(Get-Relative $file.FullName)") }
}

# 8. Log entries are well formed and newest first.
$logPath = Join-Path $rootPath '.agents/log.md'
if (Test-Path -LiteralPath $logPath -PathType Leaf) {
  $headers = [regex]::Matches((Read-Text $logPath), '(?m)^## (.*)$')
  if ($headers.Count -eq 0) { $errors.Add('.agents/log.md has no entries') }
  $previous = $null
  foreach ($header in $headers) {
    $entry = [regex]::Match($header.Groups[1].Value, '^\[(\d{4}-\d{2}-\d{2})\] [a-z-]+ \| \S')
    if (-not $entry.Success) { $errors.Add(".agents/log.md entry header is malformed: ## $($header.Groups[1].Value)"); continue }
    $date = [datetime]::ParseExact($entry.Groups[1].Value, 'yyyy-MM-dd', [System.Globalization.CultureInfo]::InvariantCulture)
    if ($null -ne $previous -and $date -gt $previous) {
      $errors.Add(".agents/log.md is not newest-first at ## $($header.Groups[1].Value)")
      break
    }
    $previous = $date
  }
}

# 9. Claude Code skill stubs match .agents/skills.
$syncScript = Join-Path $rootPath 'tools/sync_claude_skill_stubs.ps1'
if (Test-Path -LiteralPath $syncScript -PathType Leaf) {
  $output = & pwsh -NoProfile -File $syncScript -Check -Root $rootPath 2>&1
  if ($LASTEXITCODE -ne 0) {
    $output | ForEach-Object { $errors.Add("Skill stubs: $_") }
  }
}

if ($warnings.Count -gt 0) {
  Write-Output 'Warnings:'
  $warnings | ForEach-Object { Write-Output "  - $_" }
}
if ($errors.Count -gt 0) {
  Write-Output 'Errors:'
  $errors | ForEach-Object { Write-Output "  - $_" }
  exit 1
}

Write-Output "Agent KB lint passed. Markdown files checked: $($surface.Count). Warnings: $($warnings.Count)."
exit 0

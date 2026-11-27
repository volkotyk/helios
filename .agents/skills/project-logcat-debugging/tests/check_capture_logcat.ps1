#Requires -Version 5.1

$ErrorActionPreference = 'Stop'

$scriptPath = Join-Path $PSScriptRoot '..\scripts\capture-logcat.ps1'
$content = Get-Content -Raw $scriptPath

function Assert-Contains {
    param(
        [string]$Value,
        [string]$Pattern,
        [string]$Message
    )

    if ($Value -notmatch $Pattern) {
        throw $Message
    }
}

Assert-Contains `
    -Value $content `
    -Pattern "\[string\]\`$LaunchActivity = '\.MainActivity'" `
    -Message 'capture-logcat.ps1 should default -Launch to .MainActivity, the Helios HOME and LAUNCHER activity.'

Assert-Contains `
    -Value $content `
    -Pattern "'am',\s*'start'" `
    -Message 'capture-logcat.ps1 should use am start for explicit activity launches.'

Assert-Contains `
    -Value $content `
    -Pattern "'-n',\s*\`$componentName" `
    -Message 'capture-logcat.ps1 should pass an explicit component name to am start.'

Assert-Contains `
    -Value $content `
    -Pattern "\[string\]\`$PackageName = 'pl\.mateusz\.helios'" `
    -Message 'capture-logcat.ps1 should default -PackageName to pl.mateusz.helios.'

Assert-Contains `
    -Value $content `
    -Pattern "'settings\.gradle'" `
    -Message 'capture-logcat.ps1 should find the Helios root by its Groovy settings.gradle.'

Write-Host 'capture-logcat helper checks passed'

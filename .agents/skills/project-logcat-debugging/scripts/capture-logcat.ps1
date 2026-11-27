#Requires -Version 5.1
param(
    [string]$PackageName = 'pl.mateusz.helios',
    [string]$DeviceSerial,
    [switch]$ListDevices,
    [switch]$Clear,
    [switch]$Launch,
    [string]$LaunchActivity = '.MainActivity',
    [switch]$Follow,
    [ValidateSet('V', 'D', 'I', 'W', 'E', 'F', 'S')]
    [string]$MinLevel = 'W',
    [int]$Seconds = 10,
    [int]$TailLines = 1000
)

$ErrorActionPreference = 'Stop'

function Find-ProjectRoot {
    param([string]$StartPath)

    $directory = (Resolve-Path $StartPath).Path
    while ($directory) {
        foreach ($marker in @('settings.gradle', 'settings.gradle.kts')) {
            if (Test-Path (Join-Path $directory $marker)) {
                return $directory
            }
        }

        $parent = Split-Path -Parent $directory
        if (-not $parent -or $parent -eq $directory) {
            break
        }
        $directory = $parent
    }

    throw 'Could not locate project root containing settings.gradle or settings.gradle.kts.'
}

function Convert-LocalPropertiesPath {
    param([string]$RawPath)

    return $RawPath.Replace('\\', '\').Replace('\:', ':')
}

function Get-AndroidSdkCandidates {
    param([string]$ProjectRoot)

    $candidates = New-Object System.Collections.Generic.List[string]
    $localProperties = Join-Path $ProjectRoot 'local.properties'

    if (Test-Path $localProperties) {
        $sdkLine = Get-Content $localProperties |
            Where-Object { $_ -match '^sdk\.dir=' } |
            Select-Object -First 1

        if ($sdkLine) {
            $rawSdk = $sdkLine.Substring($sdkLine.IndexOf('=') + 1)
            $candidates.Add((Convert-LocalPropertiesPath $rawSdk))
        }
    }

    foreach ($envPath in @($env:ANDROID_HOME, $env:ANDROID_SDK_ROOT)) {
        if ($envPath) {
            $candidates.Add($envPath)
        }
    }

    if ($env:LOCALAPPDATA) {
        $candidates.Add((Join-Path $env:LOCALAPPDATA 'Android\Sdk'))
    }

    return $candidates | Where-Object { $_ } | Select-Object -Unique
}

function Resolve-Adb {
    param([string]$ProjectRoot)

    foreach ($sdk in (Get-AndroidSdkCandidates $ProjectRoot)) {
        $adb = Join-Path $sdk 'platform-tools\adb.exe'
        if (Test-Path $adb) {
            return $adb
        }
    }

    $pathAdb = Get-Command adb -ErrorAction SilentlyContinue
    if ($pathAdb) {
        return $pathAdb.Source
    }

    throw 'Could not find adb. Check local.properties sdk.dir or install Android SDK platform-tools.'
}

function Get-AttachedDevices {
    param([string]$AdbPath)

    $lines = & $AdbPath devices -l
    return @(
        $lines |
            Where-Object { $_ -match '\sdevice\s' } |
            ForEach-Object {
                $parts = $_ -split '\s+'
                $parts[0]
            }
    )
}

function Invoke-Adb {
    param(
        [string]$AdbPath,
        [string]$Serial,
        [string[]]$Arguments
    )

    if ($Serial) {
        & $AdbPath -s $Serial @Arguments
    } else {
        & $AdbPath @Arguments
    }
}

function Get-ActivityComponent {
    param(
        [string]$PackageName,
        [string]$LaunchActivity
    )

    if ([string]::IsNullOrWhiteSpace($LaunchActivity)) {
        return $null
    }

    if ($LaunchActivity.Contains('/')) {
        return $LaunchActivity
    }

    return "$PackageName/$LaunchActivity"
}

$projectRoot = Find-ProjectRoot $PSScriptRoot
$adbPath = Resolve-Adb $projectRoot

if ($ListDevices) {
    & $adbPath devices -l
    return
}

$devices = @(Get-AttachedDevices $adbPath)
if (-not $DeviceSerial) {
    if ($devices.Count -eq 1) {
        $DeviceSerial = $devices[0]
    } elseif ($devices.Count -eq 0) {
        throw 'No attached Android device or emulator found.'
    } else {
        Write-Host 'Multiple devices found. Re-run with -DeviceSerial <serial>.' -ForegroundColor Yellow
        & $adbPath devices -l
        exit 2
    }
}

Write-Host "adb: $adbPath"
Write-Host "device: $DeviceSerial"
Write-Host "package: $PackageName"

$installed = Invoke-Adb $adbPath $DeviceSerial @('shell', 'pm', 'list', 'packages', $PackageName)
if (-not ($installed -match [regex]::Escape($PackageName))) {
    Write-Warning "Package $PackageName is not installed on $DeviceSerial."
}

if ($Clear) {
    Invoke-Adb $adbPath $DeviceSerial @('logcat', '-c') | Out-Null
    Write-Host 'logcat cleared'
}

if ($Launch) {
    $componentName = Get-ActivityComponent $PackageName $LaunchActivity

    if ($componentName) {
        Invoke-Adb $adbPath $DeviceSerial @(
            'shell',
            'am',
            'start',
            '-W',
            '-n',
            $componentName,
            '-a',
            'android.intent.action.MAIN',
            '-c',
            'android.intent.category.LAUNCHER'
        ) | Out-Host
    } else {
        Invoke-Adb $adbPath $DeviceSerial @(
            'shell',
            'monkey',
            '-p',
            $PackageName,
            '-c',
            'android.intent.category.LAUNCHER',
            '1'
        ) | Out-Host
    }
}

if ($Seconds -gt 0 -and -not $Follow) {
    Start-Sleep -Seconds $Seconds
}

$pidOutput = Invoke-Adb $adbPath $DeviceSerial @('shell', 'pidof', '-s', $PackageName) | Select-Object -First 1
$appPid = if ($pidOutput) { $pidOutput.Trim() } else { '' }
if ($appPid) {
    Write-Host "pid: $appPid"
    if ($Follow) {
        Invoke-Adb $adbPath $DeviceSerial @('logcat', '-v', 'time', '--pid', $appPid, "*:$MinLevel")
    } else {
        Invoke-Adb $adbPath $DeviceSerial @('logcat', '-d', '-v', 'time', '--pid', $appPid, "-t", "$TailLines", "*:$MinLevel")
    }
} else {
    Write-Warning "Package $PackageName is not currently running; falling back to package-name filtered log tail."
    $logs = Invoke-Adb $adbPath $DeviceSerial @('logcat', '-d', '-v', 'time', "-t", "$TailLines", "*:$MinLevel")
    $logs | Select-String -Pattern ([regex]::Escape($PackageName))
}

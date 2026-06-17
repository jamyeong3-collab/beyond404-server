param(
    [Parameter(ValueFromRemainingArguments = $true)]
    [string[]]$GradleArgs = @("bootRun")
)

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$candidateHomes = New-Object System.Collections.Generic.List[string]

function Import-DotEnvFile {
    param([string]$Path)

    if (-not (Test-Path $Path)) {
        return
    }

    $loadedCount = 0
    Get-Content -Path $Path | ForEach-Object {
        $line = $_.Trim()
        if ([string]::IsNullOrWhiteSpace($line) -or $line.StartsWith("#")) {
            return
        }

        $separatorIndex = $line.IndexOf("=")
        if ($separatorIndex -le 0) {
            return
        }

        $name = $line.Substring(0, $separatorIndex).Trim()
        $value = $line.Substring($separatorIndex + 1).Trim()

        if ($value.Length -ge 2) {
            $first = $value.Substring(0, 1)
            $last = $value.Substring($value.Length - 1, 1)
            if (($first -eq '"' -and $last -eq '"') -or ($first -eq "'" -and $last -eq "'")) {
                $value = $value.Substring(1, $value.Length - 2)
            }
        }

        if (-not [string]::IsNullOrWhiteSpace($name)) {
            [Environment]::SetEnvironmentVariable($name, $value, "Process")
            $loadedCount += 1
        }
    }

    if ($loadedCount -gt 0) {
        Write-Host "Loaded $loadedCount environment variable(s) from .env.local"
    }
}

function Add-JavaCandidate {
    param([string]$Path)

    if ([string]::IsNullOrWhiteSpace($Path)) {
        return
    }

    $trimmedPath = $Path.Trim().TrimEnd("\")
    if ([string]::IsNullOrWhiteSpace($trimmedPath)) {
        return
    }

    if (Test-Path (Join-Path $trimmedPath "bin\java.exe")) {
        $candidateHomes.Add($trimmedPath)
    }
}

Add-JavaCandidate -Path $env:JAVA_HOME

if ($env:JAVA_HOME -match "\\bin\\server$") {
    $jdkRoot = Split-Path (Split-Path $env:JAVA_HOME -Parent) -Parent
    Add-JavaCandidate -Path $jdkRoot
}

Add-JavaCandidate -Path "C:\Program Files\Java\latest"
Add-JavaCandidate -Path "C:\Program Files\Java\jdk-21.0.10"
Add-JavaCandidate -Path "C:\Program Files\Java\jdk-25.0.2"

$javaHome = $candidateHomes | Select-Object -Unique | Select-Object -First 1

if (-not $javaHome) {
    throw "Could not find a valid JDK. Set JAVA_HOME to a JDK root such as C:\Program Files\Java\jdk-21.0.10."
}

$env:JAVA_HOME = $javaHome
Import-DotEnvFile -Path (Join-Path $scriptDir ".env.local")

Push-Location $scriptDir
try {
    Write-Host "Using JAVA_HOME=$javaHome"
    & "$scriptDir\gradlew.bat" @GradleArgs
    if ($LASTEXITCODE -ne 0) {
        exit $LASTEXITCODE
    }
}
finally {
    Pop-Location
}

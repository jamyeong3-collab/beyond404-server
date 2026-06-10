param(
    [Parameter(ValueFromRemainingArguments = $true)]
    [string[]]$GradleArgs = @("bootRun")
)

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$candidateHomes = New-Object System.Collections.Generic.List[string]

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

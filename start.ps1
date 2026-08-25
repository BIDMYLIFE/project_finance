[CmdletBinding()]
param(
    [Parameter(ValueFromRemainingArguments = $true)]
    [string[]] $MavenArguments
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location -LiteralPath $projectRoot

if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
    throw 'Java was not found. Install Java 21 or newer and add java to PATH.'
}

if (-not (Get-Command mvn -ErrorAction SilentlyContinue)) {
    throw 'Maven was not found. Install Maven and add mvn to PATH.'
}

if ([string]::IsNullOrWhiteSpace($env:JWT_SECRET)) {
    throw 'JWT_SECRET is missing. Set it in the current PowerShell session; do not store secrets in this script.'
}

$arguments = @('spring-boot:run') + $MavenArguments
Write-Host "Starting financial-erp..." -ForegroundColor Cyan
& mvn @arguments

if ($LASTEXITCODE -ne 0) {
    exit $LASTEXITCODE
}

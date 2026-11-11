param(
 [ValidateSet('java','groovy','checks','checks3')][string]$Language='java',
 [Parameter(ValueFromRemainingArguments=$true)][string[]]$ClientArguments
)
$ErrorActionPreference='Stop'
[Console]::OutputEncoding = New-Object System.Text.UTF8Encoding
[Console]::InputEncoding = New-Object System.Text.UTF8Encoding
$OutputEncoding = [Console]::OutputEncoding
& chcp.com 65001 | Out-Null
Set-Location (Split-Path $PSScriptRoot -Parent)
if (-not $env:AUCTION_USER) { $env:AUCTION_USER=Read-Host 'Application username (auction)' }
if (-not $env:AUCTION_PASSWORD) {
 $secure=Read-Host 'Application password' -AsSecureString
 $pointer=[Runtime.InteropServices.Marshal]::SecureStringToBSTR($secure)
 try { $env:AUCTION_PASSWORD=[Runtime.InteropServices.Marshal]::PtrToStringBSTR($pointer) }
 finally { [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($pointer) }
}
$module='client-java'; $main='itmo.auction.client.JavaClient'
if($Language -eq 'groovy') { $module='client-groovy';$main='itmo.auction.client.GroovyClient' }
if($Language -eq 'checks') { $main='itmo.auction.client.IntegrationChecks' }
if($Language -eq 'checks3') { $main='itmo.auction.client.Lab3Checks' }
if (-not $ClientArguments) { $ClientArguments=@('demo') }
& java '-Dfile.encoding=UTF-8' -cp "$module/target/classes;$module/target/dependency/*" $main @ClientArguments
if ($LASTEXITCODE -ne 0) { throw 'Client failed. Read the error above.' }

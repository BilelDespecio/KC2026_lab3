$ErrorActionPreference='Stop'
Set-Location (Split-Path $PSScriptRoot -Parent)
& '.runtime/wildfly-32.0.1.Final/bin/standalone.bat' '-b=127.0.0.1' '-bmanagement=127.0.0.1'
if ($LASTEXITCODE -ne 0) { throw 'WildFly failed.' }

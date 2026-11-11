$ErrorActionPreference='Stop'
Set-Location (Split-Path $PSScriptRoot -Parent)
$war=(Resolve-Path 'auction-server/target/auction-server.war').Path.Replace('\','/')
& '.runtime/wildfly-32.0.1.Final/bin/jboss-cli.bat' --connect "--command=deploy `"$war`" --force"
if ($LASTEXITCODE -ne 0) { throw 'Deployment failed. Check server console.' }

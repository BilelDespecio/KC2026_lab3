$ErrorActionPreference = 'Stop'
Set-Location (Split-Path $PSScriptRoot -Parent)
if (-not (Get-Command java -ErrorAction SilentlyContinue)) { throw 'Install JDK 17 and reopen PowerShell.' }
$versionOutput = (& java --version | Out-String)
if ($versionOutput -notmatch 'openjdk 17\.|java 17\.') { throw 'This project is tested with JDK 17. Set JAVA_HOME and PATH to JDK 17.' }
New-Item -ItemType Directory -Force .runtime | Out-Null
function Fetch-Checked($Url, $Archive, $ChecksumUrl, $Algorithm) {
 Invoke-WebRequest -UseBasicParsing -Uri $Url -OutFile $Archive
 $expected = ((Invoke-WebRequest -UseBasicParsing -Uri $ChecksumUrl).Content -split '\s+')[0].Trim().ToUpper()
 $actual = (Get-FileHash $Archive -Algorithm $Algorithm).Hash
 if ($expected -ne $actual) { Remove-Item $Archive; throw "Checksum mismatch: $Archive" }
}
if (-not (Test-Path '.runtime/apache-maven-3.9.9/bin/mvn.cmd')) {
 $url='https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.9/apache-maven-3.9.9-bin.zip'
 Fetch-Checked $url '.runtime/maven.zip' "$url.sha512" 'SHA512'
 Expand-Archive '.runtime/maven.zip' '.runtime' -Force
}
if (-not (Test-Path '.runtime/wildfly-32.0.1.Final/bin/standalone.bat')) {
 $url='https://repo.maven.apache.org/maven2/org/wildfly/wildfly-dist/32.0.1.Final/wildfly-dist-32.0.1.Final.zip'
 Fetch-Checked $url '.runtime/wildfly.zip' "$url.sha1" 'SHA1'
 Expand-Archive '.runtime/wildfly.zip' '.runtime' -Force
}
& '.runtime/apache-maven-3.9.9/bin/mvn.cmd' -B clean package
if ($LASTEXITCODE -ne 0) { throw 'Maven build failed.' }
Write-Host 'Build complete. Create an APPLICATION user next:'
Write-Host 'Select b) Application User, ApplicationRealm, username auction. Roles can be empty.'
& '.runtime/wildfly-32.0.1.Final/bin/add-user.bat'
if ($LASTEXITCODE -ne 0) { throw 'User setup failed.' }
Write-Host 'Select b) Application User, ApplicationRealm, username auction. Roles can be empty.'
Write-Host 'Then: .\scripts\start-server.ps1 in one terminal, .\scripts\deploy.ps1 in another.'

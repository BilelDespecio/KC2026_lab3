# EJB Auction - Lab 3

Java 17 / WildFly 32.0.1.Final / Groovy 4.0.24.

Start here: [Russian execution guide](Lab3_EJB_Run_Guide_RU.pdf).

French setup: [README_FR.md](README_FR.md).

Windows, PowerShell, JDK 17, Internet required for initial setup.

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\setup.ps1
.\demarrer-serveur.cmd
# Another terminal:
.\deployer.cmd
.\lancer-java.cmd
# Another terminal:
.\lancer-groovy.cmd
```

Create your own ApplicationRealm application user during setup. Never commit local WildFly configuration or passwords. H2 demonstration data is reset on deployment/restart. See the PDF for menu scenario, expected results, tests and limitations.

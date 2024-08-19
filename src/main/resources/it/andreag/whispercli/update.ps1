$scriptPath = split-path -parent $MyInvocation.MyCommand.Definition
Start-Process powershell -Verb runAs $scriptPath\updateAdmin.ps1


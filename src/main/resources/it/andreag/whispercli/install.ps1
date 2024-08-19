$scriptPath = split-path -parent $MyInvocation.MyCommand.Definition
Start-Process powershell -Verb runAs $scriptPath\admin.ps1


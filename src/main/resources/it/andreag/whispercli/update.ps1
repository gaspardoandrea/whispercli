$MyDir = [System.IO.Path]::GetDirectoryName($myInvocation.MyCommand.Definition)

if (!([Security.Principal.WindowsPrincipal][Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole] "Administrator")) { Start-Process powershell.exe "-NoProfile -ExecutionPolicy Bypass -File `"$MyDir\updateAdmin.ps1`"" -Verb RunAs }
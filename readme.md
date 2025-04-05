# WhisperCli

WhisperCli is a Windows interface written in `kotlin` that
facilitate a friendly use of whisper speech-to-text **OpenAI** utility.

# Install whisperCli

Download the install `.exe` from [my website](https://www.andreagaspardo.it/whispercli).

The install process will create a **WhisperCli** shortcut in your menu.

The first time you will run app, the browser will be opened in order to install **Powershell**:

- `Powershell`: Download and install **PowerShell-7.5.0-win-x64.msi** or newer
- Run the check (menu **Help** &rArr; **Check whisper setup**) again, you'll be redirect to the Python download page
- `Python`: Download and install **Windows installer (64-bit)** version `3.12.*` (Not the `3.13`!)
- Run the check (menu **Help** &rArr; **Check whisper setup**) again to install all the required tools. 

# Uninstall

## Uninstall whisperx, wget, git and ffmpeg
    py -m pip uninstall whisperx
    choco uninstall wget git ffmpeg

## remove choco

    Remove C:\ProgramData\chocolatey*

## Remove pip
    py -m pip uninstall pip

## Remove Python and Powershell

If you want to remove Python and Powershell, uninstall it from your Window setup. 
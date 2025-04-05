# Install

## Install Python3
<blockquote>
Installare la versione 3.12 di python da qui https://www.python.org/downloads/windows/
</blockquote>

## Install choco
<blockquote>
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))
</blockquote>

## Install wget, ffmpeg and git
<blockquote>
choco install wget git ffmpeg -y
</blockquote>

## Install pip
<blockquote>
cd c:\Windows\Temp
wget -O get-pip.py https://bootstrap.pypa.io/get-pip.py
py get-pip.py
# py -m pip install --upgrade pip
</blockquote>

## Install whisperx
<blockquote>
py -m pip install whisperx
</blockquote>


# Uninstall
<blockquote>
py -m pip uninstall whisperx
choco uninstall wget git ffmpeg
</blockquote>

## remove choco

<blockquote>
Remove C:\ProgramData\chocolatey*
</blockquote>

## Remove pip
<blockquote>
py -m pip uninstall pip
c:\python312
</blockquote>

# Upgrade
<blockquote>
py -m pip install --upgrade pip
py -m pip install --upgrade --no-deps --force-reinstall whisperx
</blockquote>

# Run
<blockquote>
cd "C:\Users\gaspa\OneDrive\src\Acel\02.2023-2024\Metodologie della ricerca antropologica\Etnografia\Materiale\Foto\UscitaFunghi 24Aprile"
py -m whisper --fp16 False --language it .\Considerazioni.aac --model tiny --output_dir tiny
</blockquote>
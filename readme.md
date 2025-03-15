# Install

## Install Python3
<blockquote>
Installare python da qui https://www.python.org/downloads/windows/
</blockquote>

## Install choco
<blockquote>
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))
</blockquote>

## Install ffmpeg
<blockquote>
choco install ffmpeg wget git -y
</blockquote>

## Install pip
<blockquote>
cd c:\Windows\Temp
wget -O get-pip.py https://bootstrap.pypa.io/get-pip.py
py get-pip.py
# py -m pip install --upgrade pip
</blockquote>

## Install Openai+whisper
<blockquote>
py -m pip install git+https://github.com/openai/whisper.git
# py -m pip install --upgrade setuptools
# py -m pip install numpy==1.26.0
</blockquote>


# Uninstall
<blockquote>
py -m pip uninstall openai-whisper
choco uninstall ffmpeg wget git
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
py -m pip install --upgrade --no-deps --force-reinstall git+https://github.com/openai/whisper.git
</blockquote>

# Run
<blockquote>
cd "C:\Users\gaspa\OneDrive\src\Acel\02.2023-2024\Metodologie della ricerca antropologica\Etnografia\Materiale\Foto\UscitaFunghi 24Aprile"
py -m whisper --fp16 False --language it .\Considerazioni.aac --model tiny --output_dir tiny
</blockquote>
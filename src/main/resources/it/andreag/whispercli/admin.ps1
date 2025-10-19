Import-Module Microsoft.PowerShell.Security
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))

choco install wget git -y
choco install ffmpeg -y --version 7

cd c:\Windows\Temp
wget -O get-pip.py https://bootstrap.pypa.io/get-pip.py
py get-pip.py
py -m pip install numpy pandas torch "torchaudio>=2.8.0,<2.9.0" torchcodec whisperx transformers nltk ctranslate2 faster_whisper "pyannote.audio<4.0" omegaconf
Start-Sleep 2
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))
Start-Sleep 1
choco install ffmpeg wget -y
Start-Sleep 1
cd c:\Windows\Temp
Start-Sleep 1
wget -O get-pip.py https://bootstrap.pypa.io/get-pip.py
Start-Sleep 1
py get-pip.py
Start-Sleep 1
py -m pip install git+https://github.com/openai/whisper.git
Start-Sleep 4
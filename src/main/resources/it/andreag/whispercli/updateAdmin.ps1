choco upgrade wget git ffmpeg -y
py -m pip install --upgrade pip numpy torch pandas torchaudio transformers nltk ctranslate2 faster_whisper pyannote
py -m pip install --upgrade --no-deps --force-reinstall whisperx

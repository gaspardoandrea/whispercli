choco upgrade wget git -y
choco upgrade ffmpeg -y --version 7
py -m pip install --upgrade numpy pandas torch "torchaudio>=2.8.0,<2.9.0" torchcodec transformers nltk ctranslate2 faster_whisper "pyannote.audio<4.0" omegaconf
py -m pip install --upgrade --no-deps --force-reinstall whisperx

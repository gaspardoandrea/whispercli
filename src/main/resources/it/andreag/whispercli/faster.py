import whisperx
import json
from pathlib import Path
import sys
import time

startTime = time.time()

device = "cpu"
lang = sys.argv[1]
audio_file = sys.argv[2]
model = sys.argv[3]
outputDir = sys.argv[4]

outputFile = Path(outputDir, Path(audio_file).stem + ".json")

# reduce if low on GPU mem
batch_size = 16

# change to "int8" if low on GPU mem (may reduce accuracy)
compute_type = "float32"

# 1. Transcribe with original whisper (batched)
model = whisperx.load_model(model, device, compute_type=compute_type)

print("Loading " + audio_file)
audio = whisperx.load_audio(audio_file)
result = model.transcribe(audio, batch_size=batch_size, language=lang)
print(result["segments"]) # before alignment

endTime = time.time()

result["startTime"] = startTime
result["endTime"] = endTime

with open(outputFile, 'w', encoding='utf-8') as f:
    json.dump(result, f, ensure_ascii=False, indent=4)

# 2. Align whisper output
#model_a, metadata = whisperx.load_align_model(language_code=result["language"], device=device)
#result = whisperx.align(result["segments"], model_a, metadata, audio, device, return_char_alignments=False)
#print(result["segments"]) # after alignment

from huggingface_hub import hf_hub_download
import os

# Configuration
repo_id = "TheBloke/Mistral-7B-Instruct-v0.2-GGUF"
filename = "mistral-7b-instruct-v0.2.Q4_K_M.gguf"
local_dir = os.path.join("models", "mistral", "models")

print(f"Starting download of {filename}...")
print(f"Source: {repo_id}")
print(f"Destination: {os.path.abspath(local_dir)}")

try:
    os.makedirs(local_dir, exist_ok=True)
    
    file_path = hf_hub_download(
        repo_id=repo_id,
        filename=filename,
        local_dir=local_dir,
        local_dir_use_symlinks=False
    )
    print(f"Successfully downloaded to: {file_path}")
except Exception as e:
    print(f"Error downloading model: {e}")

from huggingface_hub import hf_hub_download

# Download CodeLlama 7B in GGUF format (portable)
print("Downloading CodeLlama 7B GGUF model...")
model_path = hf_hub_download(
    repo_id="TheBloke/CodeLlama-7B-GGUF",
    filename="codellama-7b.Q4_K_M.gguf",  # ~4GB quantized version
    local_dir="./models"
)
print(f"Model downloaded to: {model_path}")
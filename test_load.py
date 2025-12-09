from llama_cpp import Llama
m = Llama(model_path='models/codellama-7b.Q4_K_M.gguf', n_threads=4)
print('model OK, running a small inference...')
print(m("Hello"))

import chromadb
import os
import pkgutil

package_path = os.path.dirname(chromadb.__file__)
print(f"ChromaDB path: {package_path}")

for root, dirs, files in os.walk(package_path):
    for file in files:
        if file.endswith(".py"):
            filepath = os.path.join(root, file)
            try:
                with open(filepath, "r", encoding="utf-8") as f:
                    content = f.read()
                    if "OpenTelemetry" in content or "OTEL" in content:
                        print(f"Found in {filepath}")
                        # Print a snippet
                        lines = content.splitlines()
                        for i, line in enumerate(lines):
                            if "OpenTelemetry" in line or "OTEL" in line:
                                print(f"  {i+1}: {line.strip()}")
            except Exception as e:
                pass

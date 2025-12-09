#!/usr/bin/env bash
# Small helper to prevent adding files > MAX_MB to the index. Intended for local/manual use or pre-commit hook wiring.
MAX_MB=${1:-100}

echo "Checking for staged files larger than ${MAX_MB}MB..."
staged=$(git diff --cached --name-only -z | tr '\0' '\n' | sed -n '/./p')
if [ -z "$staged" ]; then
  echo "No staged files."; exit 0
fi
over=0
for f in $staged; do
  if [ -f "$f" ]; then
    size=$(wc -c < "$f" )
    mb=$((size / 1024 / 1024))
    if [ $mb -gt $MAX_MB ]; then
      echo "ERROR: Staged file $f is ${mb}MB which is > ${MAX_MB}MB" >&2
      over=1
    fi
  fi
done
if [ $over -ne 0 ]; then
  echo "Large files detected. Aborting. Use LFS or remove the file from the index." >&2
  exit 1
fi
echo "No large staged files detected. OK."

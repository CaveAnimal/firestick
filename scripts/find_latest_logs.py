#!/usr/bin/env python3
"""find_latest_logs.py

Search for the most recent log file for the backend and LLM service.

By default the script prefers files from the current date (YYYY-MM-DD) and — when
multiple files for that date are present — picks the one with the highest letter
suffix (e.g. backend-2025-11-23c.log wins over backend-2025-11-23.log).

If no file for the current date exists in the expected folders, the script falls
back to the most recently-modified file in that folder.

Usage:
    python scripts/find_latest_logs.py [--root /path/to/project] [--date YYYY-MM-DD]

Outputs JSON with two fields: backend and llm; each contains the path chosen.
"""

from __future__ import annotations

import argparse
import json
import re
from datetime import date
from pathlib import Path
from typing import List, Optional, Tuple


def date_str(d: date) -> str:
    return d.strftime("%Y-%m-%d")


def highest_suffix_choice(files: List[Path], date_token: str, prefix: str, rx: re.Pattern) -> Optional[Path]:
    # Filter candidate files that match the pattern and the date token
    candidates: List[Tuple[str, Path]] = []
    for p in files:
        m = rx.match(p.name)
        if not m:
            continue
        if m.group(1) != date_token:
            continue
        suffix = m.group(2) or ""  # may be empty
        candidates.append((suffix, p))

    if not candidates:
        return None

    # pick the candidate with the lexicographically highest suffix ('' < 'a' < 'b' ...)
    candidates.sort(key=lambda t: t[0])
    return candidates[-1][1]


def most_recent_in_folder(files: List[Path]) -> Optional[Path]:
    if not files:
        return None
    return max(files, key=lambda p: p.stat().st_mtime)


def find_latest_backend(root: Path, date_token: str) -> Optional[Path]:
    backend_dir = root / "logs" / "backend"
    if not backend_dir.exists():
        return None

    files = [p for p in backend_dir.iterdir() if p.is_file()]

    # Prefer backend-YYYY-MM-DD<letter>.log
    rx = re.compile(rf"^backend-({date_token})([a-z]?)\.log$", re.IGNORECASE)
    pick = highest_suffix_choice(files, date_token, "backend", rx)
    if pick:
        return pick

    # Fallback to most recently modified backend log
    backend_files = [p for p in files if p.name.lower().startswith("backend-") and p.suffix == ".log"]
    return most_recent_in_folder(backend_files)


def find_latest_llm(root: Path, date_token: str) -> Optional[Path]:
    # Several possible places for LLM service logs; prefer `logs/llm/llm-<date>[a-z].log`
    llm_dir = root / "logs" / "llm"
    llmlogs_dir = root / "logs" / "LLMlogs"

    # First check logs/llm for llm-YYYY-MM-DD<letter>.log
    if llm_dir.exists():
        files = [p for p in llm_dir.iterdir() if p.is_file()]
        rx = re.compile(rf"^llm-({date_token})([a-z]?)\.log$", re.IGNORECASE)
        pick = highest_suffix_choice(files, date_token, "llm", rx)
        if pick:
            return pick

    # Next check the LLMlogs folder for llm_service_YYYY-MM-DD.log (or requests)
    if llmlogs_dir.exists():
        files = [p for p in llmlogs_dir.iterdir() if p.is_file()]
        # try prefer llm_service_{date}
        svc_pattern = re.compile(rf"^llm_service_({date_token})")
        svc = [p for p in files if svc_pattern.match(p.name)]
        if svc:
            return most_recent_in_folder(svc)

        # fallback to llm_requests_{date}
        req_pattern = re.compile(rf"^llm_requests_({date_token})")
        reqs = [p for p in files if req_pattern.match(p.name)]
        if reqs:
            return most_recent_in_folder(reqs)

    # global fallback: most recently modified llm-related file anywhere under logs
    cand = []
    for p in (root / "logs").rglob("*"):
        if p.is_file() and ("llm" in p.name.lower() or "llm_service" in p.name.lower()):
            cand.append(p)
    return most_recent_in_folder(cand)


def main() -> None:
    parser = argparse.ArgumentParser(description="Find latest backend and LLM service log files")
    parser.add_argument("--root", "-r", default=Path(__file__).resolve().parents[1], type=Path,
                        help="project root to search (default: repository root)")
    parser.add_argument("--date", "-d", default=date_str(date.today()),
                        help="date token to prefer (format YYYY-MM-DD). Defaults to today")

    args = parser.parse_args()
    root = args.root
    token = args.date

    backend = find_latest_backend(root, token)
    llm = find_latest_llm(root, token)

    result = {
        "backend": str(backend) if backend else None,
        "llm": str(llm) if llm else None,
        "date_token": token,
    }

    print(json.dumps(result))


if __name__ == "__main__":
    main()

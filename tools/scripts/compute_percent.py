#!/usr/bin/env python3
"""Compute percent complete from markdown checkboxes and update summary lines.

Usage: python tools/scripts/compute_percent.py [--apply] [paths...]

- If --apply is passed the files are modified in-place (a .bak is created).
- If no paths are passed the script scans tools/work/*/tasksDEV*.md

The script ignores fenced code blocks (```...```) when counting checkboxes.
It recognizes checkbox markers like: - [ ], - [X], - [V], - [-], - [!], - [>]
Completed count is computed as checks with X or V (case-insensitive).
"""
from __future__ import annotations

import argparse
import datetime
import re
from pathlib import Path
from typing import Tuple


CHECKBOX_RE = re.compile(r"^\s*[-*+]\s*\[(.)\]", flags=re.IGNORECASE)


def strip_fenced_code(text: str) -> str:
    # Remove fenced code block content so checkbox-looking lines inside don't count
    parts = re.split(r"(```[\s\S]*?```)", text, flags=re.MULTILINE)
    out_parts = []
    for i, p in enumerate(parts):
        if p.startswith('```'):
            # drop code fence entirely
            continue
        out_parts.append(p)
    return ''.join(out_parts)


def count_checkboxes(text: str) -> Tuple[int, int]:
    total = 0
    completed = 0
    for line in text.splitlines():
        m = CHECKBOX_RE.match(line)
        if m:
            total += 1
            ch = m.group(1).strip().upper()
            if ch in ('X', 'V'):
                completed += 1
    return total, completed


def update_summary_in_file(path: Path, total: int, completed: int) -> Tuple[bool, str]:
    text = path.read_text(encoding='utf-8')

    # Replace the top-level Task Summary block if found
    now = datetime.date.today().isoformat()

    def repl_total(m):
        return f"**Total Tasks:** {total} tasks"

    def repl_completed(m):
        return f"**Completed/Tested:** {completed} tasks"

    def repl_percent(m):
        pct = round((completed / total) * 100) if total > 0 else 0
        return f"**Percent Complete:** {pct}%"

    new = text
    new, tcount = re.subn(r"\*\*Total Tasks:\*\*.*", repl_total, new)
    new, ccount = re.subn(r"\*\*Completed/Tested:\*\*.*", repl_completed, new)
    new, pcount = re.subn(r"\*\*Percent Complete:\*\*.*", repl_percent, new)

    # Update Last Updated date if it exists
    new, lcount = re.subn(r"\*\*Last Updated:\*\*.*", f"**Last Updated:** {now}", new)

    changed = new != text
    return changed, new


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('--apply', action='store_true', help='Write changes to files (create .bak).')
    parser.add_argument('paths', nargs='*', help='Files or glob patterns to scan')
    args = parser.parse_args()

    base = Path(__file__).resolve().parents[2]
    if not args.paths:
        files = sorted(Path(base).glob('tools/work/*/tasksDEV*.md'))
    else:
        files = []
        for p in args.paths:
            files.extend(sorted(Path(base).glob(p)))

    if not files:
        print('No dev task files found.')
        return 1

    print('Files:', ', '.join(str(p) for p in files))
    modified = []
    for p in files:
        text = p.read_text(encoding='utf-8')
        stripped = strip_fenced_code(text)
        total, completed = count_checkboxes(stripped)
        print(f'  {p.name}: total={total} completed={completed}')

        if args.apply:
            changed, newtext = update_summary_in_file(p, total, completed)
            if changed:
                bak = p.with_suffix(p.suffix + '.bak')
                bak.write_text(text, encoding='utf-8')
                p.write_text(newtext, encoding='utf-8')
                modified.append(str(p))

    if args.apply:
        print('\nModified files:')
        for m in modified:
            print(' ', m)
    else:
        print('\nRun with --apply to update files in-place (backups created with .bak).')

    return 0


if __name__ == '__main__':
    raise SystemExit(main())

"""Verify that installed packages match requirements.txt pins."""

from __future__ import annotations

import argparse
from importlib import metadata
from importlib.metadata import PackageNotFoundError
from pathlib import Path
from typing import Iterable, List

from packaging.requirements import Requirement


def read_requirements(path: Path) -> List[str]:
    """Return normalized requirement strings from *path*."""
    encodings: Iterable[str] = ("utf-8", "utf-16", "utf-16-le", "utf-16-be")
    text = None
    for enc in encodings:
        try:
            text = path.read_text(encoding=enc)
            break
        except UnicodeError:
            continue
    if text is None:
        text = path.read_text()
    return [
        line.strip()
        for line in text.splitlines()
        if line.strip() and not line.lstrip().startswith("#")
    ]


def audit(requirements: Iterable[str]) -> tuple[list[str], list[str]]:
    missing: list[str] = []
    mismatch: list[str] = []

    for raw in requirements:
        req = Requirement(raw)
        if req.marker and not req.marker.evaluate():
            continue

        try:
            installed_version = metadata.version(req.name)
        except PackageNotFoundError:
            spec = str(req.specifier) if req.specifier else ""
            missing.append(f"{req.name}{spec}")
            continue

        if req.specifier and installed_version not in req.specifier:
            mismatch.append(
                f"{req.name}=={installed_version} (requires {req.specifier})"
            )

    return missing, mismatch


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument(
        "requirements",
        type=Path,
        default=Path("requirements.txt"),
        nargs="?",
        help="Path to the requirements file (default: requirements.txt)",
    )
    args = parser.parse_args()

    req_lines = read_requirements(args.requirements)
    missing, mismatch = audit(req_lines)

    if missing:
        print("Missing packages:")
        for item in missing:
            print(f"  {item}")

    if mismatch:
        print("Version mismatches:")
        for item in mismatch:
            print(f"  {item}")

    if not missing and not mismatch:
        print("All requirements satisfied.")
        return 0

    return 1


if __name__ == "__main__":
    raise SystemExit(main())

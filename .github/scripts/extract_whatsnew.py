#!/usr/bin/env python3
"""Turn tagged release-notes blocks into Play whatsnew files.

Blocks are the format Play Console itself uses per language:

    <en-US>
    line one
    line two
    </en-US>

Usage:

    extract_whatsnew.py --text <file>   --out <dir>   # notes text as-is
    extract_whatsnew.py --pulls <file>  --out <dir>   # JSON from
        gh api repos/<repo>/commits/<sha>/pulls
        Picks the first PR body in API order that actually holds a tagged block.

Writes whatsnew-<locale> into the output directory; the Play upload action reads
each file's whole content as that language's release notes.

Status lines for CI:

    NOTES_OK pr=29 langs=en-US,de-DE,... maxChars=189
    NOTES_MISSING

Exit 2 for a malformed set (partial language set, unclosed tag, duplicate tag,
empty block, block over Play's 500-character limit) and 3 when no tagged block
is present at all.
"""

import json
import re
import sys
from pathlib import Path

LANGS = ("en-US", "de-DE", "es-US", "fr-FR", "pt-BR", "hi-IN")
MAX_CHARS = 500
LANG_ALT = "|".join(LANGS)
BLOCK = re.compile(rf"<({LANG_ALT})>\s*\n(.*?)\n\s*</\1>", re.DOTALL)
# A tag only counts as an opened block when it ends its line, so prose that
# merely names the format inline is not mistaken for a broken block.
OPEN_TAG = re.compile(rf"<({LANG_ALT})>[ \t]*\r?\n")


def fail(msg: str, code: int) -> int:
    print(f"NOTES_ERROR {msg}")
    return code


def read_lenient(path: Path) -> str:
    # gh writes UTF-8 on the runner; a PowerShell dry run writes UTF-16 with a
    # BOM. Accept either so the script can be dry-run against real gh output.
    raw = path.read_bytes()
    for encoding in ("utf-8-sig", "utf-8", "utf-16"):
        try:
            text = raw.decode(encoding)
            break
        except UnicodeDecodeError:
            continue
    else:
        text = raw.decode("utf-8", errors="replace")
    return text.replace("\r\n", "\n").replace("\r", "\n")


def from_pulls(path: Path) -> tuple[str, str]:
    pulls = json.loads(read_lenient(path))
    if isinstance(pulls, dict):
        pulls = [pulls]
    candidates = [(pull.get("number"), pull.get("body") or "") for pull in pulls]
    with_block = [c for c in candidates if OPEN_TAG.search(c[1])]
    chosen = (with_block or candidates or [(None, "")])[0]
    label = f"pr={chosen[0]}" if chosen[0] is not None else "pr=none"
    return label, chosen[1]


def main() -> int:
    args = sys.argv[1:]
    if len(args) != 4 or args[2] != "--out":
        return fail("usage: extract_whatsnew.py (--text <file> | --pulls <file>) --out <dir>", 2)
    kind, src_arg, _, out_dir_arg = args

    if kind == "--pulls":
        label, source = from_pulls(Path(src_arg))
    elif kind == "--text":
        label, source = "source=input", read_lenient(Path(src_arg))
    else:
        return fail("usage: extract_whatsnew.py (--text <file> | --pulls <file>) --out <dir>", 2)

    notes: dict[str, str] = {}
    for lang, body in BLOCK.findall(source):
        if lang in notes:
            return fail(f"duplicate <{lang}> block", 2)
        notes[lang] = body

    opened = set(OPEN_TAG.findall(source))
    if not opened:
        print("NOTES_MISSING")
        return 3
    unclosed = opened - set(notes)
    if unclosed:
        return fail(f"unclosed tag(s): {', '.join(sorted(unclosed))}", 2)
    missing = [lang for lang in LANGS if lang not in notes]
    if missing:
        return fail(f"missing language(s): {', '.join(missing)}", 2)

    out_dir = Path(out_dir_arg)
    out_dir.mkdir(parents=True, exist_ok=True)
    longest = 0
    for lang in LANGS:
        body = "\n".join(line.strip() for line in notes[lang].splitlines()).strip()
        if not body:
            return fail(f"empty <{lang}> block", 2)
        if len(body) > MAX_CHARS:
            return fail(f"<{lang}> is {len(body)} chars, over the {MAX_CHARS} limit", 2)
        for line in body.splitlines():
            if line.startswith(("- ", "* ", "• ")):
                print(f"NOTES_WARN <{lang}> line looks like a bullet: {line}")
        longest = max(longest, len(body))
        (out_dir / f"whatsnew-{lang}").write_text(body, encoding="utf-8", newline="\n")

    print(f"NOTES_OK {label} langs={','.join(LANGS)} maxChars={longest}")
    return 0


if __name__ == "__main__":
    sys.exit(main())

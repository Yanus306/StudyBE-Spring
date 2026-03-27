#!/usr/bin/env python3
import json
from pathlib import Path

info_path = Path("../database-worktree/info.json")
default_package = "kr.jongyeol.springstudytemplate"

package_name = default_package

try:
    raw = info_path.read_text(encoding="utf-8")
    payload = json.loads(raw)
    if isinstance(payload, dict):
        value = payload.get("package", "")
        if isinstance(value, str) and value.strip():
            package_name = value.strip()
except Exception:
    pass

print(package_name)

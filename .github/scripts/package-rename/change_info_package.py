import json
import sys
from pathlib import Path

path = Path("../database-worktree/info.json")
package = sys.argv[1].strip()

data = {}
if path.exists():
  try:
      data = json.loads(path.read_text(encoding="utf-8"))
      if not isinstance(data, dict):
          data = {}
  except json.JSONDecodeError:
      data = {}

data["package"] = package
path.parent.mkdir(parents=True, exist_ok=True)
path.write_text(json.dumps(data, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
print(f"Updated {path}")
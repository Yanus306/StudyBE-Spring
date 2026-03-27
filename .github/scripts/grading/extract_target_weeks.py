#!/usr/bin/env python3
import json
import re
import sys

package_name = sys.argv[1].strip()
package_path = package_name.replace(".", "/")

pattern = re.compile(r"^src/main/java/" + re.escape(package_path) + r"/study/week(\d+)")

weeks = set()
data = json.loads(sys.stdin.read())

for val in data:
    changed = val.strip()
    if not changed:
        continue

    m = pattern.match(changed.replace(".", "/"))
    if m:
        weeks.add(int(m.group(1)))
        continue

print(",".join(str(week) for week in sorted(weeks)))
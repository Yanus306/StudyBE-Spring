#!/usr/bin/env python3
import json
import re
import sys
from datetime import datetime, timedelta, timezone
from pathlib import Path
from xml.sax.saxutils import escape

KST = timezone(timedelta(hours=9))
WEEK_KEY_PATTERN = re.compile(r"Week(\d+)")

nickname = sys.argv[1]
commit_epoch_ms = int(sys.argv[2])
target_weeks_raw = sys.argv[3].strip()
results_file = Path("build/test-results.json")
result_worktree = Path("../database-worktree")

target_weeks = set()
if target_weeks_raw:
    for token in target_weeks_raw.split(","):
        token = token.strip()
        if token.isdigit():
            target_weeks.add(int(token))

study_dir = result_worktree / ".study"
docs_dir = result_worktree / ".docs"
week_docs_dir = docs_dir / "week"


def load_json(path: Path):
    try:
        with path.open("r", encoding="utf-8") as f:
            return json.load(f)
    except FileNotFoundError:
        return {}


def write_text_if_changed(path: Path, content: str):
    try:
        prev = path.read_text(encoding="utf-8")
        if prev == content:
            return False
    except FileNotFoundError:
        pass

    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(content, encoding="utf-8")
    return True


def save_json_if_changed(path: Path, data):
    content = json.dumps(data, ensure_ascii=False, indent=2) + "\n"
    return write_text_if_changed(path, content)


def parse_week_number(key: str):
    m = WEEK_KEY_PATTERN.fullmatch(key)
    return int(m.group(1)) if m else None


def format_kst(epoch_ms: int):
    dt = datetime.fromtimestamp(epoch_ms / 1000, tz=KST)
    return dt.strftime("%Y-%m-%d %H:%M:%S")


def render_table_svg(title: str, headers, rows, show_title: bool = True):
    row_height = 34
    table_pad = 0

    colors = {
        "bg": "#0d1117",
        "header": "#161b22",
        "text": "#c9d1d9",
        "grid": "#30363d",
    }

    base_widths = [64, 180, 130, 180, 220]
    col_widths = [base_widths[i] if i < len(base_widths) else 160 for i in range(len(headers))]
    table_width = sum(col_widths)

    x_positions = [table_pad]
    for w in col_widths:
        x_positions.append(x_positions[-1] + w)

    title_block = 28 if show_title else 0
    y_start = table_pad + title_block
    table_bottom_y = y_start + row_height * (len(rows) + 1)

    width = table_width
    height = table_bottom_y

    lines = [
        f'<svg xmlns="http://www.w3.org/2000/svg" width="{width}" height="{height}" viewBox="0 0 {width} {height}">',
        f'<rect width="100%" height="100%" fill="{colors["bg"]}"/>',
        f'<rect x="{table_pad}" y="{y_start}" width="{table_width}" height="{row_height}" fill="{colors["header"]}"/>',
    ]

    if show_title:
        lines.append(
            f'<text x="{table_pad}" y="{table_pad + 18}" font-size="16" font-family="Arial, sans-serif" fill="{colors["text"]}">{escape(str(title))}</text>'
        )

    for x in x_positions:
        lines.append(
            f'<line x1="{x}" y1="{y_start}" x2="{x}" y2="{table_bottom_y}" stroke="{colors["grid"]}" stroke-width="1"/>'
        )

    for i in range(len(rows) + 2):
        y = y_start + i * row_height
        lines.append(
            f'<line x1="{table_pad}" y1="{y}" x2="{table_pad + table_width}" y2="{y}" stroke="{colors["grid"]}" stroke-width="1"/>'
        )

    for i, header in enumerate(headers):
        lines.append(
            f'<text x="{x_positions[i] + 8}" y="{y_start + 22}" font-size="14" font-family="Arial, sans-serif" fill="{colors["text"]}">{escape(str(header))}</text>'
        )

    for r_i, row in enumerate(rows):
        for c_i, cell in enumerate(row):
            y = y_start + row_height * (r_i + 1) + 22
            lines.append(
                f'<text x="{x_positions[c_i] + 8}" y="{y}" font-size="14" font-family="Arial, sans-serif" fill="{colors["text"]}">{escape(str(cell))}</text>'
            )

    lines.append("</svg>")
    return "\n".join(lines) + "\n"


new_results = load_json(results_file)
touched_weeks = set()

for week_key, data in new_results.items():
    week_num = parse_week_number(week_key)
    if week_num is None:
        continue

    if target_weeks and week_num not in target_weeks:
        continue

    touched_weeks.add(week_num)

    if not data.get("passed"):
        continue

    week_file = study_dir / f"{week_num}.json"
    week_data = load_json(week_file)
    if not isinstance(week_data, dict):
        week_data = {}

    if nickname not in week_data:
        try:
            duration = int(data.get("duration", 0))
        except (TypeError, ValueError):
            duration = 0
        week_data[nickname] = {"passTime": commit_epoch_ms, "duration": max(duration, 0)}

    week_data = dict(sorted(week_data.items(), key=lambda x: x[0].lower()))
    save_json_if_changed(week_file, week_data)

for week_num in sorted(touched_weeks):
    week_file = study_dir / f"{week_num}.json"
    if not week_file.exists():
        save_json_if_changed(week_file, {})

all_week_data = {}

if study_dir.exists():
    for week_file in study_dir.glob("*.json"):
        if not week_file.stem.isdigit():
            continue

        week_num = int(week_file.stem)
        week_data = load_json(week_file)

        sanitized = {}
        if isinstance(week_data, dict):
            for uname, entry in week_data.items():
                if not isinstance(entry, dict):
                    continue
                try:
                    pass_time = int(entry.get("passTime", 0))
                    duration = int(entry.get("duration", 0))
                except (TypeError, ValueError):
                    continue
                if pass_time <= 0:
                    continue
                sanitized[uname] = {
                    "passTime": pass_time,
                    "duration": max(duration, 0),
                }

        all_week_data[week_num] = dict(sorted(sanitized.items(), key=lambda x: x[0].lower()))

for week_num in touched_weeks:
    all_week_data.setdefault(week_num, {})

user_stats = {}
for week_num in sorted(all_week_data.keys()):
    for uname, record in all_week_data[week_num].items():
        stats = user_stats.setdefault(uname, {"passed": 0, "total_duration": 0, "durations": {}})
        stats["passed"] += 1
        stats["total_duration"] += record["duration"]
        stats["durations"][week_num] = record["duration"]

ranked_users = sorted(
    user_stats.items(),
    key=lambda item: (-item[1]["passed"], item[1]["total_duration"], item[0].lower()),
)

weeks_with_pass = [week_num for week_num, records in all_week_data.items() if records]
last_passed_week = max(weeks_with_pass) if weeks_with_pass else None

main_headers = ["순위", "이름", "통과 횟수", "총 걸린 시간(ms)"]
if last_passed_week is not None:
    main_headers.append(f"{last_passed_week}주차 걸린 시간(ms)")

main_rows = []
for rank, (uname, stats) in enumerate(ranked_users, 1):
    row = [str(rank), uname, str(stats["passed"]), str(stats["total_duration"])]
    if last_passed_week is not None:
        row.append(str(stats["durations"].get(last_passed_week, "-")))
    main_rows.append(row)

if not main_rows:
    main_rows.append(["-" for _ in main_headers])

week_docs_dir.mkdir(parents=True, exist_ok=True)
main_svg = render_table_svg("전체 랭킹", main_headers, main_rows, show_title=False)
write_text_if_changed(docs_dir / "main.svg", main_svg)

for week_num in sorted(all_week_data.keys()):
    if target_weeks and week_num not in target_weeks:
        continue

    week_headers = ["순위", "이름", "걸린 시간(ms)", "통과 시간"]
    rows = []

    ranked_week = sorted(
        all_week_data[week_num].items(),
        key=lambda item: (item[1]["duration"], item[1]["passTime"], item[0].lower()),
    )

    for rank, (uname, record) in enumerate(ranked_week, 1):
        rows.append([
            str(rank),
            uname,
            str(record["duration"]),
            format_kst(record["passTime"]),
        ])

    if not rows:
        rows.append(["-", "-", "-", "-"])

    week_svg = render_table_svg(f"{week_num}주차 랭킹", week_headers, rows, show_title=False)
    write_text_if_changed(week_docs_dir / f"{week_num}.svg", week_svg)

print(f"Updated {study_dir}")
print(f"Updated {docs_dir / 'main.svg'}")
print(f"Updated {week_docs_dir}")
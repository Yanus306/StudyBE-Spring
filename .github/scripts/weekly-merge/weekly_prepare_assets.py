#!/usr/bin/env python3
import json
import re
import sys
from pathlib import Path
from xml.sax.saxutils import escape

WEEK_FILE_PATTERN = re.compile(r"Week(\d+)Tests\.java$")
PACKAGE_PATTERN = re.compile(r"^\s*package\s+([A-Za-z_][\w\.]*)\s*;", re.MULTILINE)
WEEK_SECTION_START = "<!-- WEEK_RANKING_START -->"
WEEK_SECTION_END = "<!-- WEEK_RANKING_END -->"
DEFAULT_PACKAGE = "kr.jongyeol.springstudytemplate"

repo = sys.argv[1]
result_worktree = Path("../database-worktree")

study_dir = result_worktree / ".study"
docs_dir = result_worktree / ".docs"
week_docs_dir = docs_dir / "week"


def ensure_info_json_package():
    info_path = result_worktree / "info.json"
    package_name = detect_base_package()

    try:
        existing = json.loads(info_path.read_text(encoding="utf-8"))
        if not isinstance(existing, dict):
            existing = {}
    except FileNotFoundError:
        existing = {}
    except json.JSONDecodeError:
        existing = {}

    if isinstance(existing.get("package"), str) and existing["package"].strip():
        package_name = existing["package"].strip()
    else:
        existing["package"] = package_name

    content = json.dumps(existing, ensure_ascii=False, indent=2) + "\n"
    write_text_if_changed(info_path, content)
    return package_name


def detect_base_package():
    java_root = Path("src/main/java")
    if not java_root.exists():
        return DEFAULT_PACKAGE

    for file_path in sorted(java_root.rglob("*.java")):
        try:
            text = file_path.read_text(encoding="utf-8")
        except OSError:
            continue

        m = PACKAGE_PATTERN.search(text)
        if m:
            return m.group(1)

    return DEFAULT_PACKAGE


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


def detect_weeks():
    weeks = set()
    test_root = Path("src/test/java")
    if not test_root.exists():
        return []

    for file_path in test_root.rglob("*.java"):
        m = WEEK_FILE_PATTERN.search(file_path.name)
        if m:
            weeks.add(int(m.group(1)))

    return sorted(weeks)


def render_table_svg(title, headers, rows, show_title=True):
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


def ensure_result_placeholders(weeks):
    study_dir.mkdir(parents=True, exist_ok=True)
    week_docs_dir.mkdir(parents=True, exist_ok=True)

    main_svg = docs_dir / "main.svg"
    if not main_svg.exists():
        write_text_if_changed(
            main_svg,
            render_table_svg(
                "전체 랭킹",
                ["순위", "이름", "통과 횟수", "총 걸린 시간(ms)"],
                [["-", "-", "-", "-"]],
                show_title=False,
            ),
        )

    for week in weeks:
        week_json = study_dir / f"{week}.json"
        if not week_json.exists():
            write_text_if_changed(week_json, "{}\n")

        week_svg = week_docs_dir / f"{week}.svg"
        if not week_svg.exists():
            write_text_if_changed(
                week_svg,
                render_table_svg(
                    f"{week}주차 랭킹",
                    ["순위", "이름", "걸린 시간(ms)", "통과 시간"],
                    [["-", "-", "-", "-"]],
                    show_title=False,
                ),
            )


def week_readme_block(week_num: int):
    lines = [
        WEEK_SECTION_START,
        f"## Week{week_num} 랭킹",
        "",
        f"![Week{week_num} 랭킹](https://raw.githubusercontent.com/{repo}/database/.docs/week/{week_num}.svg)",
        WEEK_SECTION_END,
    ]
    return "\n".join(lines) + "\n"


def update_week_readmes(weeks, package_name: str):
    def resolve_week_dir(week_num: int) -> Path:
        package_path = package_name.replace(".", "/")
        roots = [Path("src/main/java")]

        candidates = []
        for root in roots:
            candidates.append(root / f"{package_name}.study.week{week_num}")
            candidates.append(root / package_path / "study" / f"week{week_num}")

        for c in candidates:
            if c.exists() and c.is_dir():
                return c

        for root in roots:
            if not root.exists():
                continue

            dotted_matches = sorted(root.glob(f"**/*.study.week{week_num}"))
            for m in dotted_matches:
                if m.is_dir():
                    return m

            slash_matches = sorted(root.glob(f"**/study/week{week_num}"))
            for m in slash_matches:
                if m.is_dir():
                    return m

        fallback = Path("src/main/java") / package_path / "study" / f"week{week_num}"
        fallback.mkdir(parents=True, exist_ok=True)
        return fallback

    for week in weeks:
        week_dir = resolve_week_dir(week)
        week_readme = week_dir / "README.md"
        block = week_readme_block(week)

        if week_readme.exists():
            content = week_readme.read_text(encoding="utf-8")
        else:
            content = f"# Week{week}\n"

        start_idx = content.find(WEEK_SECTION_START)
        end_idx = content.find(WEEK_SECTION_END)

        if start_idx != -1 and end_idx != -1 and end_idx > start_idx:
            end_idx += len(WEEK_SECTION_END)
            tail = content[end_idx:].lstrip()
            new_content = block
            if tail:
                new_content += "\n" + tail
        else:
            body = content.lstrip()
            new_content = block
            if body:
                new_content += "\n" + body

        write_text_if_changed(week_readme, new_content)


weeks = detect_weeks()
package_name = ensure_info_json_package()
ensure_result_placeholders(weeks)
update_week_readmes(weeks, package_name)

print("Updated database placeholders and week readmes.")
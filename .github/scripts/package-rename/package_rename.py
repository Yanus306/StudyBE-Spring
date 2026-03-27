#!/usr/bin/env python3
import re
import shutil
import sys
from pathlib import Path

PACKAGE_RE = re.compile(r"^[A-Za-z_][A-Za-z0-9_]*(?:\.[A-Za-z_][A-Za-z0-9_]*)*$")
DECL_RE = re.compile(r"^(\s*package\s+)([A-Za-z_][\w.]*)(\s*;.*)$", re.MULTILINE)
IMPORT_RE = re.compile(r"^(\s*import\s+)([A-Za-z_][\w.]*)(\s*;.*)$", re.MULTILINE)


def validate_package(value: str) -> str:
    value = value.strip()
    if not PACKAGE_RE.fullmatch(value):
        raise ValueError(f"Invalid package name: {value}")
    return value


def project_display_name(package_name: str) -> str:
    tail = package_name.rsplit(".", 1)[-1].strip()
    if not tail:
        return package_name
    return tail[:1].upper() + tail[1:]


def detect_base_package() -> str | None:
    java_root = Path("src/main/java")
    if not java_root.exists():
        return None

    for file_path in sorted(java_root.rglob("*.java")):
        try:
            text = file_path.read_text(encoding="utf-8")
        except OSError:
            continue

        m = DECL_RE.search(text)
        if m:
            return m.group(2)

    return None


def rewrite_java_content(text: str, old_pkg: str, new_pkg: str) -> tuple[str, bool]:
    changed = False

    def replace(match: re.Match[str]) -> str:
        nonlocal changed
        pkg = match.group(2)
        if pkg == old_pkg or pkg.startswith(old_pkg + "."):
            changed = True
            pkg = new_pkg + pkg[len(old_pkg):]
        return f"{match.group(1)}{pkg}{match.group(3)}"

    text = DECL_RE.sub(replace, text)
    text = IMPORT_RE.sub(replace, text)
    return text, changed


def rewrite_java_tree(root: Path, old_pkg: str, new_pkg: str) -> int:
    if not root.exists():
        return 0

    changed_count = 0
    for file_path in root.rglob("*.java"):
        try:
            text = file_path.read_text(encoding="utf-8")
        except OSError:
            continue

        new_text, changed = rewrite_java_content(text, old_pkg, new_pkg)
        if not changed:
            continue

        print(f"Updating package in {file_path}")
        changed_count += 1
        file_path.write_text(new_text, encoding="utf-8")

    return changed_count


def move_package_dir(java_root: Path, old_pkg: str, new_pkg: str) -> bool:
    changed = False
    old_path = Path(*old_pkg.split("."))
    new_path = Path(*new_pkg.split("."))

    slash_src = java_root / old_path
    slash_dst = java_root / new_path
    if slash_src.exists() and slash_src.is_dir() and slash_src != slash_dst:
        changed = True
        move_items(slash_src, slash_dst)
        prune_empty_dirs(java_root, slash_src.parent)

    dotted_src = java_root / old_pkg
    dotted_dst = java_root / new_pkg
    if dotted_src.exists() and dotted_src.is_dir() and dotted_src != dotted_dst:
        changed = True
        move_items(dotted_src, dotted_dst)

    return changed

def move_items(source: Path, destination: Path):
    print(f"Moving {source} to {destination}")
    destination.parent.mkdir(parents=True, exist_ok=True)
    if destination.exists() and destination.is_dir():
        for item in sorted(source.iterdir(), key=lambda p: p.name):
            target = destination / item.name
            if target.exists():
                raise RuntimeError(f"Cannot move {item} to {target}: target already exists")
            shutil.move(str(item), str(target))
        source.rmdir()
    else:
        shutil.move(str(source), str(destination))

def prune_empty_dirs(root: Path, start: Path):
    current = start
    while current != root and current.exists() and current.is_dir():
        try:
            current.rmdir()
        except OSError:
            break
        current = current.parent


def update_application_properties(new_pkg: str) -> bool:
    path = Path("src/main/resources/application.properties")
    if not path.exists():
        return False

    try:
        text = path.read_text(encoding="utf-8")
    except OSError:
        return False

    lines = text.splitlines()
    key = "spring.application.name="
    app_name = project_display_name(new_pkg)
    changed = False
    found = False

    for i, line in enumerate(lines):
        if line.startswith(key):
            found = True
            new_line = f"{key}{app_name}"
            if line != new_line:
                lines[i] = new_line
                changed = True
            break

    if not found:
        lines.append(f"{key}{app_name}")
        changed = True

    if changed:
        path.write_text("\n".join(lines) + "\n", encoding="utf-8")
        print(f"Updating application name in {path}")

    return changed


def update_settings_gradle(new_pkg: str) -> bool:
    path = Path("settings.gradle")
    if not path.exists():
        return False

    try:
        text = path.read_text(encoding="utf-8")
    except OSError:
        return False

    lines = text.splitlines()
    project_name = project_display_name(new_pkg)
    changed = False
    found = False
    # rootProject.name = '...'
    assign_re = re.compile(r"^(\s*rootProject\.name\s*=\s*)(['\"])(.*?)(['\"])(\s*)$")

    for i, line in enumerate(lines):
        m = assign_re.match(line)
        if not m:
            continue
        if m.group(2) != m.group(4):
            continue
        found = True
        new_line = f"{m.group(1)}{m.group(2)}{project_name}{m.group(4)}{m.group(5)}"
        if line != new_line:
            lines[i] = new_line
            changed = True
        break

    if not found:
        lines.append(f"rootProject.name = '{project_name}'")
        changed = True

    if changed:
        path.write_text("\n".join(lines) + "\n", encoding="utf-8")
        print(f"Updating rootProject.name in {path}")

    return changed


def main():
    new_pkg = validate_package(sys.argv[1])
    old_pkg = detect_base_package()

    if not old_pkg:
        raise RuntimeError("Could not detect old package from src/main/java")

    if old_pkg == new_pkg:
        print(f"No changes needed: package already '{new_pkg}'")
        return

    changed_java = 0
    changed_java += rewrite_java_tree(Path("src/main/java"), old_pkg, new_pkg)
    changed_java += rewrite_java_tree(Path("src/test/java"), old_pkg, new_pkg)

    moved_dirs = False
    moved_dirs |= move_package_dir(Path("src/main/java"), old_pkg, new_pkg)
    moved_dirs |= move_package_dir(Path("src/test/java"), old_pkg, new_pkg)
    updated_props = update_application_properties(new_pkg)
    updated_settings = update_settings_gradle(new_pkg)

    print()
    print("==================================================")
    print(f"Package changed: {old_pkg} -> {new_pkg}")
    print(f"Changed Java files: {changed_java}")
    print(f"Moved package directories: {'yes' if moved_dirs else 'no'}")
    print(f"Updated application.properties: {'yes' if updated_props else 'no'}")
    print(f"Updated settings.gradle: {'yes' if updated_settings else 'no'}")
    print("==================================================")

if __name__ == "__main__":
    main()

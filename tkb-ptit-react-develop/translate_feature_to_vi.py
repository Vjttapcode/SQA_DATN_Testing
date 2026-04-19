import csv
import sys
from pathlib import Path


MAPPING = {
    "ImportFileModal": "Modal import file",
    "Import data": "Import dữ liệu",
    "Batch table input": "Nhập liệu bảng môn (batch)",
    "Grouped combinations": "Gộp ngành (tổ hợp)",
    "GenerateTKB": "Sinh TKB",
    "Hidden rows logic": "Logic ẩn/hiện dòng",
    "Common registration": "Đăng ký chung",
    "State interaction": "Tương tác trạng thái",
    "Failed subjects": "Danh sách môn không sinh được",
    "Results rendering": "Hiển thị bảng kết quả",
    "Results handling": "Xử lý hiển thị kết quả",
    "Assign rooms": "Gán phòng",
    "Save schedules": "Lưu thời khóa biểu",
    "View/Remove saved results": "Xem/Xóa kết quả đã lưu",
    "View saved schedules": "Xem kết quả đã lưu",
    "Remove saved results": "Xóa kết quả đã lưu",
    "Clear saved results": "Xóa tất cả kết quả đã lưu",
    "UI robustness": "Độ ổn định giao diện",
    "Input validation": "Kiểm tra hợp lệ dữ liệu nhập",
    "Security Testing": "Kiểm thử bảo mật",
    "Concurrency": "Kiểm thử đồng thời",
    "State Transition Testing": "Kiểm thử chuyển trạng thái",
    "Persisted state": "Trạng thái lưu (localStorage)",
    "Performance/Robustness": "Hiệu năng/Độ ổn định",
    "UI state": "Trạng thái UI",
}


EXPECTED_COLS = 11


def normalize_row(fields: list[str]) -> list[str]:
    if len(fields) == EXPECTED_COLS:
        return fields
    if len(fields) > EXPECTED_COLS:
        status = fields[-2] if len(fields) >= 2 else ""
        note = fields[-1] if len(fields) >= 1 else ""
        stable_prefix = fields[:7]
        merged_expected = ";".join(fields[7:-2]).strip().strip(";").strip()
        row = stable_prefix + [merged_expected, "", status, note]
        if len(row) < EXPECTED_COLS:
            row += [""] * (EXPECTED_COLS - len(row))
        return row[:EXPECTED_COLS]
    return fields + [""] * (EXPECTED_COLS - len(fields))


def main() -> None:
    in_path = Path(sys.argv[1]) if len(sys.argv) >= 2 else Path("test-cases-module-lap-lich-non-happy-100.csv")
    out_path = Path(sys.argv[2]) if len(sys.argv) >= 3 else in_path

    lines = in_path.read_text(encoding="utf-8").splitlines()
    if not lines:
        raise RuntimeError(f"Empty input: {in_path}")

    header = next(csv.reader([lines[0]], delimiter=";", quotechar='"'))
    out_rows: list[list[str]] = [header]

    replaced = 0
    for line in lines[1:]:
        if not line.strip():
            continue
        fields = next(csv.reader([line], delimiter=";", quotechar='"'))
        fields = normalize_row(fields)
        feature = fields[2]
        if feature in MAPPING:
            fields[2] = MAPPING[feature]
            replaced += 1
        out_rows.append(fields)

    with out_path.open("w", encoding="utf-8", newline="") as f:
        w = csv.writer(
            f,
            delimiter=";",
            quotechar='"',
            quoting=csv.QUOTE_MINIMAL,
            lineterminator="\n",
        )
        w.writerows(out_rows)

    print(f"Updated: {out_path} (rows={len(out_rows)-1}, replaced={replaced})")


if __name__ == "__main__":
    main()


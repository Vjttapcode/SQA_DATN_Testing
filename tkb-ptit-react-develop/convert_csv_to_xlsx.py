import csv
from pathlib import Path
import sys

import pandas as pd


INPUT_CSV = Path("test-cases-module-lap-lich-100.csv")
OUTPUT_XLSX = Path("test-cases-module-lap-lich-100.xlsx")

# Expected columns in the sheet (from CSV header)
EXPECTED_COLS = [
    "Tiền điều kiện",
    "Mã TC",
    "Chức năng",
    "Mục đích kiểm thử",
    "Kỹ thuật kiểm thử",
    "Dữ liệu đầu vào",
    "Các bước thực hiện",
    "Kết quả mong đợi",
    "Kết quả thực tế",
    "Trạng thái",
    "Ghi chú",
]


def parse_header(cols_from_file: list[str]) -> list[str]:
    if len(cols_from_file) == len(EXPECTED_COLS):
        return cols_from_file
    return EXPECTED_COLS


def normalize_row(fields: list[str], cols: list[str]) -> list[str]:
    """
    Some cells contain an unquoted ';' which breaks delimiter-based parsing.
    Heuristic:
      - keep first 7 columns stable
      - merge everything between col[7] and last-2 into 'Kết quả mong đợi'
      - set 'Kết quả thực tế' empty (usually blank in this dataset)
    """
    n = len(cols)
    if len(fields) == n:
        return fields

    if len(fields) > n:
        status = fields[-2] if len(fields) >= 2 else ""
        note = fields[-1] if len(fields) >= 1 else ""
        stable_prefix = fields[:7]
        merged_expected = ";".join(fields[7:-2]).strip().strip(";").strip()
        row = stable_prefix + [merged_expected, "", status, note]
        # ensure exact length
        if len(row) < n:
            row += [""] * (n - len(row))
        elif len(row) > n:
            row = row[:n]
        return row

    # fewer columns -> pad
    return fields + [""] * (n - len(fields))


def main() -> None:
    global INPUT_CSV, OUTPUT_XLSX

    # Optional CLI args: python convert_csv_to_xlsx.py in.csv out.xlsx
    if len(sys.argv) >= 2:
        INPUT_CSV = Path(sys.argv[1])
    if len(sys.argv) >= 3:
        OUTPUT_XLSX = Path(sys.argv[2])

    raw_lines = INPUT_CSV.read_text(encoding="utf-8").splitlines()
    if not raw_lines:
        raise RuntimeError(f"Empty input: {INPUT_CSV}")

    header_fields = next(csv.reader([raw_lines[0]], delimiter=";", quotechar='"'))
    cols = parse_header(header_fields)

    out_rows: list[list[str]] = []
    for line_no, line in enumerate(raw_lines[1:], start=2):
        if not line.strip():
            continue
        fields = next(csv.reader([line], delimiter=";", quotechar='"'))
        out_rows.append(normalize_row(fields, cols))

    df = pd.DataFrame(out_rows, columns=cols)
    df.to_excel(OUTPUT_XLSX, index=False)
    print(f"Wrote: {OUTPUT_XLSX} (rows={len(df)})")


if __name__ == "__main__":
    main()


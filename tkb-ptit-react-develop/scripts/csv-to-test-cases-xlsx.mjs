import fs from 'fs'
import path from 'path'
import * as XLSX from 'xlsx'

const root = process.cwd()
const csvPath = path.join(root, 'TEST_CASES.csv')
const xlsxPath = path.join(root, 'TEST_CASES.xlsx')

/** Parse one line of pipe-delimited CSV; supports "quoted|fields" */
function parsePipeRow(line) {
  const cells = []
  let cur = ''
  let inQuote = false
  for (let i = 0; i < line.length; i++) {
    const c = line[i]
    if (c === '"') {
      if (inQuote && line[i + 1] === '"') {
        cur += '"'
        i++
        continue
      }
      inQuote = !inQuote
      continue
    }
    if (c === '|' && !inQuote) {
      cells.push(cur)
      cur = ''
      continue
    }
    cur += c
  }
  cells.push(cur)
  return cells
}

let raw = fs.readFileSync(csvPath, 'utf8')
if (raw.charCodeAt(0) === 0xfeff) raw = raw.slice(1)

const lines = raw.split(/\r?\n/).filter((l) => l.length > 0)
const rows = lines.map(parsePipeRow)

const ws = XLSX.utils.aoa_to_sheet(rows)
const wb = XLSX.utils.book_new()
XLSX.utils.book_append_sheet(wb, ws, 'Test cases')

// Cột rộng vừa đọc (ký tự ~ước lượng)
ws['!cols'] = [
  { wch: 10 },
  { wch: 36 },
  { wch: 44 },
  { wch: 48 },
  { wch: 90 },
  { wch: 90 },
  { wch: 12 },
  { wch: 52 },
]

XLSX.writeFile(wb, xlsxPath)
console.error('Wrote', xlsxPath, '(' + (rows.length - 1), 'data rows + header)')

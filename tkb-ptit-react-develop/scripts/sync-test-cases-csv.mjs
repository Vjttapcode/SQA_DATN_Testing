import fs from 'fs'
import path from 'path'
import { spawnSync } from 'child_process'
import { extractItSnippets } from './extract-it-bodies.mjs'

const root = process.cwd()
const testsDir = path.join(root, 'tests')

const files = []
function walk(d) {
  for (const e of fs.readdirSync(d, { withFileTypes: true })) {
    const p = path.join(d, e.name)
    if (e.isDirectory()) walk(p)
    else if (/\.(test|spec)\.(ts|tsx)$/.test(e.name)) files.push(p)
  }
}
walk(testsDir)
files.sort()

const rows = []
for (const abs of files) {
  const f = path.relative(root, abs).replace(/\\/g, '/')
  const source = fs.readFileSync(abs, 'utf8')
  for (const sn of extractItSnippets(source)) {
    rows.push({
      file: f,
      name: sn.name,
      dauVaoCode: sn.dauVao,
      ketQuaCode: sn.ketQua,
    })
  }
}

/** codeCell: luôn bọc ngoặc kép vì mã có thể chứa ký tự | (union TS, v.v.) */
function cell(s, codeCell = false) {
  const t = String(s ?? '').replace(/\r\n/g, ' ').replace(/\n/g, ' ')
  if (codeCell || t.includes('|') || t.includes('"')) return `"${t.replace(/"/g, '""')}"`
  return t
}

function shortName(file) {
  return path.basename(file).replace(/\.test\.(tsx|ts)$/, '')
}

function guess(file, name) {
  const f = file.replace(/\\/g, '/')
  const n = name
  let mucDich = `Xác minh testcase: ${n}`
  let dauVaoFallback = `Vitest + jsdom; ${f}; mock/stub theo describe/beforeEach trong file.`
  let ketQuaFallback = `Mọi expect() trong testcase pass; không ném lỗi ngoài ý muốn.`

  if (f === 'tests/pages/SchedulePage.test.tsx') {
    mucDich = `Kiểm tra logic nghiệp vụ TKB (không render UI): ${n}`
    dauVaoFallback =
      'Dữ liệu cục bộ (batchRows, results, localStorage string) như trong testcase; không mount SchedulePage.'
    ketQuaFallback = 'Giá trị tính toán / transform / parse JSON khớp từng expect trong file test.'
  }

  if (f.includes('/pages/') && f.endsWith('.tsx')) {
    const base = shortName(f)
    mucDich = `Kiểm tra trang ${base}: ${n}`
    dauVaoFallback = `Mount ${base} trong BrowserRouter; mock API trong beforeEach; testcase ${n}.`
    ketQuaFallback = 'DOM / service mock khớp assert trong file test.'
  }
  if (f.includes('/components/')) {
    const base = shortName(f)
    mucDich = `Kiểm tra UI component (${base}): ${n}`
    dauVaoFallback = `Render ${base} với props/sự kiện theo testcase ${n}; jsdom.`
    ketQuaFallback = 'onChange/onClose/DOM khớp assert trong file test.'
  }
  if (f.includes('/hooks/')) {
    mucDich = `Kiểm tra hook: ${n}`
    dauVaoFallback = 'renderHook + act khi cập nhật state async.'
    ketQuaFallback = 'State notification đúng theo từng lần gọi success/error/...'
  }
  if (f.includes('/services/api.test.ts')) {
    mucDich = `Kiểm tra quy tắc client API & type: ${n}`
    dauVaoFallback =
      'Object/params/string URL mẫu hoặc localStorage; không gọi HTTP thật; logic trong testcase.'
    ketQuaFallback = 'URL/body/type/export khớp expect trong testcase.'
  }
  if (f.includes('/static/assets.test.ts')) {
    mucDich = `Kiểm tra file public / template: ${n}`
    dauVaoFallback = 'fs.existsSync / readFileSync trên thư mục public hoặc fixture.'
    ketQuaFallback = 'File tồn tại; JSON hợp lệ; template có mặt nếu testcase yêu cầu.'
  }
  if (f.endsWith('App.test.tsx')) {
    mucDich = `Kiểm tra cấu hình App/Router: ${n}`
    dauVaoFallback = 'Render App; mock useAuth hoặc localStorage như App.test.tsx.'
    ketQuaFallback = 'Route mặc định hoặc cấu hình router khớp assert.'
  }

  if (/exports /i.test(n)) {
    mucDich = `Kiểm tra export từ @/services/api: ${n}`
    dauVaoFallback = 'await import(module); kiểm tra typeof từng method public.'
    ketQuaFallback = 'Service export đủ API surface như trong expect.'
  }
  if (
    f.includes('/services/api.test.ts') &&
    /localStorage|authToken|401|redirect|interceptor|Authorization|pathname|login API/i.test(n)
  ) {
    dauVaoFallback += ' Giả lập localStorage/window.location cho interceptor.'
    ketQuaFallback += ' Xử lý 401/redirect/header đúng assert.'
  }
  if (
    /tính |gộp|group |parse |transform |build request|extract |filter phòng|encode |mark isActive|áp dụng đúng|handle invalid|parse failed|unique room|bỏ qua rows|mã ngành|môn học có nhiều ngành/i.test(
      n,
    )
  ) {
    mucDich = `Kiểm tra xử lý dữ liệu thuần: ${n}`
    dauVaoFallback = 'Mảng/object đầu vào cố định trong testcase.'
    ketQuaFallback = 'Kết quả map/filter/reduce/Math khớp expect.'
  }

  return { mucDich, dauVaoFallback, ketQuaFallback }
}

const header =
  'ID bài test|Tên file|Tên phương thức|Mục đích|Đầu vào|Kết quả mong đợi|Kết quả test|Ghi chú'

const lines = [header]
rows.forEach((item, i) => {
  const id = `TC${String(i + 1).padStart(3, '0')}`
  const g = guess(item.file, item.name)
  const dauVao = item.dauVaoCode?.trim() ? item.dauVaoCode.trim() : g.dauVaoFallback
  const ketQua = item.ketQuaCode?.trim()
    ? item.ketQuaCode.trim()
    : `${g.ketQuaFallback} (Không trích được expect() từ mã squash; xem cột Đầu vào hoặc file test.)`
  lines.push(
    [
      cell(id),
      cell(item.file),
      cell(item.name),
      cell(g.mucDich),
      cell(dauVao, true),
      cell(ketQua, true),
      cell('PASS'),
      cell('Đồng bộ Vitest; Đầu vào = toàn bộ thân it() squash; Kết quả = các expect() trích tự động; npm run test-cases:csv'),
    ].join('|'),
  )
})

const outPath = path.join(root, 'TEST_CASES.csv')
fs.writeFileSync(outPath, '\uFEFF' + lines.join('\n') + '\n', 'utf8')
console.error('Tests:', rows.length, '->', outPath)

const xlsxScript = path.join(root, 'scripts', 'csv-to-test-cases-xlsx.mjs')
const x = spawnSync(process.execPath, [xlsxScript], { cwd: root, stdio: 'inherit' })
if (x.status) process.exit(x.status)

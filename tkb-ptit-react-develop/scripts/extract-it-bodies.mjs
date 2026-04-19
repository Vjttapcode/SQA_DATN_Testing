/**
 * Trích xuất thân it(...) →
 * - Đầu vào: toàn bộ thân test (squash), chi tiết mã.
 * - Kết quả mong đợi: các biểu thức expect(...) + chuỗi .matcher(...) (bộ đếm ngoặc).
 */

function trySkipCommentOrString(src, i, len) {
  if (i >= len) return i
  const next2 = i + 1 < len ? src.slice(i, i + 2) : ''

  if (next2 === '//') {
    while (i < len && src[i] !== '\n') i++
    return i
  }
  if (next2 === '/*') {
    i += 2
    while (i < len - 1 && src.slice(i, i + 2) !== '*/') i++
    return Math.min(i + 2, len)
  }

  const ch = src[i]
  if (ch === "'" || ch === '"') {
    const q = ch
    i++
    while (i < len) {
      if (src[i] === '\\') {
        i += 2
        continue
      }
      if (src[i] === q) return i + 1
      i++
    }
    return len
  }

  if (ch === '`') {
    i++
    while (i < len) {
      if (src[i] === '\\') {
        i += 2
        continue
      }
      if (src[i] === '`') return i + 1
      i++
    }
    return len
  }

  return i
}

function findBodyOpenBrace(src, commaIdx) {
  let i = commaIdx + 1
  const len = src.length

  while (i < len && /\s/.test(src[i])) i++

  if (src.slice(i, i + 5) === 'async') {
    i += 5
    while (i < len && /\s/.test(src[i])) i++
  }

  if (src.slice(i, i + 8) === 'function') {
    i += 8
    while (i < len && /\s/.test(src[i])) i++
    while (i < len && /[\w$]/.test(src[i])) i++
    while (i < len && /\s/.test(src[i])) i++
    if (src[i] !== '(') return -1
    i++
    let depth = 1
    while (i < len && depth > 0) {
      const j = trySkipCommentOrString(src, i, len)
      if (j > i) {
        i = j
        continue
      }
      const c = src[i]
      if (c === '(') depth++
      else if (c === ')') depth--
      i++
    }
    while (i < len && /\s/.test(src[i])) i++
    if (src[i] !== '{') return -1
    return i
  }

  if (src[i] !== '(') return -1
  i++
  let paren = 1
  while (i < len && paren > 0) {
    const j = trySkipCommentOrString(src, i, len)
    if (j > i) {
      i = j
      continue
    }
    const c = src[i]
    if (c === '(') paren++
    else if (c === ')') paren--
    i++
  }

  while (i < len && /\s/.test(src[i])) i++
  if (src[i] !== '=' || src[i + 1] !== '>') return -1
  i += 2
  while (i < len && /\s/.test(src[i])) i++

  if (src[i] === '{') return i
  return -1
}

/** @returns {{ body: string, endExclusive: number } | null} */
function extractBraceBody(src, openBraceIdx) {
  const len = src.length
  let i = openBraceIdx + 1
  let depth = 1
  const start = i

  while (i < len && depth > 0) {
    const j = trySkipCommentOrString(src, i, len)
    if (j > i) {
      i = j
      continue
    }
    const c = src[i]
    if (c === '{') depth++
    else if (c === '}') depth--
    i++
  }

  if (depth !== 0) return null
  return { body: src.slice(start, i - 1), endExclusive: i }
}

/** Kết thúc (index sau ký tự cuối) của một chuỗi expect(...).... từ vị trí `expect(` */
function parseExpectChainEnd(src, expectOpenIdx) {
  const len = src.length
  let i = expectOpenIdx
  if (!src.startsWith('expect(', i)) return expectOpenIdx
  i += 'expect('.length
  let dep = 1
  while (i < len && dep > 0) {
    const j = trySkipCommentOrString(src, i, len)
    if (j > i) {
      i = j
      continue
    }
    if (src[i] === '(') dep++
    else if (src[i] === ')') dep--
    i++
  }
  while (i < len && /\s/.test(src[i])) i++
  while (i < len && src[i] === '.') {
    i++
    while (i < len && /[\w$]/.test(src[i])) i++
    if (src[i] === '(') {
      i++
      let d = 1
      while (i < len && d > 0) {
        const j = trySkipCommentOrString(src, i, len)
        if (j > i) {
          i = j
          continue
        }
        if (src[i] === '(') d++
        else if (src[i] === ')') d--
        i++
      }
    } else if (src[i] === '`') {
      i = trySkipCommentOrString(src, i, len)
    }
    while (i < len && /\s/.test(src[i])) i++
  }
  return i
}

/** Danh sách các biểu thức expect(...).... trong chuỗi đã squash */
function collectExpectParts(src) {
  const len = src.length
  const parts = []
  let i = 0
  while (i < len) {
    const j = trySkipCommentOrString(src, i, len)
    if (j > i) {
      i = j
      continue
    }
    const prev = i > 0 ? src[i - 1] : ' '
    const safeStart = !/[\w.$]/.test(prev)
    if (safeStart && src.startsWith('expect(', i)) {
      const end = parseExpectChainEnd(src, i)
      if (end > i) {
        parts.push(src.slice(i, end).trim())
        i = end
        continue
      }
    }
    i++
  }
  return parts
}

/** Gộp xuống một dòng, cắt bớt khoảng trắng; không đụng nội dung trong chuỗi — chỉ collapse newlines và block space ngoài string (đơn giản: collapse toàn cục, đủ cho testcase thông thường) */
function squashCode(s) {
  return s
    .replace(/\/\/[^\n]*/g, '')
    .replace(/\/\*[\s\S]*?\*\//g, '')
    .replace(/[\r\n]+/g, ' ')
    .replace(/\s+/g, ' ')
    .trim()
}

const MAX_INPUT = 22000
const MAX_OUTPUT = 22000

function clip(s, max) {
  const m = max ?? MAX_INPUT
  if (s.length <= m) return s
  return s.slice(0, m - 3) + '...'
}

/** Đọc tên testcase trong it('...', hoặc it("...", — hỗ trợ dấu nháy lồng nhau */
function scanItCall(source, fromIndex) {
  const re = /\bit(?:\.(?:only|skip|todo|concurrent))?\(\s*/g
  re.lastIndex = fromIndex
  const head = re.exec(source)
  if (!head) return null
  let i = head.index + head[0].length
  const open = source[i]
  if (open !== "'" && open !== '"') return null
  i++
  let name = ''
  while (i < source.length) {
    if (source[i] === '\\') {
      name += source[i + 1] ?? ''
      i += 2
      continue
    }
    if (source[i] === open) break
    name += source[i]
    i++
  }
  if (i >= source.length || source[i] !== open) return null
  i++
  while (i < source.length && /\s/.test(source[i])) i++
  if (source[i] !== ',') return null
  const commaIdx = i
  return { name, commaIdx, itHeadStart: head.index }
}

/**
 * @param {string} source
 * @returns {{ name: string, dauVao: string, ketQua: string }[]}
 */
export function extractItSnippets(source) {
  const out = []
  let from = 0
  while (from < source.length) {
    const m = scanItCall(source, from)
    if (!m) break
    const { name, commaIdx } = m
    const braceIdx = findBodyOpenBrace(source, commaIdx)
    if (braceIdx < 0) {
      out.push({ name, dauVao: '', ketQua: '' })
      from = m.itHeadStart + 3
      continue
    }
    const extracted = extractBraceBody(source, braceIdx)
    if (!extracted) {
      out.push({ name, dauVao: '', ketQua: '' })
      from = m.itHeadStart + 3
      continue
    }
    const { body, endExclusive } = extracted
    from = endExclusive

    const squashed = squashCode(body)
    const expectParts = collectExpectParts(squashed)
    const ketRaw = expectParts.join(' ')
    let setupOnly = squashed
    for (const p of expectParts) {
      setupOnly = setupOnly.split(p).join(' ')
    }
    setupOnly = squashCode(setupOnly)
    const dauVao = clip(setupOnly.replace(/\s/g, '').length ? setupOnly : squashed, MAX_INPUT)
    const ketQua = clip(ketRaw, MAX_OUTPUT)

    out.push({ name, dauVao, ketQua })
  }
  return out
}

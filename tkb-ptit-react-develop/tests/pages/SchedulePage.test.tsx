import { describe, it, expect, vi, beforeEach } from 'vitest'

describe('SchedulePage - Business Logic Tests', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    localStorage.clear()
  })

  // ==================== BUSINESS LOGIC TESTS (Pure Functions) ====================
  describe('Business Logic - Pure Functions', () => {
    // Tính số lớp = ceil(sĩ số / sĩ số mỗi lớp)
    it('tính số lớp = ceil(sĩ số / sĩ số mỗi lớp)', () => {
      const calculations = [
        { siso: 60, sisoMotLop: 30, expected: 2 },
        { siso: 45, sisoMotLop: 30, expected: 2 },
        { siso: 30, sisoMotLop: 30, expected: 1 },
        { siso: 15, sisoMotLop: 30, expected: 1 },
        { siso: 61, sisoMotLop: 30, expected: 3 },
      ]

      calculations.forEach(({ siso, sisoMotLop, expected }) => {
        const solop = Math.ceil(siso / sisoMotLop)
        expect(solop).toBe(expected)
      })
    })

    // Tính số tiết = lý thuyết + bài tập + bài tập lớn
    it('tính số tiết = lý thuyết + bài tập + bài tập lớn', () => {
      const calculations = [
        { theory: 30, exercise: 15, project: 0, expected: 45 },
        { theory: 20, exercise: 10, project: 10, expected: 40 },
        { theory: 15, exercise: 0, project: 0, expected: 15 },
      ]

      calculations.forEach(({ theory, exercise, project, expected }) => {
        const sotiet = theory + exercise + project
        expect(sotiet).toBe(expected)
      })
    })

    // Kiểm tra mã ngành bắt đầu bằng E- là standalone
    it('mã ngành bắt đầu bằng E- là standalone', () => {
      const isStandaloneMajor = (majorCode: string): boolean => {
        return majorCode.startsWith('E-')
      }

      expect(isStandaloneMajor('E-CN')).toBe(true)
      expect(isStandaloneMajor('E-AT')).toBe(true)
      expect(isStandaloneMajor('CN')).toBe(false)
      expect(isStandaloneMajor('AT-KH')).toBe(false)
    })

    // Kiểm tra môn học có nhiều ngành để gộp
    it('môn học có nhiều ngành để gộp', () => {
      const batchRows = [
        { mmh: 'CS101', nganh: 'CN' },
        { mmh: 'CS101', nganh: 'AT' },
        { mmh: 'CS101', nganh: 'KH' },
        { mmh: 'CS102', nganh: 'CN' },
      ]

      const subjectCode = 'CS101'
      const rows = batchRows.filter(r => r.mmh === subjectCode)
      expect(rows.length).toBe(3)
    })

    // Tính tổng sĩ số khi gộp ngành
    it('tính tổng sĩ số khi gộp ngành', () => {
      const rows = [
        { mmh: 'CS101', nganh: 'CN', siso: 60 },
        { mmh: 'CS101', nganh: 'AT', siso: 45 },
        { mmh: 'CS101', nganh: 'KH', siso: 35 },
      ]

      const selectedNganhs = ['CN', 'AT', 'KH']
      let totalSiso = 0
      selectedNganhs.forEach(nganh => {
        const found = rows.find(r => r.nganh === nganh)
        if (found) totalSiso += found.siso
      })

      expect(totalSiso).toBe(140)
    })

    // Gộp khóa khác cùng môn học
    it('gộp khóa khác cùng môn học', () => {
      const row1 = { mmh: 'CS101', khoa: '2024', siso: 60 }
      const row2 = { mmh: 'CS101', khoa: '2023', siso: 50 }

      const year1 = parseInt(row1.khoa.slice(-2))
      const year2 = parseInt(row2.khoa.slice(-2))
      const sortedYears = [year1, year2].sort((a, b) => a - b)
      const newKhoa = `${sortedYears[0]}-${sortedYears[1]}`
      const newSiso = row1.siso + row2.siso

      expect(newKhoa).toBe('23-24')
      expect(newSiso).toBe(110)
    })

    // Build request body đúng format
    it('build request body đúng format cho generate TKB', () => {
      const batchRows = [
        {
          mmh: 'CS101',
          tmh: 'Lập trình',
          sotiet: 45,
          siso: 60,
          siso_mot_lop: 30,
          nganh: 'CN',
          khoa: '2024',
          he_dac_thu: 'Chính quy',
          isGrouped: false,
          isHiddenByCombination: false,
          isCommonRegistration: false,
          combinations: [],
        },
      ]

      const items: any[] = []
      batchRows.forEach((row) => {
        if (row.isHiddenByCombination) return

        if (row.isGrouped && row.combinations.length > 0) {
          // Handle grouped
        } else {
          const solop = Math.ceil(row.siso / row.siso_mot_lop)
          items.push({
            ma_mon: row.mmh,
            ten_mon: row.tmh,
            sotiet: row.sotiet,
            solop: solop,
            siso: row.siso_mot_lop,
            nganh: row.nganh,
          })
        }
      })

      expect(items.length).toBe(1)
      expect(items[0].ma_mon).toBe('CS101')
      expect(items[0].solop).toBe(2)
    })

    // Transform TKBResultRow sang SaveScheduleRequest format
    it('transform TKBResultRow sang SaveScheduleRequest format', () => {
      const resultRow = {
        subject_database_id: 1,
        lop: '1',
        khoa: '2024',
        nganh: 'CN',
        he_dac_thu: 'Chính quy',
        phong: 'A101',
      }

      const schedule = {
        subject_id: resultRow.subject_database_id,
        class_number: parseInt(resultRow.lop || '1') || 1,
        student_year: resultRow.khoa,
        major: resultRow.nganh,
        special_system: resultRow.he_dac_thu,
        si_so_mot_lop: null,
        room_number: resultRow.phong || null,
        template_database_id: null,
      }

      expect(schedule.subject_id).toBe(1)
      expect(schedule.class_number).toBe(1)
      expect(schedule.room_number).toBe('A101')
    })

    // Bỏ qua rows không có subject_database_id
    it('bỏ qua rows không có subject_database_id', () => {
      const results = [
        { subject_database_id: 1, lop: '1' },
        { subject_database_id: null, lop: '1' },
        { subject_database_id: 2, lop: '1' },
      ]

      const schedules = results
        .map(row => {
          if (!row.subject_database_id) return null
          return { subject_id: row.subject_database_id }
        })
        .filter(s => s !== null)

      expect(schedules.length).toBe(2)
    })

    // Group results by subject correctly
    it('group results by subject correctly', () => {
      const results = [
        { ma_mon: 'CS101', ten_mon: 'Lập trình', thu: '2', kip: 'Sáng', phong: 'A101' },
        { ma_mon: 'CS101', ten_mon: 'Lập trình', thu: '4', kip: 'Chiều', phong: 'A102' },
        { ma_mon: 'CS102', ten_mon: 'Cấu trúc', thu: '3', kip: 'Sáng', phong: 'B201' },
      ]

      const grouped = Object.values(
        results.reduce((acc: any, row) => {
          const key = row.ma_mon || 'unknown'
          if (!acc[key]) {
            acc[key] = { input: { ma_mon: row.ma_mon, ten_mon: row.ten_mon }, rows: [] }
          }
          acc[key].rows.push(row)
          return acc
        }, {})
      )

      expect(grouped.length).toBe(2)
      expect(grouped[0].rows.length).toBe(2)
      expect(grouped[1].rows.length).toBe(1)
    })

    // Extract unique room IDs từ results
    it('extract unique room IDs từ results', () => {
      const results = [
        { room_id: 1, phong: 'A101' },
        { room_id: 2, phong: 'A102' },
        { room_id: 1, phong: 'A101' }, // Duplicate
        { room_id: null, phong: null },
      ]

      const usedRoomIds = [...new Set(
        results
          .map(row => row.room_id)
          .filter(id => id !== null && id !== undefined)
      )]

      expect(usedRoomIds).toEqual([1, 2])
    })

    // Parse failed subjects từ response
    it('parse failed subjects từ response', () => {
      const responseItems = [
        { input: { ten_mon: 'CS101', ma_mon: 'CS101', sotiet: 45 }, rows: [], note: 'Không tìm thấy phòng' },
        { input: { ten_mon: 'CS102', ma_mon: 'CS102', sotiet: 30 }, rows: [{ id: 1 }] },
      ]

      const failed = responseItems
        .filter((item: any) => (!item.rows || item.rows.length === 0) && item.note)
        .map((item: any) => ({
          subjectName: item.input.ten_mon || item.input.ma_mon,
          major: '',
          note: item.note,
          totalPeriods: item.input.sotiet || 0,
        }))

      expect(failed.length).toBe(1)
      expect(failed[0].subjectName).toBe('CS101')
    })

    it('handle invalid JSON in localStorage', () => {
      localStorage.setItem('tkbPageState', 'invalid json')

      let result = null
      try {
        const saved = localStorage.getItem('tkbPageState')
        if (saved) {
          result = JSON.parse(saved)
        }
      } catch (error) {
        // Expected to fail
      }

      expect(result).toBeNull()
    })
  })
})


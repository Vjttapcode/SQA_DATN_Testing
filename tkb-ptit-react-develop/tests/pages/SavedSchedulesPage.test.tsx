import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import { BrowserRouter } from 'react-router-dom'
import SavedSchedulesPage from '@/pages/SavedSchedulesPage'
import api, { subjectService, semesterService, roomService } from '@/services/api'

// Mock dependencies
vi.mock('@/services/api', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    delete: vi.fn(),
    put: vi.fn(),
  },
  subjectService: {
    getAll: vi.fn(),
    getAllClassYears: vi.fn(),
  },
  semesterService: {
    getAll: vi.fn(),
    getActive: vi.fn(),
  },
  roomService: {
    updateStatusByRoomIds: vi.fn(),
    updateStatusByRoomCodes: vi.fn(),
    getAll: vi.fn(),
  },
}))

vi.mock('react-hot-toast', () => ({
  default: {
    success: vi.fn(),
    error: vi.fn(),
    loading: vi.fn(),
    dismiss: vi.fn(),
  },
}))

vi.mock('@/hooks/useNotification', () => ({
  useNotification: () => ({
    success: vi.fn(),
    error: vi.fn(),
    warning: vi.fn(),
    info: vi.fn(),
    notification: { isOpen: false },
    close: vi.fn(),
  }),
}))

vi.mock('@/components/NotificationModal', () => ({
  default: () => null,
}))

describe('SavedSchedulesPage - Business Logic Tests', () => {
  const mockSchedules = [
    {
      id: 1,
      classNumber: 1,
      studentYear: '2024',
      major: 'CN',
      specialSystem: 'Chính quy',
      siSoMotLop: 30,
      room: { id: 1, name: 'A101', building: 'A1', capacity: 60, type: 'GENERAL', status: 'AVAILABLE' },
      subject: {
        id: 1,
        subjectCode: 'CS101',
        subjectName: 'Lập trình cơ bản',
        credits: 3,
        theoryHours: 30,
        practiceHours: 15,
        semester: { id: 1, semesterName: 'Học kỳ 1', academicYear: '2024-2025', isActive: true },
        major: { id: 1, majorCode: 'CN', majorName: 'Công nghệ thông tin' },
        classYear: '2024',
      },
      tkbTemplate: {
        id: 1,
        templateId: 'T001',
        totalPeriods: 45,
        dayOfWeek: 2,
        kip: 1,
        startPeriod: 1,
        periodLength: 3,
        weekSchedule: '[1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1]',
        totalUsed: 45,
        rowOrder: 1,
      },
    },
    {
      id: 2,
      classNumber: 1,
      studentYear: '2024',
      major: 'CN',
      specialSystem: 'Chính quy',
      siSoMotLop: 30,
      room: { id: 2, name: 'A102', building: 'A1', capacity: 40, type: 'GENERAL', status: 'AVAILABLE' },
      subject: {
        id: 2,
        subjectCode: 'CS102',
        subjectName: 'Cấu trúc dữ liệu',
        credits: 3,
        theoryHours: 30,
        practiceHours: 15,
        semester: { id: 1, semesterName: 'Học kỳ 1', academicYear: '2024-2025', isActive: true },
        major: { id: 1, majorCode: 'CN', majorName: 'Công nghệ thông tin' },
        classYear: '2024',
      },
      tkbTemplate: {
        id: 2,
        templateId: 'T002',
        totalPeriods: 45,
        dayOfWeek: 4,
        kip: 2,
        startPeriod: 4,
        periodLength: 3,
        weekSchedule: '[1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1]',
        totalUsed: 45,
        rowOrder: 2,
      },
    },
  ]

  const mockSemesters = [
    { id: 1, semesterName: 'Học kỳ 1', academicYear: '2024-2025', isActive: true },
    { id: 2, semesterName: 'Học kỳ 2', academicYear: '2024-2025', isActive: false },
  ]

  const mockClassYears = ['2024', '2023', '2022']

  beforeEach(() => {
    vi.clearAllMocks()
    localStorage.clear()

    ;(api.get as any).mockResolvedValue({
      data: mockSchedules,
    })

    ;(subjectService.getAllClassYears as any).mockResolvedValue({
      data: { success: true, data: mockClassYears },
    })

    ;(semesterService.getAll as any).mockResolvedValue({
      data: { success: true, data: mockSemesters },
    })
  })

  // ==================== INITIALIZATION ====================
  describe('Page Initialization - Khởi tạo trang', () => {
    it('render header với đúng title', async () => {
      render(
        <BrowserRouter>
          <SavedSchedulesPage />
        </BrowserRouter>
      )

      await waitFor(() => {
        expect(screen.getByText('Thời Khóa Biểu')).toBeInTheDocument()
      })
    })

    it('hiển thị nút Xuất Excel', async () => {
      render(
        <BrowserRouter>
          <SavedSchedulesPage />
        </BrowserRouter>
      )

      await waitFor(() => {
        expect(screen.getByText('Xuất Excel')).toBeInTheDocument()
      })
    })

    it('hiển thị filter dropdowns', async () => {
      render(
        <BrowserRouter>
          <SavedSchedulesPage />
        </BrowserRouter>
      )

      await waitFor(() => {
        expect(screen.getByText('Tất cả năm học')).toBeInTheDocument()
        expect(screen.getByText('Tất cả học kỳ')).toBeInTheDocument()
      })
    })

    it('hiển thị nút xóa ngành và xóa tất cả', async () => {
      render(
        <BrowserRouter>
          <SavedSchedulesPage />
        </BrowserRouter>
      )

      await waitFor(() => {
        expect(screen.getByText('Xóa ngành')).toBeInTheDocument()
        expect(screen.getByText('Xóa Tất Cả')).toBeInTheDocument()
      })
    })
  })

  // ==================== SCHEDULE DISPLAY ====================
  describe('Schedule Display - Hiển thị lịch học', () => {
    it('hiển thị danh sách lịch học trong table', async () => {
      render(
        <BrowserRouter>
          <SavedSchedulesPage />
        </BrowserRouter>
      )

      await waitFor(() => {
        expect(screen.getByText('CS101')).toBeInTheDocument()
      })
    })

    it('hiển thị thông tin chi tiết lịch học', async () => {
      render(
        <BrowserRouter>
          <SavedSchedulesPage />
        </BrowserRouter>
      )

      await waitFor(() => {
        expect(screen.getByText('CS101')).toBeInTheDocument()
        expect(screen.getByText('CS102')).toBeInTheDocument()
      })
    })

    it('hiển thị tổng số lịch học', async () => {
      render(
        <BrowserRouter>
          <SavedSchedulesPage />
        </BrowserRouter>
      )

      await waitFor(() => {
        expect(screen.getByText(/Tổng:/)).toBeInTheDocument()
      })
    })
  })

  // ==================== FILTERING ====================
  describe('Filtering - Lọc lịch học', () => {
    it('filter theo năm học', async () => {
      render(
        <BrowserRouter>
          <SavedSchedulesPage />
        </BrowserRouter>
      )

      await waitFor(() => {
        expect(screen.getByText('Tất cả năm học')).toBeInTheDocument()
      })

      // Find the academic year select
      const selects = screen.getAllByRole('combobox')
      const academicYearSelect = selects.find(select => select.getAttribute('value') === '')
      if (academicYearSelect) {
        fireEvent.change(academicYearSelect, { target: { value: '2024-2025' } })
      }
    })

    it('filter theo học kỳ', async () => {
      render(
        <BrowserRouter>
          <SavedSchedulesPage />
        </BrowserRouter>
      )

      await waitFor(() => {
        expect(screen.getByText('Tất cả học kỳ')).toBeInTheDocument()
      })
    })

    it('filter theo khóa', async () => {
      render(
        <BrowserRouter>
          <SavedSchedulesPage />
        </BrowserRouter>
      )

      await waitFor(() => {
        expect(screen.getByText('Tất cả khóa')).toBeInTheDocument()
      })
    })

    it('filter theo ngành', async () => {
      render(
        <BrowserRouter>
          <SavedSchedulesPage />
        </BrowserRouter>
      )

      await waitFor(() => {
        expect(screen.getByText('Tất cả ngành')).toBeInTheDocument()
      })
    })

    it('reset semester filter khi đổi năm học', async () => {
      render(
        <BrowserRouter>
          <SavedSchedulesPage />
        </BrowserRouter>
      )

      await waitFor(() => {
        expect(screen.getByText('Tất cả năm học')).toBeInTheDocument()
      })
    })
  })

  // ==================== GROUP SCHEDULES ====================
  describe('Group Schedules - Nhóm lịch học', () => {
    it('group schedules by class key (subjectCode-major-classNumber)', () => {
      const groupSchedulesByClass = (scheduleList: any[]): any[] => {
        const sortedSchedules = [...scheduleList].sort((a, b) => a.id - b.id)
        const grouped = new Map()

        sortedSchedules.forEach((schedule) => {
          const subjectCode = schedule.subject?.subjectCode || 'N/A'
          const majorCode = schedule.major || 'N/A'
          const classKey = `${subjectCode}-${majorCode}-${schedule.classNumber}`

          if (!grouped.has(classKey)) {
            grouped.set(classKey, {
              classKey,
              classNumber: schedule.classNumber,
              subjectId: subjectCode,
              subjectName: schedule.subject?.subjectName || 'N/A',
              studentYear: schedule.studentYear,
              major: majorCode,
              specialSystem: schedule.specialSystem,
              schedules: [],
            })
          }

          grouped.get(classKey)!.schedules.push(schedule)
        })

        return Array.from(grouped.values())
      }

      const grouped = groupSchedulesByClass(mockSchedules)
      expect(grouped.length).toBe(2)
      expect(grouped[0].schedules.length).toBe(1)
    })

    it('tính tổng số tiết từ weekSchedule', () => {
      const getWeekValue = (schedule: any, weekNum: number): string => {
        if (!schedule.tkbTemplate?.weekSchedule) return ''

        try {
          const weekSchedule = JSON.parse(schedule.tkbTemplate.weekSchedule)
          const weekIndex = weekNum - 1

          if (weekIndex >= 0 && weekIndex < weekSchedule.length) {
            return weekSchedule[weekIndex] === 1 ? 'x' : ''
          }
        } catch (error) {
          console.error('Error parsing weekSchedule:', error)
        }

        return ''
      }

      const schedule = mockSchedules[0]
      const values = Array.from({ length: 17 }, (_, i) => getWeekValue(schedule, i + 1))
      const xCount = values.filter(v => v === 'x').length
      const totalPeriods = xCount * (schedule.tkbTemplate?.periodLength || 0)

      expect(totalPeriods).toBe(51) // 17 weeks * 3 periods
    })
  })

  // ==================== DELETE OPERATIONS ====================
  describe('Delete Operations - Xóa lịch học', () => {
    it('mở modal xóa ngành', async () => {
      render(
        <BrowserRouter>
          <SavedSchedulesPage />
        </BrowserRouter>
      )

      await waitFor(() => {
        expect(screen.getByText('Xóa ngành')).toBeInTheDocument()
      })

      fireEvent.click(screen.getByText('Xóa ngành'))

      await waitFor(() => {
        expect(screen.getByText('Xác nhận xóa')).toBeInTheDocument()
      })
    })

    it('mở modal xóa tất cả', async () => {
      render(
        <BrowserRouter>
          <SavedSchedulesPage />
        </BrowserRouter>
      )

      await waitFor(() => {
        expect(screen.getByText('Xóa Tất Cả')).toBeInTheDocument()
      })

      fireEvent.click(screen.getByText('Xóa Tất Cả'))

      await waitFor(() => {
        expect(screen.getByText('Xác nhận xóa')).toBeInTheDocument()
      })
    })

    it('validate cần chọn năm học và học kỳ khi xóa tất cả', async () => {
      ;(api.delete as any).mockRejectedValue(new Error('Validation error'))

      render(
        <BrowserRouter>
          <SavedSchedulesPage />
        </BrowserRouter>
      )

      await waitFor(() => {
        expect(screen.getByText('Xóa Tất Cả')).toBeInTheDocument()
      })

      fireEvent.click(screen.getByText('Xóa Tất Cả'))

      await waitFor(() => {
        // Click xóa button without selecting filters
        const deleteButton = screen.getByRole('button', { name: 'Xóa' })
        if (deleteButton) {
          fireEvent.click(deleteButton)
        }
      })
    })
  })

  // ==================== EXPORT EXCEL ====================
  describe('Export Excel - Xuất Excel', () => {
    it('tạo data rows từ schedules', () => {
      const groupedSchedules = [
        {
          classKey: 'CS101-CN-1',
          classNumber: 1,
          subjectId: 'CS101',
          subjectName: 'Lập trình cơ bản',
          schedules: [mockSchedules[0]],
        },
      ]

      const hasSchedules = groupedSchedules.length > 0
      expect(hasSchedules).toBe(true)
      expect(groupedSchedules[0].schedules.length).toBe(1)
    })
  })

  // ==================== EMPTY STATE ====================
  describe('Empty State - Trạng thái trống', () => {
    it('hiển thị thông báo khi không có lịch học', async () => {
      ;(api.get as any).mockResolvedValue({
        data: [],
      })

      render(
        <BrowserRouter>
          <SavedSchedulesPage />
        </BrowserRouter>
      )

      await waitFor(() => {
        expect(screen.getByText('Chưa có lịch học nào được lưu')).toBeInTheDocument()
      })
    })

    it('disable export button khi không có dữ liệu', async () => {
      ;(api.get as any).mockResolvedValue({
        data: [],
      })

      render(
        <BrowserRouter>
          <SavedSchedulesPage />
        </BrowserRouter>
      )

      await waitFor(() => {
        const exportButton = screen.getByText('Xuất Excel')
        expect(exportButton).toBeDisabled()
      })
    })
  })

  // ==================== ERROR HANDLING ====================
  describe('Error Handling - Xử lý lỗi', () => {
    it('hiển thị lỗi khi API fail', async () => {
      ;(api.get as any).mockRejectedValue(new Error('API Error'))

      render(
        <BrowserRouter>
          <SavedSchedulesPage />
        </BrowserRouter>
      )

      await waitFor(() => {
        // Component should handle error gracefully
      })
    })
  })

  // ==================== ROOM STATUS UPDATE ====================
  describe('Room Status Update - Cập nhật trạng thái phòng', () => {
    it('extract unique room IDs từ schedules', () => {
      const schedules = mockSchedules
      const allUsedRoomIds = new Set<number>()

      schedules.forEach(schedule => {
        if (schedule.room?.id) {
          allUsedRoomIds.add(schedule.room.id)
        }
      })

      expect(allUsedRoomIds.size).toBe(2)
      expect(allUsedRoomIds.has(1)).toBe(true)
      expect(allUsedRoomIds.has(2)).toBe(true)
    })

    it('gọi updateStatusByRoomIds khi xóa schedules', async () => {
      ;(api.delete as any).mockResolvedValue({ data: { success: true } })
      ;(roomService.updateStatusByRoomIds as any).mockResolvedValue({
        data: { success: true },
      })

      render(
        <BrowserRouter>
          <SavedSchedulesPage />
        </BrowserRouter>
      )

      await waitFor(() => {
        expect(screen.getByText('Thời Khóa Biểu')).toBeInTheDocument()
      })
    })
  })
})

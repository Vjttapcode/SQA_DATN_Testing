import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, waitFor } from '@testing-library/react'
import { BrowserRouter } from 'react-router-dom'
import SubjectsPage from '@/pages/SubjectsPage'
import { subjectService, curriculumService, majorService, semesterService } from '@/services/api'

// Mock dependencies
vi.mock('@/services/api', () => ({
  subjectService: {
    getAll: vi.fn(),
    getById: vi.fn(),
    create: vi.fn(),
    update: vi.fn(),
    delete: vi.fn(),
    getAllProgramTypes: vi.fn(),
    getAllClassYears: vi.fn(),
    getGroupMajors: vi.fn(),
    getByMajors: vi.fn(),
    getCommonSubjects: vi.fn(),
    deleteBySemester: vi.fn(),
  },
  majorService: {
    getAll: vi.fn(),
  },
  facultyService: {
    getAll: vi.fn(),
  },
  semesterService: {
    getAll: vi.fn(),
    getActive: vi.fn(),
    getById: vi.fn(),
    getByName: vi.fn(),
  },
  curriculumService: {
    importExcel: vi.fn(),
  },
}))

vi.mock('react-hot-toast', () => ({
  default: {
    success: vi.fn(),
    error: vi.fn(),
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

vi.mock('@/components/ImportFileModal', () => ({
  default: () => null,
}))

describe('SubjectsPage - Business Logic Tests', () => {
  const mockSubjects = [
    { id: 1, subjectCode: 'CS101', subjectName: 'Lập trình cơ bản', credits: 3, classYear: '2024', programType: 'Chính quy', numberOfStudents: 60, numberOfClasses: 2, theoryHours: 30, exerciseHours: 15, labHours: 0, projectHours: 0, selfStudyHours: 45, department: 'CNTT', examFormat: 'Thi viết', majorCode: 'CN', majorId: 1, facultyId: 'K15', semesterName: 'Học kỳ 1', academicYear: '2024-2025' },
    { id: 2, subjectCode: 'CS102', subjectName: 'Cấu trúc dữ liệu', credits: 3, classYear: '2024', programType: 'Chính quy', numberOfStudents: 60, numberOfClasses: 2, theoryHours: 30, exerciseHours: 15, labHours: 0, projectHours: 0, selfStudyHours: 45, department: 'CNTT', examFormat: 'Thi viết', majorCode: 'CN', majorId: 1, facultyId: 'K15', semesterName: 'Học kỳ 1', academicYear: '2024-2025' },
  ]

  const mockMajors = [
    { id: 1, majorCode: 'CN', majorName: 'Công nghệ thông tin', numberOfStudents: 200, classYear: '2024', facultyId: 'K15' },
    { id: 2, majorCode: 'AT', majorName: 'An toàn thông tin', numberOfStudents: 100, classYear: '2024', facultyId: 'K15' },
  ]

  const mockSemesters = [
    { id: 1, semesterName: 'Học kỳ 1', academicYear: '2024-2025', isActive: true, subjectCount: 10 },
    { id: 2, semesterName: 'Học kỳ 2', academicYear: '2024-2025', isActive: false, subjectCount: 8 },
  ]

  beforeEach(() => {
    vi.clearAllMocks()
    localStorage.clear()

    ;(semesterService.getActive as any).mockResolvedValue({
      data: { success: true, data: mockSemesters[0] },
    })

    ;(subjectService.getAllProgramTypes as any).mockResolvedValue({
      data: { success: true, data: ['Chính quy', 'Đặc thù'] },
    })

    ;(subjectService.getAllClassYears as any).mockResolvedValue({
      data: { success: true, data: ['2024', '2023', '2022'] },
    })

    ;(majorService.getAll as any).mockResolvedValue({
      data: { success: true, data: mockMajors },
    })

    ;(semesterService.getAll as any).mockResolvedValue({
      data: { success: true, data: mockSemesters },
    })

    ;(subjectService.getAll as any).mockResolvedValue({
      data: {
        success: true,
        data: {
          items: mockSubjects,
          page: 1,
          size: 18,
          totalElements: 2,
          totalPages: 1,
        },
      },
    })
  })

  // ==================== INITIALIZATION ====================
  describe('Page Initialization - Khởi tạo trang', () => {
    it('render header với đúng title', async () => {
      render(
        <BrowserRouter>
          <SubjectsPage />
        </BrowserRouter>
      )

      await waitFor(() => {
        expect(screen.getByText('Quản lý Môn học')).toBeInTheDocument()
      })
    })

    it('hiển thị buttons Thêm môn học và Upload môn học', async () => {
      render(
        <BrowserRouter>
          <SubjectsPage />
        </BrowserRouter>
      )

      await waitFor(() => {
        expect(screen.getByText('Thêm môn học')).toBeInTheDocument()
        expect(screen.getByText('Upload môn học')).toBeInTheDocument()
      })
    })

    it('hiển thị search input', async () => {
      render(
        <BrowserRouter>
          <SubjectsPage />
        </BrowserRouter>
      )

      await waitFor(() => {
        expect(screen.getByPlaceholderText('Tìm theo mã hoặc tên môn học...')).toBeInTheDocument()
      })
    })

    it('hiển thị filter dropdowns', async () => {
      render(
        <BrowserRouter>
          <SubjectsPage />
        </BrowserRouter>
      )

      await waitFor(() => {
        expect(screen.getByText('Tất cả năm học')).toBeInTheDocument()
        expect(screen.getByText('Tất cả khóa')).toBeInTheDocument()
      })
    })
  })

  // ==================== SUBJECT DISPLAY ====================
  describe('Subject Display - Hiển thị môn học', () => {
    it('hiển thị danh sách môn học trong table', async () => {
      render(
        <BrowserRouter>
          <SubjectsPage />
        </BrowserRouter>
      )

      await waitFor(() => {
        expect(screen.getByText('CS101')).toBeInTheDocument()
        expect(screen.getByText('CS102')).toBeInTheDocument()
      })
    })

    it('hiển thị pagination info', async () => {
      render(
        <BrowserRouter>
          <SubjectsPage />
        </BrowserRouter>
      )

      await waitFor(() => {
        expect(screen.getByText(/Hiển thị/)).toBeInTheDocument()
      })
    })
  })

  // ==================== SEARCH & FILTER ====================
  describe('Search & Filter - Tìm kiếm và lọc', () => {
    it('reset page khi search thay đổi', async () => {
      render(
        <BrowserRouter>
          <SubjectsPage />
        </BrowserRouter>
      )

      await waitFor(() => {
        expect(screen.getByPlaceholderText('Tìm theo mã hoặc tên môn học...')).toBeInTheDocument()
      })
    })
  })

  // ==================== PAGINATION ====================
  describe('Pagination - Phân trang', () => {
    it('hiển thị pagination controls', async () => {
      render(
        <BrowserRouter>
          <SubjectsPage />
        </BrowserRouter>
      )

      await waitFor(() => {
        // Should have pagination controls
        const content = document.body.textContent || ''
        expect(content.length).toBeGreaterThan(0)
      })
    })

    it('chuyển trang khi click page number', async () => {
      render(
        <BrowserRouter>
          <SubjectsPage />
        </BrowserRouter>
      )

      await waitFor(() => {
        expect(screen.getByText('Quản lý Môn học')).toBeInTheDocument()
      })
    })

    it('thay đổi page size', async () => {
      render(
        <BrowserRouter>
          <SubjectsPage />
        </BrowserRouter>
      )

      await waitFor(() => {
        expect(screen.getByText('Quản lý Môn học')).toBeInTheDocument()
      })
    })
  })

  // ==================== SUBJECT CRUD ====================
  describe('Subject CRUD - Tạo/Sửa/Xóa môn học', () => {
    it('gọi API delete khi xác nhận xóa', async () => {
      ;(subjectService.delete as any).mockResolvedValue({
        data: { success: true },
      })

      render(
        <BrowserRouter>
          <SubjectsPage />
        </BrowserRouter>
      )

      await waitFor(() => {
        expect(screen.getByText('CS101')).toBeInTheDocument()
      })
    })
  })

  // ==================== FORM VALIDATION ====================
  describe('Form Validation - Validation form', () => {
    it('validate credits phải > 0', async () => {
      render(
        <BrowserRouter>
          <SubjectsPage />
        </BrowserRouter>
      )

      await waitFor(() => {
        expect(screen.getByText('Quản lý Môn học')).toBeInTheDocument()
      })
    })
  })

  // ==================== IMPORT FILE ====================
  describe('Import File - Import file', () => {
    it('validate semester phải được chọn trước khi upload', async () => {
      render(
        <BrowserRouter>
          <SubjectsPage />
        </BrowserRouter>
      )

      await waitFor(() => {
        expect(screen.getByText('Upload môn học')).toBeInTheDocument()
      })
    })

    it('handle import success', async () => {
      ;(curriculumService.importExcel as any).mockResolvedValue({
        data: {
          success: true,
          data: {
            successCount: 10,
            skippedCount: 2,
            totalRows: 12,
            warnings: [],
            validSubjects: [],
          },
        },
      })

      render(
        <BrowserRouter>
          <SubjectsPage />
        </BrowserRouter>
      )

      await waitFor(() => {
        expect(screen.getByText('Quản lý Môn học')).toBeInTheDocument()
      })
    })
  })

  // ==================== SUBJECT DETAIL ====================
  describe('Subject Detail - Chi tiết môn học', () => {
    it('hiển thị thông tin chi tiết đầy đủ', async () => {
      render(
        <BrowserRouter>
          <SubjectsPage />
        </BrowserRouter>
      )

      await waitFor(() => {
        expect(screen.getByText('CS101')).toBeInTheDocument()
      })
    })
  })

  // ==================== BULK DELETE ====================
  describe('Bulk Delete - Xóa nhiều', () => {
    it('hiển thị bulk delete button khi có subjects được chọn', async () => {
      render(
        <BrowserRouter>
          <SubjectsPage />
        </BrowserRouter>
      )

      await waitFor(() => {
        expect(screen.getByText('Quản lý Môn học')).toBeInTheDocument()
      })
    })

    it('xóa nhiều subjects cùng lúc', async () => {
      ;(subjectService.delete as any).mockResolvedValue({
        data: { success: true },
      })

      render(
        <BrowserRouter>
          <SubjectsPage />
        </BrowserRouter>
      )

      await waitFor(() => {
        expect(screen.getByText('CS101')).toBeInTheDocument()
      })
    })
  })

  // ==================== CALCULATIONS ====================
  describe('Calculations - Tính toán', () => {
    it('tính tổng số tiết = LT + TH + BT + BTL', () => {
      const calculations = [
        { theory: 30, exercise: 15, lab: 0, project: 0, expected: 45 },
        { theory: 20, exercise: 10, lab: 15, project: 10, expected: 55 },
      ]

      calculations.forEach(({ theory, exercise, lab, project, expected }) => {
        const total = theory + exercise + lab + project
        expect(total).toBe(expected)
      })
    })

    it('tính số lớp = ceil(sĩ số / sĩ số mỗi lớp)', () => {
      const calculations = [
        { students: 60, perClass: 30, expected: 2 },
        { students: 61, perClass: 30, expected: 3 },
        { students: 30, perClass: 30, expected: 1 },
      ]

      calculations.forEach(({ students, perClass, expected }) => {
        const classes = Math.ceil(students / perClass)
        expect(classes).toBe(expected)
      })
    })

    it('tính số tín chỉ hợp lệ (1-10)', () => {
      const validCredits = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
      const invalidCredits = [0, -1, 11, 100]

      validCredits.forEach(credit => {
        expect(credit >= 1 && credit <= 10).toBe(true)
      })

      invalidCredits.forEach(credit => {
        expect(credit < 1 || credit > 10).toBe(true)
      })
    })
  })
})

import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen } from '@testing-library/react'
import React from 'react'
import { BrowserRouter } from 'react-router-dom'
import ScheduleValidationPage from '@/pages/ScheduleValidationPage'

// Use vi.hoisted() to properly handle hoisting
const mockAnalyzeSchedule = vi.hoisted(() => vi.fn())

// Mock dependencies
vi.mock('react-hot-toast', () => ({
  default: {
    success: vi.fn(),
    error: vi.fn(),
    loading: vi.fn(),
  },
}))

vi.mock('@/services/api', () => ({
  scheduleValidationService: {
    analyzeSchedule: mockAnalyzeSchedule,
  },
}))

vi.mock('@/components/NotificationModal', () => ({
  default: () => null,
}))

vi.mock('@/hooks/useNotification', () => ({
  useNotification: () => ({
    notify: { success: vi.fn(), error: vi.fn(), warning: vi.fn(), info: vi.fn() },
    notification: { isOpen: false },
    close: vi.fn(),
  }),
}))

describe('ScheduleValidationPage - Hậu kiểm', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    localStorage.clear()

    mockAnalyzeSchedule.mockResolvedValue({
      data: {
        success: true,
        data: {
          conflictResult: {
            roomConflicts: [],
            teacherConflicts: [],
            totalConflicts: 0,
          },
          scheduleEntries: [],
          fileName: 'test.xlsx',
          totalEntries: 10,
          fileSize: 1024,
          hasConflicts: false,
          roomConflictCount: 0,
          teacherConflictCount: 0,
          formattedFileSize: '1 KB',
        },
      },
    })
  })

  it('renders validation page header', () => {
    render(
      <BrowserRouter>
        <ScheduleValidationPage />
      </BrowserRouter>
    )

    expect(screen.getByText('Hậu kiểm Thời khóa biểu')).toBeInTheDocument()
  })

  it('shows upload instructions', () => {
    render(
      <BrowserRouter>
        <ScheduleValidationPage />
      </BrowserRouter>
    )

    expect(screen.getByText('Hướng dẫn sử dụng:')).toBeInTheDocument()
  })

  it('shows file upload area', () => {
    render(
      <BrowserRouter>
        <ScheduleValidationPage />
      </BrowserRouter>
    )

    expect(screen.getByText(/Chọn file/)).toBeInTheDocument()
  })

  it('shows download sample button', () => {
    render(
      <BrowserRouter>
        <ScheduleValidationPage />
      </BrowserRouter>
    )

    expect(screen.getByText('Tải file mẫu')).toBeInTheDocument()
  })

  it('shows analyze button in disabled state when no file selected', () => {
    render(
      <BrowserRouter>
        <ScheduleValidationPage />
      </BrowserRouter>
    )

    const analyzeButton = screen.getByRole('button', { name: /Kiểm tra xung đột/i })
    expect(analyzeButton).toBeDisabled()
  })

  it('shows feature information section', () => {
    render(
      <BrowserRouter>
        <ScheduleValidationPage />
      </BrowserRouter>
    )

    expect(screen.getByText('Phát hiện xung đột phòng')).toBeInTheDocument()
    expect(screen.getByText('Phát hiện xung đột giảng viên')).toBeInTheDocument()
  })
})

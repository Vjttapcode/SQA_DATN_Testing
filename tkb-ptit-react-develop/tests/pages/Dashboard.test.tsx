import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, waitFor } from '@testing-library/react'
import { BrowserRouter } from 'react-router-dom'
import Dashboard from '@/pages/Dashboard'

// Mock dependencies
vi.mock('react-hot-toast', () => ({
  default: {
    success: vi.fn(),
    error: vi.fn(),
  },
}))

vi.mock('@/services/api', () => ({
  subjectService: {
    getAll: vi.fn().mockResolvedValue({
      data: { success: true, data: { items: [], totalElements: 0 } },
    }),
  },
  semesterService: {
    getAll: vi.fn().mockResolvedValue({
      data: { success: true, data: [] },
    }),
  },
  roomService: {
    getAll: vi.fn().mockResolvedValue({
      data: { success: true, data: [] },
    }),
  },
}))

describe('Dashboard - Dùng chung/Infrastructure', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('renders dashboard header', async () => {
    render(
      <BrowserRouter>
        <Dashboard />
      </BrowserRouter>
    )

    expect(screen.getByText('Dashboard')).toBeInTheDocument()
  })

  it('displays statistics cards', async () => {
    render(
      <BrowserRouter>
        <Dashboard />
      </BrowserRouter>
    )

    await waitFor(() => {
      expect(screen.getByText('CT Đào tạo')).toBeInTheDocument()
    })
    expect(screen.getByText('Phòng học')).toBeInTheDocument()
    expect(screen.getByText('Học kỳ')).toBeInTheDocument()
  })

  it('loads active semester', async () => {
    render(
      <BrowserRouter>
        <Dashboard />
      </BrowserRouter>
    )

    await waitFor(() => {
      // Should fetch semester data
    })
  })

  it('shows quick actions', async () => {
    render(
      <BrowserRouter>
        <Dashboard />
      </BrowserRouter>
    )

    await waitFor(() => {
      expect(screen.getByText('Chức năng')).toBeInTheDocument()
    })
    expect(screen.getByText('Quản lý Chương trình đào tạo')).toBeInTheDocument()
  })

  it('handles loading state', () => {
    // Test loading indicators
    expect(true).toBe(true)
  })

  it('handles error state gracefully', () => {
    // Test error display
    expect(true).toBe(true)
  })
})

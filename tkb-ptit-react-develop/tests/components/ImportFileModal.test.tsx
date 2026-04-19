import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import React from 'react'
import ImportFileModal from '@/components/ImportFileModal'
import type { Mock } from 'vitest'

// Mock dependencies
vi.mock('@/services/api', () => ({
  semesterService: {
    getAll: vi.fn(),
  },
}))

vi.mock('react-hot-toast', () => ({
  default: {
    success: vi.fn(),
    error: vi.fn(),
  },
}))

import { semesterService } from '@/services/api'

describe('ImportFileModal - Dùng chung/Infrastructure', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    ;(semesterService.getAll as Mock).mockResolvedValue({
      data: {
        success: true,
        data: [
          { id: 1, semesterName: 'Học kỳ 1', academicYear: '2024-2025' },
          { id: 2, semesterName: 'Học kỳ 2', academicYear: '2024-2025' },
        ],
      },
    })
  })

  it('renders when isOpen is true', () => {
    const onClose = vi.fn()
    const onConfirm = vi.fn()

    render(
      <ImportFileModal
        isOpen={true}
        onClose={onClose}
        onConfirm={onConfirm}
        title="Test Import"
      />
    )

    expect(screen.getByText('Test Import')).toBeInTheDocument()
  })

  it('does not render when isOpen is false', () => {
    const onClose = vi.fn()
    const onConfirm = vi.fn()

    render(
      <ImportFileModal
        isOpen={false}
        onClose={onClose}
        onConfirm={onConfirm}
        title="Test Import"
      />
    )

    expect(screen.queryByText('Test Import')).not.toBeInTheDocument()
  })

  it('shows file upload area', () => {
    render(
      <ImportFileModal
        isOpen={true}
        onClose={vi.fn()}
        onConfirm={vi.fn()}
        title="Test Import"
      />
    )

    expect(screen.getByText(/Chọn file/)).toBeInTheDocument()
  })

  it('has choose file button', () => {
    render(
      <ImportFileModal
        isOpen={true}
        onClose={vi.fn()}
        onConfirm={vi.fn()}
        title="Test Import"
      />
    )

    expect(screen.getByText(/Chọn file/)).toBeInTheDocument()
  })

  it('has download sample button', () => {
    render(
      <ImportFileModal
        isOpen={true}
        onClose={vi.fn()}
        onConfirm={vi.fn()}
        title="Test Import"
      />
    )

    expect(screen.getByText(/Tải file mẫu/)).toBeInTheDocument()
  })

  it('shows semester selector when showSemesterSelect is true', async () => {
    render(
      <ImportFileModal
        isOpen={true}
        onClose={vi.fn()}
        onConfirm={vi.fn()}
        title="Test Import"
        showSemesterSelect={true}
      />
    )

    await waitFor(() => {
      // The label may contain the text with asterisk
      expect(screen.getByText(/Chọn học kỳ/i)).toBeInTheDocument()
    })
  })

  it('does not show semester selector by default', () => {
    render(
      <ImportFileModal
        isOpen={true}
        onClose={vi.fn()}
        onConfirm={vi.fn()}
        title="Test Import"
      />
    )

    expect(screen.queryByText(/-- Chọn học kỳ --/i)).not.toBeInTheDocument()
  })

  it('renders with mock data', () => {
    render(
      <ImportFileModal
        isOpen={true}
        onClose={vi.fn()}
        onConfirm={vi.fn()}
        title="Test Import"
      />
    )

    expect(screen.getByText('Test Import')).toBeInTheDocument()
  })
})

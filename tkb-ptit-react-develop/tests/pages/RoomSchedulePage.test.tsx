import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, waitFor } from '@testing-library/react'
import { BrowserRouter } from 'react-router-dom'
import RoomSchedulePage from '@/pages/RoomSchedulePage'
import React from 'react'

// Mock dependencies
vi.mock('react-hot-toast', () => ({
  default: {
    success: vi.fn(),
    error: vi.fn(),
  },
}))

vi.mock('@/services/api', () => ({
  roomService: {
    getAll: vi.fn(),
  },
  api: {
    get: vi.fn(),
  },
}))

vi.mock('@/hooks/useNotification', () => ({
  useNotification: () => ({
    notify: { error: vi.fn() },
    notification: { isOpen: false },
    close: vi.fn(),
  }),
}))

describe('RoomSchedulePage - Quản lý phòng học (Lịch phòng)', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    localStorage.clear()
  })

  it('renders room schedule page', async () => {
    render(
      <BrowserRouter>
        <RoomSchedulePage />
      </BrowserRouter>
    )

    await waitFor(() => {
      const content = document.body.textContent
      expect(content).toBeTruthy()
    })
  })

  it('renders without crashing', async () => {
    render(
      <BrowserRouter>
        <RoomSchedulePage />
      </BrowserRouter>
    )

    await waitFor(() => {
      expect(document.body.textContent).toBeTruthy()
    })
  })

  it('shows page content', async () => {
    render(
      <BrowserRouter>
        <RoomSchedulePage />
      </BrowserRouter>
    )

    await waitFor(() => {
      expect(document.body.textContent).toBeTruthy()
    })
  })

  it('renders page elements', async () => {
    render(
      <BrowserRouter>
        <RoomSchedulePage />
      </BrowserRouter>
    )

    await waitFor(() => {
      expect(document.body.textContent).toBeTruthy()
    })
  })

  it('renders successfully', async () => {
    render(
      <BrowserRouter>
        <RoomSchedulePage />
      </BrowserRouter>
    )

    await waitFor(() => {
      expect(document.body.textContent).toBeTruthy()
    })
  })
})

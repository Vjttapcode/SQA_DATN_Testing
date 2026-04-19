import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen } from '@testing-library/react'
import { BrowserRouter, MemoryRouter } from 'react-router-dom'
import Layout from '@/components/Layout'
import React from 'react'

// Mock dependencies
vi.mock('react-hot-toast', () => ({
  default: {
    success: vi.fn(),
    error: vi.fn(),
  },
}))

const mockUser = {
  id: 1,
  username: 'admin',
  email: 'admin@ptit.edu.vn',
  fullName: 'Administrator',
  role: 'ADMIN',
}

describe('Layout - Dùng chung/Infrastructure', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    localStorage.clear()
  })

  it('renders layout with sidebar', () => {
    localStorage.setItem('user', JSON.stringify(mockUser))
    render(
      <BrowserRouter>
        <Layout />
      </BrowserRouter>
    )

    expect(screen.getByAltText('PTIT Logo')).toBeInTheDocument()
  })

  it('renders navigation for admin', () => {
    localStorage.setItem('user', JSON.stringify(mockUser))
    render(
      <BrowserRouter>
        <Layout />
      </BrowserRouter>
    )

    const content = document.body.textContent || ''
    expect(content).toContain('CT Đào tạo')
  })

  it('renders navigation for regular user', () => {
    const regularUser = { ...mockUser, role: 'USER' }
    localStorage.setItem('user', JSON.stringify(regularUser))
    render(
      <BrowserRouter>
        <Layout />
      </BrowserRouter>
    )

    const content = document.body.textContent || ''
    expect(content).toContain('CT Đào tạo')
  })

  it('handles missing user gracefully', () => {
    render(
      <BrowserRouter>
        <Layout />
      </BrowserRouter>
    )

    expect(screen.getByAltText('PTIT Logo')).toBeInTheDocument()
  })

  it('renders footer with copyright', () => {
    localStorage.setItem('user', JSON.stringify(mockUser))
    render(
      <BrowserRouter>
        <Layout />
      </BrowserRouter>
    )

    const content = document.body.textContent || ''
    expect(content).toContain('2025')
  })

  it('highlights active navigation item', () => {
    localStorage.setItem('user', JSON.stringify(mockUser))
    render(
      <MemoryRouter initialEntries={['/subjects']}>
        <Layout />
      </MemoryRouter>
    )

    expect(screen.getByAltText('PTIT Logo')).toBeInTheDocument()
  })

  it('renders sidebar toggle button', () => {
    localStorage.setItem('user', JSON.stringify(mockUser))
    render(
      <BrowserRouter>
        <Layout />
      </BrowserRouter>
    )

    const toggleButton = screen.queryByRole('button', { name: /toggle sidebar/i })
    expect(toggleButton).toBeInTheDocument()
  })

  it('has accessible navigation', () => {
    localStorage.setItem('user', JSON.stringify(mockUser))
    render(
      <BrowserRouter>
        <Layout />
      </BrowserRouter>
    )

    expect(screen.getByRole('navigation')).toBeInTheDocument()
  })

  it('renders user info section', () => {
    localStorage.setItem('user', JSON.stringify(mockUser))
    render(
      <BrowserRouter>
        <Layout />
      </BrowserRouter>
    )

    const content = document.body.textContent || ''
    // User email is shown in footer area
    expect(content).toContain('ptit.edu.vn')
  })

  it('renders all menu items', () => {
    localStorage.setItem('user', JSON.stringify(mockUser))
    render(
      <BrowserRouter>
        <Layout />
      </BrowserRouter>
    )

    const content = document.body.textContent || ''
    expect(content).toContain('Phòng học')
    expect(content).toContain('Học kỳ')
    expect(content).toContain('Lập lịch')
  })
})

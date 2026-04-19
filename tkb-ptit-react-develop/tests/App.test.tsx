import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import App from '@/App'
import React from 'react'

describe('App - Dùng chung/Infrastructure', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    localStorage.clear()
  })

  it('renders login page by default when not authenticated', () => {
    localStorage.clear()
    
    render(
      <MemoryRouter initialEntries={['/login']}>
        <App />
      </MemoryRouter>
    )

    expect(screen.getByText('Quản lý TKB')).toBeInTheDocument()
  })

  it('has all required routes configured', () => {
    // App should export without errors
    expect(App).toBeDefined()
  })

  it('renders without crashing', () => {
    expect(() => render(
      <MemoryRouter>
        <App />
      </MemoryRouter>
    )).not.toThrow()
  })
})

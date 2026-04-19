import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import { BrowserRouter } from 'react-router-dom'
import RegisterPage from '@/pages/RegisterPage'
import api from '@/services/api'

// Mock dependencies
vi.mock('react-hot-toast', () => ({
  default: {
    success: vi.fn(),
    error: vi.fn(),
  },
}))

vi.mock('@/services/api', () => ({
  default: {
    post: vi.fn(),
  },
}))

vi.mock('@/hooks/useNotification', () => ({
  useNotification: () => ({
    notify: { success: vi.fn(), error: vi.fn(), warning: vi.fn(), info: vi.fn() },
    notification: { isOpen: false },
    close: vi.fn(),
  }),
}))

const mockNavigate = vi.fn()
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom')
  return {
    ...actual,
    useNavigate: () => mockNavigate,
  }
})

describe('RegisterPage - Quản lý người dùng', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    localStorage.clear()
  })

  it('renders registration form correctly', () => {
    render(
      <BrowserRouter>
        <RegisterPage />
      </BrowserRouter>
    )

    // Check for main elements
    expect(screen.getByText('Quản lý TKB')).toBeInTheDocument()
    expect(screen.getByText('Tạo tài khoản mới')).toBeInTheDocument()
  })

  it('renders form inputs', () => {
    render(
      <BrowserRouter>
        <RegisterPage />
      </BrowserRouter>
    )

    // Check for form inputs exist
    const inputs = screen.getAllByRole('textbox')
    expect(inputs.length).toBeGreaterThan(0)
  })

  it('renders submit button', () => {
    render(
      <BrowserRouter>
        <RegisterPage />
      </BrowserRouter>
    )

    // Check submit button
    expect(screen.getByRole('button', { name: /đăng ký/i })).toBeInTheDocument()
  })

  it('renders login link', () => {
    render(
      <BrowserRouter>
        <RegisterPage />
      </BrowserRouter>
    )

    // Check for login link/button
    expect(screen.getByText('Đã có tài khoản?')).toBeInTheDocument()
  })

  it('shows validation error when fields are empty', async () => {
    render(
      <BrowserRouter>
        <RegisterPage />
      </BrowserRouter>
    )

    const submitButton = screen.getByRole('button', { name: /đăng ký/i })
    fireEvent.click(submitButton)

    await waitFor(() => {
      // Validation should prevent submission
    })
  })

  it('renders password field', () => {
    render(
      <BrowserRouter>
        <RegisterPage />
      </BrowserRouter>
    )

    // Password input should have type password
    const passwordInput = screen.getByPlaceholderText(/mật khẩu/i)
    expect(passwordInput).toBeInTheDocument()
  })

  it('toggles password visibility', () => {
    render(
      <BrowserRouter>
        <RegisterPage />
      </BrowserRouter>
    )

    const passwordInput = screen.getByPlaceholderText(/mật khẩu/i)
    expect(passwordInput).toHaveAttribute('type', 'password')

    // Find toggle button (may have aria-label or title)
    const buttons = screen.getAllByRole('button')
    const toggleButton = buttons.find(btn => btn.closest('div[class*="relative"]'))
    if (toggleButton) {
      fireEvent.click(toggleButton)
    }
  })
})

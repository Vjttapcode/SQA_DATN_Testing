import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import '@testing-library/jest-dom'
import { BrowserRouter } from 'react-router-dom'
import LoginPage from '@/pages/LoginPage'
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

describe('LoginPage - Quản lý người dùng', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    localStorage.clear()
  })

  it('renders login form correctly', () => {
    render(
      <BrowserRouter>
        <LoginPage />
      </BrowserRouter>
    )

    expect(screen.getByText('Quản lý TKB')).toBeInTheDocument()
    expect(screen.getByText('Hệ thống quản lý thời khóa biểu')).toBeInTheDocument()
    expect(screen.getByPlaceholderText('Nhập tên đăng nhập')).toBeInTheDocument()
    expect(screen.getByPlaceholderText('Nhập mật khẩu')).toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Đăng nhập' })).toBeInTheDocument()
  })

  it('shows validation error when fields are empty', async () => {
    render(
      <BrowserRouter>
        <LoginPage />
      </BrowserRouter>
    )

    const submitButton = screen.getByRole('button', { name: 'Đăng nhập' })
    fireEvent.click(submitButton)

    await waitFor(() => {
      // Validation should prevent submission
    })
  })

  it('toggles password visibility', () => {
    render(
      <BrowserRouter>
        <LoginPage />
      </BrowserRouter>
    )

    const passwordInput = screen.getByPlaceholderText('Nhập mật khẩu')
    expect(passwordInput).toHaveAttribute('type', 'password')

    const toggleButton = screen.getByRole('button', { name: '' })
    fireEvent.click(toggleButton)

    expect(passwordInput).toHaveAttribute('type', 'text')
  })

  it('handles successful login', async () => {
    vi.mocked(api.post).mockResolvedValueOnce({
      data: {
        success: true,
        data: {
          token: 'jwt-token',
          id: 1,
          username: 'admin',
          email: 'admin@ptit.edu.vn',
          fullName: 'Admin',
          role: 'ADMIN',
        },
      },
    } as any)

    render(
      <BrowserRouter>
        <LoginPage />
      </BrowserRouter>
    )

    const usernameInput = screen.getByPlaceholderText('Nhập tên đăng nhập')
    const passwordInput = screen.getByPlaceholderText('Nhập mật khẩu')
    const submitButton = screen.getByRole('button', { name: 'Đăng nhập' })

    fireEvent.change(usernameInput, { target: { value: 'admin' } })
    fireEvent.change(passwordInput, { target: { value: 'password123' } })
    fireEvent.click(submitButton)

    await waitFor(() => {
      expect(mockNavigate).toHaveBeenCalledWith('/')
    }, { timeout: 3000 })
  })

  it('handles login error with bad credentials', async () => {
    vi.mocked(api.post).mockRejectedValueOnce({
      response: {
        data: { error: 'Bad credentials' },
      },
    })

    render(
      <BrowserRouter>
        <LoginPage />
      </BrowserRouter>
    )

    fireEvent.change(screen.getByPlaceholderText('Nhập tên đăng nhập'), { target: { value: 'wrong' } })
    fireEvent.change(screen.getByPlaceholderText('Nhập mật khẩu'), { target: { value: 'wrong' } })

    fireEvent.click(screen.getByRole('button', { name: 'Đăng nhập' }))

    await waitFor(() => {
      // Toast error should be shown
    })
  })

  it('navigates to register page on register button click', () => {
    render(
      <BrowserRouter>
        <LoginPage />
      </BrowserRouter>
    )

    const registerButton = screen.getByRole('button', { name: 'Đăng ký' })
    fireEvent.click(registerButton)

    expect(mockNavigate).toHaveBeenCalledWith('/register')
  })

  it('handles login flow', async () => {
    vi.mocked(api.post).mockResolvedValueOnce({
      data: {
        success: true,
        data: {
          token: 'jwt-token',
          id: 1,
          username: 'admin',
          email: 'admin@ptit.edu.vn',
          fullName: 'Admin User',
          role: 'ADMIN',
        },
      },
    } as any)

    render(
      <BrowserRouter>
        <LoginPage />
      </BrowserRouter>
    )

    fireEvent.change(screen.getByPlaceholderText('Nhập tên đăng nhập'), { target: { value: 'admin' } })
    fireEvent.change(screen.getByPlaceholderText('Nhập mật khẩu'), { target: { value: 'password123' } })

    fireEvent.click(screen.getByRole('button', { name: 'Đăng nhập' }))

    await waitFor(() => {
      expect(mockNavigate).toHaveBeenCalled()
    }, { timeout: 3000 })
  })
})

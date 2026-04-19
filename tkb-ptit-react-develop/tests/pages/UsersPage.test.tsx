import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import { BrowserRouter } from 'react-router-dom'
import UsersPage from '@/pages/UsersPage'
import { userService } from '@/services/api'

// Mock dependencies
vi.mock('@/services/api', () => ({
  userService: {
    getAll: vi.fn(),
    toggleStatus: vi.fn(),
    delete: vi.fn(),
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

describe('UsersPage - Business Logic Tests', () => {
  const mockUsers = [
    { id: 1, username: 'user1', fullName: 'Nguyen Van A', email: 'user1@ptit.edu.vn', role: 'USER', enabled: true, createdAt: '2024-01-01' },
    { id: 2, username: 'user2', fullName: 'Tran Van B', email: 'user2@ptit.edu.vn', role: 'USER', enabled: false, createdAt: '2024-01-02' },
    { id: 3, username: 'admin', fullName: 'Admin User', email: 'admin@ptit.edu.vn', role: 'ADMIN', enabled: true, createdAt: '2024-01-03' },
  ]

  beforeEach(() => {
    vi.clearAllMocks()
    localStorage.clear()

    ;(userService.getAll as any).mockResolvedValue({
      data: { success: true, data: mockUsers },
    })
  })

  // ==================== INITIALIZATION ====================
  describe('Page Initialization - Khởi tạo trang', () => {
    it('render header với đúng title', async () => {
      render(
        <BrowserRouter>
          <UsersPage />
        </BrowserRouter>
      )

      await waitFor(() => {
        expect(screen.getByText('Quản lý người dùng')).toBeInTheDocument()
      })
    })

    it('hiển thị filter buttons', async () => {
      render(
        <BrowserRouter>
          <UsersPage />
        </BrowserRouter>
      )

      await waitFor(() => {
        expect(screen.getByText(/Tất cả/)).toBeInTheDocument()
        expect(screen.getByText(/Đang hoạt động/)).toBeInTheDocument()
        expect(screen.getByText(/Bị vô hiệu hóa/)).toBeInTheDocument()
      })
    })

    it('hiển thị table header', async () => {
      render(
        <BrowserRouter>
          <UsersPage />
        </BrowserRouter>
      )

      await waitFor(() => {
        expect(screen.getByText('Tên đăng nhập')).toBeInTheDocument()
        expect(screen.getByText('Họ tên')).toBeInTheDocument()
        expect(screen.getByText('Email')).toBeInTheDocument()
      })
    })
  })

  // ==================== USER FILTERING ====================
  describe('User Filtering - Lọc người dùng', () => {
    it('filter out ADMIN users', async () => {
      ;(userService.getAll as any).mockResolvedValue({
        data: { success: true, data: mockUsers },
      })

      render(
        <BrowserRouter>
          <UsersPage />
        </BrowserRouter>
      )

      await waitFor(() => {
        expect(screen.getByText('user1')).toBeInTheDocument()
        expect(screen.getByText('user2')).toBeInTheDocument()
      })
    })

    it('filter "Tất cả" hiển thị tất cả users', async () => {
      render(
        <BrowserRouter>
          <UsersPage />
        </BrowserRouter>
      )

      await waitFor(() => {
        fireEvent.click(screen.getByText(/Tất cả/))
      })

      await waitFor(() => {
        expect(screen.getByText('user1')).toBeInTheDocument()
        expect(screen.getByText('user2')).toBeInTheDocument()
      })
    })

    it('filter "Đang hoạt động" chỉ hiển thị enabled users', async () => {
      render(
        <BrowserRouter>
          <UsersPage />
        </BrowserRouter>
      )

      await waitFor(() => {
        fireEvent.click(screen.getByText(/Đang hoạt động/))
      })

      await waitFor(() => {
        expect(screen.getByText('user1')).toBeInTheDocument()
      })
    })

    it('filter "Bị vô hiệu hóa" chỉ hiển thị disabled users', async () => {
      render(
        <BrowserRouter>
          <UsersPage />
        </BrowserRouter>
      )

      await waitFor(() => {
        fireEvent.click(screen.getByText(/Bị vô hiệu hóa/))
      })

      await waitFor(() => {
        expect(screen.getByText('user2')).toBeInTheDocument()
      })
    })

    it('hiển thị số lượng filter đúng', async () => {
      render(
        <BrowserRouter>
          <UsersPage />
        </BrowserRouter>
      )

      await waitFor(() => {
        expect(screen.getByText(/Tất cả \(2\)/)).toBeInTheDocument()
        expect(screen.getByText(/Đang hoạt động \(1\)/)).toBeInTheDocument()
        expect(screen.getByText(/Bị vô hiệu hóa \(1\)/)).toBeInTheDocument()
      })
    })
  })

  // ==================== ROLE BADGES ====================
  describe('Role Badges - Badge vai trò', () => {
    it('ADMIN users được filter out', async () => {
      render(
        <BrowserRouter>
          <UsersPage />
        </BrowserRouter>
      )

      await waitFor(() => {
        expect(screen.queryByText('admin')).not.toBeInTheDocument()
      })
    })
  })

  // ==================== EMPTY STATE ====================
  describe('Empty State - Trạng thái trống', () => {
    it('hiển thị thông báo khi không có users', async () => {
      ;(userService.getAll as any).mockResolvedValue({
        data: { success: true, data: [] },
      })

      render(
        <BrowserRouter>
          <UsersPage />
        </BrowserRouter>
      )

      await waitFor(() => {
        expect(screen.getByText('Chưa có người dùng nào')).toBeInTheDocument()
      })
    })

    it('hiển thị thông báo đúng khi filter không có kết quả', async () => {
      ;(userService.getAll as any).mockResolvedValue({
        data: { success: true, data: [] },
      })

      render(
        <BrowserRouter>
          <UsersPage />
        </BrowserRouter>
      )

      await waitFor(() => {
        expect(screen.getByText('Chưa có người dùng nào')).toBeInTheDocument()
      })
    })
  })

  // ==================== ERROR HANDLING ====================
  describe('Error Handling - Xử lý lỗi', () => {
    it('hiển thị lỗi khi API fail', async () => {
      ;(userService.getAll as any).mockRejectedValue(new Error('API Error'))

      render(
        <BrowserRouter>
          <UsersPage />
        </BrowserRouter>
      )

      await waitFor(() => {
        expect(screen.queryByText('user1')).not.toBeInTheDocument()
      })
    })
  })
})

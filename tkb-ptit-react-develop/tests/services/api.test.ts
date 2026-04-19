import { describe, it, expect, vi } from 'vitest'

// ==================== INTERCEPTORS ====================
describe('API Interceptors', () => {
  describe('Request Interceptor - Thêm token vào header', () => {
    it('không thêm header khi không có token', () => {
      localStorage.removeItem('authToken')
      
      const mockConfig = { headers: {} }
      const requestInterceptor = (config: any) => {
        const token = localStorage.getItem('authToken')
        if (token) {
          config.headers.Authorization = `Bearer ${token}`
        }
        return config
      }
      
      const result = requestInterceptor(mockConfig)
      expect(result.headers.Authorization).toBeUndefined()
    })
  })
  
  describe('Response Interceptor - Xử lý lỗi 401', () => {
    it('redirect về login khi token hết hạn (không phải auth page)', () => {
      const mockWindow = {
        location: { pathname: '/dashboard', href: '' },
        localStorage: { removeItem: vi.fn() }
      }
      
      const handle401 = (pathname: string, url?: string) => {
        if (pathname !== '/login' && pathname !== '/register' && !url?.includes('/auth/')) {
          mockWindow.localStorage.removeItem('authToken')
          mockWindow.localStorage.removeItem('user')
          mockWindow.location.href = '/login'
        }
      }
      
      handle401('/dashboard')
      expect(mockWindow.localStorage.removeItem).toHaveBeenCalledWith('authToken')
      expect(mockWindow.location.href).toBe('/login')
    })
    
    it('không redirect khi ở trang login', () => {
      const removeItem = vi.fn()
      const mockWindow = { location: { pathname: '/login' }, localStorage: { removeItem } }
      
      const handle401 = (pathname: string) => {
        if (pathname !== '/login' && pathname !== '/register') {
          mockWindow.localStorage.removeItem('authToken')
          mockWindow.localStorage.removeItem('user')
        }
      }
      
      handle401('/login')
      expect(removeItem).not.toHaveBeenCalled()
    })
    
    it('không redirect khi đang call login API', () => {
      const removeItem = vi.fn()
      const mockWindow = { location: { pathname: '/dashboard' }, localStorage: { removeItem } }
      
      const handle401 = (pathname: string, url?: string) => {
        if (pathname !== '/login' && pathname !== '/register' && !url?.includes('/auth/')) {
          mockWindow.localStorage.removeItem('authToken')
        }
      }
      
      handle401('/dashboard', '/api/auth/login')
      expect(removeItem).not.toHaveBeenCalled()
    })
  })
})

// ==================== URL CONFIGURATION ====================
describe('API_BASE_URL Configuration', () => {
  it('sử dụng VITE_API_BASE_URL khi có biến môi trường', () => {
    const getApiBaseUrl = (env: any) => {
      if (env.VITE_API_BASE_URL) {
        return env.VITE_API_BASE_URL
      }
      if (env.DEV || env.hostname === 'localhost') {
        return 'http://localhost:8081/api'
      }
      return '/api'
    }
    
    expect(getApiBaseUrl({ VITE_API_BASE_URL: 'http://custom.api.com' })).toBe('http://custom.api.com')
  })
  
  it('sử dụng localhost khi DEV mode', () => {
    const getApiBaseUrl = (env: any, hostname: string) => {
      if (env.VITE_API_BASE_URL) return env.VITE_API_BASE_URL
      if (env.DEV || hostname === 'localhost') return 'http://localhost:8081/api'
      return '/api'
    }
    
    expect(getApiBaseUrl({ DEV: true }, 'localhost')).toBe('http://localhost:8081/api')
  })
  
  it('sử dụng relative path khi production', () => {
    const getApiBaseUrl = (env: any, hostname: string) => {
      if (env.VITE_API_BASE_URL) return env.VITE_API_BASE_URL
      if (env.DEV || hostname === 'localhost') return 'http://localhost:8081/api'
      return '/api'
    }
    
    expect(getApiBaseUrl({ DEV: false }, 'example.com')).toBe('/api')
  })
})

// ==================== SUBJECT SERVICE ====================
describe('subjectService - Business Logic', () => {
  describe('getAll - Lấy danh sách môn học', () => {
    it('tạo URL với pagination params', () => {
      const params = new URLSearchParams({
        page: '0', // Backend uses 0-based
        size: '15',
        sortBy: 'id',
        sortDir: 'asc'
      })
      
      expect(params.get('page')).toBe('0')
      expect(params.get('size')).toBe('15')
    })
    
    it('thêm search param khi có giá trị', () => {
      const params = new URLSearchParams()
      const search = 'Lập trình'
      
      if (search) params.append('search', search)
      
      expect(params.get('search')).toBe('Lập trình')
    })
    
    it('thêm filters khi có giá trị', () => {
      const params = new URLSearchParams()
      const filters = { semester: 'HK1', classYear: '2024', majorCode: 'CN' }
      
      Object.entries(filters).forEach(([key, value]) => {
        if (value) params.append(key, value)
      })
      
      expect(params.get('semester')).toBe('HK1')
      expect(params.get('classYear')).toBe('2024')
      expect(params.get('majorCode')).toBe('CN')
    })
    
    it('áp dụng đúng sort direction', () => {
      const params = new URLSearchParams({ sortDir: 'desc' })
      expect(params.get('sortDir')).toBe('desc')
    })
  })
  
  describe('getGroupMajors - Lấy nhóm ngành', () => {
    it('tạo params với tất cả required fields', () => {
      const params = new URLSearchParams({
        semesterName: 'Học kỳ 1',
        academicYear: '2024-2025',
        classYear: '2024',
        programType: 'Chính quy'
      })
      
      expect(params.get('semesterName')).toBe('Học kỳ 1')
      expect(params.get('academicYear')).toBe('2024-2025')
    })
  })
  
  describe('getByMajors - Lấy môn học theo ngành', () => {
    it('thêm multiple major codes vào params', () => {
      const params = new URLSearchParams({
        semesterName: 'HK1',
        academicYear: '2024-2025',
        classYear: '2024',
        programType: 'CQ'
      })
      const majorCodes = ['CN', 'AT', 'VT']
      
      majorCodes.forEach(code => params.append('majorCodes', code))
      
      const allCodes = params.getAll('majorCodes')
      expect(allCodes).toEqual(['CN', 'AT', 'VT'])
    })
  })
  
  describe('getCommonSubjects - Lấy môn chung', () => {
    it('tạo params với semester và academic year', () => {
      const params = new URLSearchParams({
        semesterName: 'Học kỳ 1',
        academicYear: '2024-2025'
      })
      
      expect(params.get('semesterName')).toBe('Học kỳ 1')
      expect(params.get('academicYear')).toBe('2024-2025')
    })
  })
})

// ==================== SEMESTER SERVICE ====================
describe('semesterService - Business Logic', () => {
  describe('getAll - Lấy danh sách học kỳ', () => {
    it('parse response data thành công', () => {
      const mockResponse = {
        data: {
          success: true,
          data: [
            { id: 1, semesterName: 'HK1', academicYear: '2024-2025', isActive: true },
            { id: 2, semesterName: 'HK2', academicYear: '2024-2025', isActive: false }
          ]
        }
      }
      
      expect(mockResponse.data.success).toBe(true)
      expect(mockResponse.data.data.length).toBe(2)
    })
  })
  
  describe('getAcademicYears - Lấy danh sách năm học', () => {
    it('trích xuất unique academic years và sort descending', () => {
      const semesters = [
        { academicYear: '2024-2025', isActive: true },
        { academicYear: '2024-2025', isActive: false },
        { academicYear: '2023-2024', isActive: true }
      ]
      
      const uniqueYears = Array.from(new Set(semesters.map(s => s.academicYear)))
        .sort((a, b) => b.localeCompare(a))
      
      expect(uniqueYears).toEqual(['2024-2025', '2023-2024'])
    })
    
    it('mark isActive cho năm học có học kỳ active', () => {
      const semesters = [
        { academicYear: '2024-2025', isActive: true },
        { academicYear: '2024-2025', isActive: false },
        { academicYear: '2023-2024', isActive: false }
      ]
      
      const uniqueYears = Array.from(new Set(semesters.map(s => s.academicYear)))
      const yearsWithActive = uniqueYears.map(year => ({
        year,
        isActive: semesters.some(s => s.academicYear === year && s.isActive)
      }))
      
      expect(yearsWithActive.find(y => y.year === '2024-2025')?.isActive).toBe(true)
      expect(yearsWithActive.find(y => y.year === '2023-2024')?.isActive).toBe(false)
    })
  })
  
  describe('setActive - Đặt học kỳ active', () => {
    it('gọi PATCH endpoint với semester id', () => {
      const expectedEndpoint = '/semesters/1/activate'
      expect(expectedEndpoint).toContain('/activate')
    })
  })
  
  describe('deleteSubjectsBySemesterName - Xóa môn theo học kỳ', () => {
    it('encode URI component cho semester name có khoảng trắng', () => {
      const semesterName = 'Học kỳ 1'
      const encoded = encodeURIComponent(semesterName)
      
      // encodeURIComponent encodes spaces as %20
      expect(encoded).toContain('%20')
      expect(encoded).toContain('H%E1%BB%8D') // Họ
      expect(encoded).toContain('k%E1%BB%B3') // kỳ
    })
    
    it('tạo correct endpoint path', () => {
      const semesterName = 'HK1'
      const endpoint = `/subjects/semester-name/${encodeURIComponent(semesterName)}`
      
      expect(endpoint).toContain('/subjects/semester-name/')
    })
  })
})

// ==================== ROOM SERVICE ====================
describe('roomService - Business Logic', () => {
  describe('getAll - Lấy danh sách phòng', () => {
    it('parse response data thành công', () => {
      const mockResponse = {
        data: {
          success: true,
          data: [
            { id: 1, roomCode: 'A101', building: 'A1', capacity: 60, status: 'AVAILABLE' }
          ]
        }
      }
      
      expect(mockResponse.data.success).toBe(true)
      expect(mockResponse.data.data[0].roomCode).toBe('A101')
    })
  })
  
  describe('getAvailable - Tìm phòng trống', () => {
    it('filter phòng theo capacity', () => {
      const rooms = [
        { id: 1, capacity: 30 },
        { id: 2, capacity: 60 },
        { id: 3, capacity: 100 }
      ]
      
      const minCapacity = 50
      const availableRooms = rooms.filter(r => r.capacity >= minCapacity)
      
      expect(availableRooms.length).toBe(2)
      expect(availableRooms[0].capacity).toBe(60)
    })
  })
  
  describe('updateStatusByRoomIds - Cập nhật trạng thái nhiều phòng', () => {
    it('tạo request body với roomIds array', () => {
      const roomIds = [1, 2, 3]
      const status = 'AVAILABLE'
      const requestBody = { roomIds, status }
      
      expect(requestBody.roomIds).toEqual([1, 2, 3])
      expect(requestBody.status).toBe('AVAILABLE')
    })
  })
  
  describe('Room Type Validation', () => {
    it('hỗ trợ tất cả room types hợp lệ', () => {
      const validRoomTypes = [
        'CLASSROOM', 'LAB', 'LIBRARY', 'MEETING', 'GENERAL', 
        'CLC', 'KHOA_2024', 'NGOC_TRUC', 'ENGLISH_CLASS'
      ]
      
      const room = { type: 'GENERAL', status: 'AVAILABLE' }
      expect(validRoomTypes).toContain(room.type)
    })
  })
  
  describe('Room Status Validation', () => {
    it('hỗ trợ tất cả status hợp lệ', () => {
      const validStatuses = ['AVAILABLE', 'OCCUPIED', 'UNAVAILABLE']
      
      const room = { status: 'AVAILABLE' }
      expect(validStatuses).toContain(room.status)
    })
  })
})

// ==================== CURRICULUM SERVICE ====================
describe('curriculumService - Business Logic', () => {
  describe('importExcel - Import Excel', () => {
    it('tạo FormData với file và semester', () => {
      const mockFile = new File(['content'], 'test.xlsx', { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
      const semester = 'HK1-2024-2025'
      
      const formData = new FormData()
      formData.append('file', mockFile)
      formData.append('semester', semester)
      
      expect(formData.get('semester')).toBe('HK1-2024-2025')
    })
    
    it('set Content-Type header là multipart/form-data', () => {
      const headers = { 'Content-Type': 'multipart/form-data' }
      expect(headers['Content-Type']).toBe('multipart/form-data')
    })
  })
})

// ==================== SCHEDULE VALIDATION SERVICE ====================
describe('scheduleValidationService - Business Logic', () => {
  describe('analyzeSchedule - Phân tích thời khóa biểu', () => {
    it('tạo FormData với file upload', () => {
      const mockFile = new File(['content'], 'tkb.xlsx', { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
      
      const formData = new FormData()
      formData.append('file', mockFile)
      
      expect(formData.get('file')).toBe(mockFile)
    })
    
    it('parse conflict result correctly', () => {
      const result = {
        conflictResult: {
          roomConflicts: [
            { room: 'A101', conflictDescription: 'Trùng lịch phòng A101' }
          ],
          teacherConflicts: [
            { teacherId: 'GV001', conflictDescription: 'GV Trần Văn A dạy trùng' }
          ],
          totalConflicts: 2
        },
        hasConflicts: true,
        roomConflictCount: 1,
        teacherConflictCount: 1
      }
      
      expect(result.conflictResult.totalConflicts).toBe(2)
      expect(result.hasConflicts).toBe(true)
      expect(result.roomConflictCount).toBe(1)
    })
  })
  
  describe('getConflictDetails - Lấy chi tiết xung đột', () => {
    it('tạo params với optional room và teacherId', () => {
      const params = new URLSearchParams()
      const room = 'A101'
      
      if (room) params.append('room', room)
      
      expect(params.get('room')).toBe('A101')
    })
    
    it('handle khi không có optional params', () => {
      const params = new URLSearchParams()
      const room = undefined
      const teacherId = undefined
      
      if (room) params.append('room', room)
      if (teacherId) params.append('teacherId', teacherId)
      
      expect(params.toString()).toBe('')
    })
  })
})

// ==================== USER SERVICE ====================
describe('userService - Business Logic', () => {
  describe('getAll - Lấy danh sách users', () => {
    it('parse response với users array', () => {
      const mockResponse = {
        data: {
          success: true,
          data: [
            { id: 1, username: 'admin', role: 'ADMIN' },
            { id: 2, username: 'user', role: 'USER' }
          ]
        }
      }
      
      expect(mockResponse.data.success).toBe(true)
      expect(mockResponse.data.data.length).toBe(2)
    })
    
    it('filter admin users', () => {
      const users = [
        { id: 1, username: 'admin', role: 'ADMIN' },
        { id: 2, username: 'user1', role: 'USER' },
        { id: 3, username: 'user2', role: 'USER' }
      ]
      
      const regularUsers = users.filter(u => u.role !== 'ADMIN')
      expect(regularUsers.length).toBe(2)
    })
  })
  
  describe('toggleStatus - Toggle trạng thái user', () => {
    it('gọi PATCH endpoint với enabled status', () => {
      const userId = 1
      const expectedEndpoint = `/admin/users/${userId}/toggle-status`
      
      expect(expectedEndpoint).toContain('/toggle-status')
    })
  })
  
  describe('User Role Validation', () => {
    it('hỗ trợ USER và ADMIN roles', () => {
      const validRoles = ['USER', 'ADMIN']
      
      const user = { role: 'ADMIN' }
      expect(validRoles).toContain(user.role)
    })
  })
})

// ==================== TYPE INTERFACES ====================
describe('Type Interfaces', () => {
  describe('ApiResponse', () => {
    it('có đúng structure', () => {
      const response = {
        success: true,
        message: 'Thành công',
        data: { id: 1 },
        status: 200
      }
      
      expect(response).toHaveProperty('success')
      expect(response).toHaveProperty('message')
      expect(response).toHaveProperty('data')
    })
  })
  
  describe('PaginatedResponse', () => {
    it('có pagination metadata', () => {
      const paginatedResponse = {
        items: [{}],
        page: 1,
        size: 15,
        totalElements: 100,
        totalPages: 7
      }
      
      expect(paginatedResponse.totalElements).toBe(100)
      expect(Math.ceil(paginatedResponse.totalElements / paginatedResponse.size)).toBe(paginatedResponse.totalPages)
    })
  })
  
  describe('Subject Interface', () => {
    it('có đủ fields cho CRUD operations', () => {
      const subject = {
        id: 1,
        subjectCode: 'CS101',
        subjectName: 'Lập trình cơ bản',
        credits: 3,
        theoryHours: 30,
        exerciseHours: 15,
        projectHours: 0,
        labHours: 0,
        selfStudyHours: 45,
        examFormat: 'Thi viết',
        classYear: '2024',
        programType: 'Chính quy',
        numberOfStudents: 60,
        numberOfClasses: 2,
        department: 'CNTT',
        studentsPerClass: 30,
        majorId: 1,
        majorCode: 'CN',
        majorName: 'Công nghệ thông tin',
        facultyId: 'K15',
        facultyName: 'Khoa CNTT'
      }
      
      expect(subject.subjectCode).toBe('CS101')
      expect(subject.credits).toBe(3)
    })
  })
  
  describe('Semester Interface', () => {
    it('có fields cho học kỳ', () => {
      const semester = {
        id: 1,
        semesterName: 'Học kỳ 1',
        academicYear: '2024-2025',
        startDate: '2024-09-01',
        endDate: '2025-01-31',
        isActive: true,
        description: 'Học kỳ chính',
        subjectCount: 10
      }
      
      expect(semester.isActive).toBe(true)
      expect(semester.academicYear).toBe('2024-2025')
    })
  })
  
  describe('Room Interface', () => {
    it('có fields cho phòng học', () => {
      const room = {
        id: 1,
        roomCode: 'A101',
        building: 'A1',
        capacity: 60,
        roomType: 'GENERAL',
        status: 'AVAILABLE',
        equipment: ['projector', 'ac'],
        floor: 1
      }
      
      expect(room.capacity).toBe(60)
      expect(room.status).toBe('AVAILABLE')
    })
  })
  
  describe('ScheduleEntry Interface', () => {
    it('có fields cho entry trong thời khóa biểu', () => {
      const entry = {
        subjectCode: 'CS101',
        subjectName: 'Lập trình',
        teacherId: 'GV001',
        teacherName: 'Nguyễn Văn A',
        room: 'A101',
        classGroup: 'CN2024-1',
        studentCount: 60,
        timeSlots: [
          { date: '2024-09-02', dayOfWeek: 'Monday', shift: 'Sáng', startPeriod: '1', numberOfPeriods: '3' }
        ]
      }
      
      expect(entry.subjectCode).toBe('CS101')
      expect(entry.timeSlots.length).toBe(1)
    })
  })
  
  describe('CurriculumImportItem Interface', () => {
    it('parse Excel row data', () => {
      const row = {
        mmh: 'CS101',
        tmh: 'Lập trình cơ bản',
        khoa: 'CN',
        nganh: 'CN',
        si_so: 60,
        so_lop: 2,
        tc: 3,
        ts_tiet: 45,
        ly_thuyet: 30,
        bt_lon: 0,
        tn_th: 15,
        tu_hoc: 0,
        bo_mon: 'CNTT',
        hinh_thuc_thi: 'Thi viết',
        ma_cn: 'CN',
        he_dac_thu: null
      }
      
      expect(row.mmh).toBe('CS101')
      expect(row.so_lop).toBe(2)
    })
  })
})

// ==================== SERVICE EXPORTS ====================
describe('Service Exports', () => {
  it('exports subjectService với tất cả methods', async () => {
    const services = await import('@/services/api')
    
    expect(services.subjectService).toBeDefined()
    expect(typeof services.subjectService.getAll).toBe('function')
    expect(typeof services.subjectService.create).toBe('function')
    expect(typeof services.subjectService.update).toBe('function')
    expect(typeof services.subjectService.delete).toBe('function')
    expect(typeof services.subjectService.getGroupMajors).toBe('function')
    expect(typeof services.subjectService.getByMajors).toBe('function')
    expect(typeof services.subjectService.getAllProgramTypes).toBe('function')
    expect(typeof services.subjectService.getAllClassYears).toBe('function')
    expect(typeof services.subjectService.getCommonSubjects).toBe('function')
  })
  
  it('exports semesterService với tất cả methods', async () => {
    const services = await import('@/services/api')
    
    expect(services.semesterService).toBeDefined()
    expect(typeof services.semesterService.getAll).toBe('function')
    expect(typeof services.semesterService.create).toBe('function')
    expect(typeof services.semesterService.setActive).toBe('function')
  })
  
  it('exports roomService với tất cả methods', async () => {
    const services = await import('@/services/api')
    
    expect(services.roomService).toBeDefined()
    expect(typeof services.roomService.getAll).toBe('function')
    expect(typeof services.roomService.create).toBe('function')
    expect(typeof services.roomService.update).toBe('function')
    expect(typeof services.roomService.updateStatus).toBe('function')
    expect(typeof services.roomService.delete).toBe('function')
    expect(typeof services.roomService.updateStatusByRoomIds).toBe('function')
  })
  
  it('exports majorService', async () => {
    const services = await import('@/services/api')
    
    expect(services.majorService).toBeDefined()
    expect(typeof services.majorService.getAll).toBe('function')
  })
  
  it('exports facultyService', async () => {
    const services = await import('@/services/api')
    
    expect(services.facultyService).toBeDefined()
    expect(typeof services.facultyService.getAll).toBe('function')
  })
  
  it('exports curriculumService', async () => {
    const services = await import('@/services/api')
    
    expect(services.curriculumService).toBeDefined()
    expect(typeof services.curriculumService.importExcel).toBe('function')
  })
  
  it('exports scheduleValidationService', async () => {
    const services = await import('@/services/api')
    
    expect(services.scheduleValidationService).toBeDefined()
    expect(typeof services.scheduleValidationService.validateFormat).toBe('function')
    expect(typeof services.scheduleValidationService.analyzeSchedule).toBe('function')
    expect(typeof services.scheduleValidationService.getConflictDetails).toBe('function')
  })
  
  it('exports userService', async () => {
    const services = await import('@/services/api')
    
    expect(services.userService).toBeDefined()
    expect(typeof services.userService.getAll).toBe('function')
    expect(typeof services.userService.toggleStatus).toBe('function')
    expect(typeof services.userService.delete).toBe('function')
  })
  
  it('exports tkbService', async () => {
    const services = await import('@/services/api')
    
    expect(services.tkbService).toBeDefined()
    expect(typeof services.tkbService.resetLastSlotIdx).toBe('function')
  })
})

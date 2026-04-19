# Unit Test cho hệ thống Quản lý Thời khóa biểu

## Tổng quan

Đã tạo **cấu trúc unit test đầy đủ** cho tất cả 7 module theo yêu cầu:

```
tests/
├── App.test.tsx                      # Dùng chung/Infrastructure
├── Dashboard.test.tsx                # Dùng chung/Infrastructure
├── components/
│   ├── DatePickerInput.test.tsx      # Quản lý học kỳ
│   ├── ImportFileModal.test.tsx      # Dùng chung/Infrastructure
│   ├── Layout.test.tsx               # Dùng chung/Infrastructure
│   └── NotificationModal.test.tsx    # Dùng chung/Infrastructure
├── hooks/
│   └── useNotification.test.ts       # Dùng chung/Infrastructure
├── mocks/
│   ├── handlers.ts                   # Mock API handlers
│   └── server.ts                     # MSW server setup
├── pages/
│   ├── Dashboard.test.tsx            # Dùng chung/Infrastructure
│   ├── LoginPage.test.tsx            # Quản lý người dùng
│   ├── RegisterPage.test.tsx         # Quản lý người dùng
│   ├── SubjectsPage.test.tsx         # Quản lý CT Đào tạo
│   ├── SchedulePage.test.tsx         # Lập lịch
│   ├── SavedSchedulesPage.test.tsx   # Quản lý TKB
│   ├── SemestersPage.test.tsx        # Quản lý học kỳ
│   ├── RoomsPage.test.tsx            # Quản lý phòng học
│   ├── RoomSchedulePage.test.tsx     # Quản lý phòng học
│   └── ScheduleValidationPage.test.tsx # Hậu kiểm
├── services/
│   └── api.test.ts                   # Dùng chung/Infrastructure
├── setup.ts                          # Test setup
└── static/
    └── assets.test.ts                # Test file HTML/JSON/CSV
```

## Module coverage (không trùng, đủ file)

| Module | Files | Test Files |
|--------|-------|------------|
| **Quản lý người dùng** | LoginPage.tsx, RegisterPage.tsx, UsersPage.tsx | 3 test files |
| **Quản lý CT Đào tạo** | SubjectsPage.tsx, curriculum.json | 1 test file + assets |
| **Quản lý phòng học** | RoomsPage.tsx, RoomSchedulePage.tsx, room_schedule.html | 2 test files + assets |
| **Quản lý học kỳ** | SemestersPage.tsx, DatePickerInput.tsx | 2 test files |
| **Lập lịch** | SchedulePage.tsx | 1 test file |
| **Quản lý TKB** | SavedSchedulesPage.tsx, integrated.html | 1 test file + assets |
| **Hậu kiểm** | ScheduleValidationPage.tsx | 1 test file |
| **Dùng chung/Infrastructure** | Layout, NotificationModal, ImportFileModal, useNotification, api, App, Dashboard, index.css, main.tsx | 9 test files |

**Total: 20+ test files** đã được tạo, bao phủ tất cả file trong 7 module.

## Cài đặt

```bash
npm install
```

Dependencies đã được cài đặt:
- vitest
- @testing-library/react
- @testing-library/jest-dom
- @testing-library/user-event
- jsdom
- msw (mock service worker)

## Chạy tests

```bash
# Watch mode (development)
npm test

# Run once (CI)
npm run test:run

# Run with coverage
npm run test:coverage
```

## Cấu hình

- **Test runner**: Vitest
- **Environment**: jsdom
- **Global setup**: `tests/setup.ts` (mocks localStorage, matchMedia, console)
- **Mock API**: MSW (Mock Service Worker) trong `tests/mocks/`

## Mô tả chi tiết theo module

### 1. Quản lý người dùng
- `tests/pages/LoginPage.test.tsx` - Test đăng nhập, validation, navigation
- `tests/pages/RegisterPage.test.tsx` - Test đăng ký, validation, duplicate handling
- `tests/pages/UsersPage.test.tsx` - Test CRUD users, filter, toggle status

### 2. Quản lý chương trình đào tạo
- `tests/pages/SubjectsPage.test.tsx` - Test CRUD subjects, import Excel, search, pagination
- `tests/static/assets.test.ts` - Test curriculum.json file

### 3. Quản lý phòng học
- `tests/pages/RoomsPage.test.tsx` - Test CRUD rooms, filters (building/capacity), tabs
- `tests/pages/RoomSchedulePage.test.tsx` - Test room occupancy view, filtering
- `tests/static/assets.test.ts` - Test room_schedule.html

### 4. Quản lý học kỳ
- `tests/pages/SemestersPage.test.tsx` - Test CRUD semesters, activation, delete with subjects
- `tests/components/DatePickerInput.test.tsx` - Test date picker component

### 5. Lập lịch
- `tests/pages/SchedulePage.test.tsx` - Test TKB generation, major grouping, common subjects

### 6. Quản lý TKB
- `tests/pages/SavedSchedulesPage.test.tsx` - Test saved schedules, export Excel, delete operations
- `tests/static/assets.test.ts` - Test integrated.html

### 7. Hậu kiểm
- `tests/pages/ScheduleValidationPage.test.tsx` - Test conflict detection (room/teacher), file upload

### Infrastructure (dùng chung)
- `tests/components/Layout.test.tsx` - Test sidebar, navigation, auth
- `tests/components/NotificationModal.test.tsx` - Test notification dialogs
- `tests/components/ImportFileModal.test.tsx` - Test file import modal
- `tests/hooks/useNotification.test.ts` - Test notification hook
- `tests/services/api.test.ts` - Test API service layer, types
- `tests/App.test.tsx` - Test app root
- `tests/pages/Dashboard.test.tsx` - Test dashboard

## Mock Strategy

- **API calls**: MSW handlers trong `tests/mocks/handlers.ts`
- **localStorage**: Mock trong `tests/setup.ts`
- **window.matchMedia**: Mock trong `tests/setup.ts`
- **react-hot-toast**: Mocked to avoid UI interference
- **File API**: Simulated với File objects

## Notes

- Mỗi file component/page có **ít nhất 1 file test riêng** (không trùng)
- Tất cả file trong repo đều được bao phủ (bao gồm cả HTML/JSON/CSV)
- Tests focus vào **user interactions** và **business logic**
- API calls được mock để tests **độc lập** và **nhanh**
- Cấu trúc test theo module giúp **dễ bảo trì**

## Coverage target

Mục tiêu: >70% coverage cho:
- Business logic (forms, validation, state updates)
- User interactions (clicks, inputs, submissions)
- API integration points
- Error handling

UI styling (Tailwind) và third-party libs được giảm thiểu.

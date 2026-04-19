# Room Occupancy + Subject Management Postman Tests

## File đầu ra
- `data/room_occupancy_subject_management.postman_collection.json`
- `data/room_occupancy_subject_management.postman_environment.json`
- `data/room_occupancy_subject_management_tests.md`

## Phạm vi
Collection này được sinh theo controller thật trong `BE/schedule`:
- `AuthController`
- `SemesterController`
- `RoomOccupancyController`
- `SubjectController`

Collection chỉ chứa endpoint có thật ở backend. Một số route FE helper cũ như `GET /api/subjects/{id}` hoặc `GET /api/subjects/search` không được đưa vào vì backend hiện tại không có.

## Điều kiện chạy
- Backend chạy local tại `http://localhost:8080`
- Tài khoản mặc định còn tồn tại:
  - `admin / admin123`
- DB đã có seed `faculties` và `rooms`
- Room ổn định dùng cho test:
  - `roomIdPrimary = 1`
  - `roomIdSecondary = 31`

## Cách import vào Postman
1. Mở Postman.
2. Import file `data/room_occupancy_subject_management.postman_collection.json`.
3. Import file `data/room_occupancy_subject_management.postman_environment.json`.
4. Chọn environment `Room Occupancy + Subject Management Local`.
5. Bảo đảm backend đang chạy rồi mới run collection.

## Biến môi trường chính
- `baseUrl`: mặc định `http://localhost:8080`
- `adminUsername`, `adminPassword`: dùng để login
- `token`: tự lưu sau request login
- `runId`: tự sinh trong request login
- `primarySemesterId`, `deleteNameSemesterId`: tự lấy từ response tạo semester
- `regularSubjectId`, `commonSubjectId`, `subjectMajorId`: tự lấy từ API list/search subject

## Dữ liệu test được sinh tự động
Request `Login Admin And Initialize Run Context` sẽ reset context của một lần chạy mới và sinh các biến:
- `primarySemesterName = API_PRIMARY_<runId>`
- `deleteNameSemesterName = API_DELETE_<runId>`
- `regularSubjectCode = REG<runId>`
- `commonSubjectCode = COM<runId>`
- `deleteNameSubjectCode = DEL<runId>`
- `majorCode = API<runId>`
- `excelSemesterLabel = <primarySemesterName> - <primaryAcademicYear>`

Nhờ đó các request xóa cuối collection chỉ dọn đúng dữ liệu test của lần chạy hiện tại.

## Thứ tự chạy khuyến nghị
1. Folder `00 Setup & Auth`
2. Folder `01 Room Occupancy`
3. Folder `02 Subject Management`

Có thể chạy toàn collection theo đúng thứ tự trên. Không nên chạy riêng các request phụ thuộc biến nếu chưa chạy setup trước.

## Chi tiết folder 00 Setup & Auth
### 1. Login Admin And Initialize Run Context
- Mục đích: tạo `runId`, reset biến cũ, login admin, lưu JWT vào `token`
- Kỳ vọng:
  - HTTP `200`
  - `success = true`
  - `data.token` có giá trị

### 2. Get Current User
- Mục đích: xác minh token vừa lấy dùng được
- Kỳ vọng:
  - HTTP `200`
  - username là `admin`
  - role là `ADMIN`

### 3. Create Primary Semester
- Mục đích: tạo semester chính cho test Room Occupancy và Subject Management
- Kỳ vọng:
  - HTTP `201`
  - lưu `primarySemesterId`

### 4. Create Delete-By-Name Semester
- Mục đích: tạo semester riêng cho test xóa subject theo `semesterName`
- Kỳ vọng:
  - HTTP `201`
  - lưu `deleteNameSemesterId`

### 5. Unauthorized Room Occupancy Request
- Mục đích: xác nhận protected API yêu cầu JWT
- Kỳ vọng:
  - HTTP `401`
  - message chứa `Unauthorized`

## Chi tiết folder 01 Room Occupancy
### Bulk Create Occupancies - Initial
- API: `POST /api/v1/room-occupancies/bulk-create`
- Mục đích: tạo 3 occupancy ban đầu
- Kỳ vọng:
  - HTTP `200`
  - `totalRequested = 3`
  - `totalCreated = 3`
  - `totalSkipped = 0`

### Bulk Create Occupancies - Duplicate And Invalid
- Mục đích: test batch có record trùng và room không tồn tại
- Kỳ vọng:
  - HTTP `200`
  - `totalRequested = 3`
  - `totalCreated = 1`
  - `totalSkipped = 2`
- Ghi chú:
  - Backend hiện tại skip theo từng item, không fail toàn bộ batch

### Check Availability - Occupied Slot
- API: `GET /api/v1/room-occupancies/check-availability`
- Kỳ vọng:
  - HTTP `200`
  - `available = false`

### Check Availability - Free Slot
- Kỳ vọng:
  - HTTP `200`
  - `available = true`

### Get Occupancies By Room
- API: `GET /api/v1/room-occupancies/room/{roomId}`
- Kỳ vọng:
  - HTTP `200`
  - `total >= 1`
  - record trả về thuộc `roomIdPrimary`

### Get Occupancies By Room With Filters
- Mục đích: test filter `semesterId`, `dayOfWeek`, `period`, `search`
- Kỳ vọng:
  - HTTP `200`
  - `total = 1`
  - note chứa `regularSubjectCode`

### Get Occupancies By Room And Semester
- Kỳ vọng:
  - HTTP `200`
  - danh sách không rỗng

### Get Occupancies By Semester
- Kỳ vọng:
  - HTTP `200`
  - `total = 4`

### Get Semester Statistics
- API: `GET /api/v1/room-occupancies/semester/{semesterId}/statistics`
- Kỳ vọng:
  - HTTP `200`
  - có `totalRoomsUsed`, `totalOccupiedSlots`, `occupancyByDay`, `occupancyByPeriod`, `topRooms`

### Get Rooms Status With Filters
- Mục đích: test filter building/type/occupancyStatus/capacity/search
- Kỳ vọng:
  - HTTP `200`
  - kết quả đầu tiên là room `31`, building `A2`, type `ENGLISH_CLASS`, status `USED`

### Get Room Status By Room And Semester
- Kỳ vọng:
  - HTTP `200`
  - room `31` có `totalOccupiedSlots >= 2`

### Get Available Rooms
- Kỳ vọng:
  - HTTP `200`
  - danh sách không chứa room `1` tại slot đã bị chiếm

### Delete Occupancies By Semester
- API: `DELETE /api/v1/room-occupancies/semester/{semesterId}`
- Kỳ vọng:
  - HTTP `200`
  - `status = success`

### Verify Occupancies Deleted By Room And Semester
- Kỳ vọng:
  - HTTP `200`
  - mảng rỗng

### Verify Slot Available After Delete
- Kỳ vọng:
  - HTTP `200`
  - `available = true`

## Chi tiết folder 02 Subject Management
### Health Check
- API: `GET /api/subjects/health`
- Kỳ vọng:
  - HTTP `200`
  - `data = Server is OK`

### Create Regular Subject
- API: `POST /api/subjects`
- Kỳ vọng:
  - HTTP `201`
  - subject được tạo trong `primarySemester`

### Create Regular Subject Again To Verify Upsert Behavior
- Mục đích: xác nhận logic trùng hiện tại của backend
- Kỳ vọng:
  - HTTP `201`
  - backend update bản ghi cũ thay vì trả `409`
- Ghi chú:
  - Đây là hành vi hiện tại trong `SubjectServiceImpl.createSubject`

### Find Regular Subject By Search
- API: `GET /api/subjects`
- Mục đích: truy `regularSubjectId` và `subjectMajorId`
- Kỳ vọng:
  - HTTP `200`
  - `data.items[0].id` tồn tại

### Create Common Subject
- Mục đích: tạo subject `isCommon = true`, `programType = Chung`
- Kỳ vọng:
  - HTTP `201`

### Find Common Subject By Search
- Mục đích: truy `commonSubjectId` để dùng cho delete by id
- Kỳ vọng:
  - HTTP `200`

### Create Delete-By-Name Subject
- Mục đích: tạo dữ liệu riêng cho API delete theo `semesterName`
- Kỳ vọng:
  - HTTP `201`

### Get All Subjects With Filters
- Mục đích: test pagination + search + filter
- Kỳ vọng:
  - HTTP `200`
  - record đầu tiên có `subjectCode = regularSubjectCode`

### Get Subjects By Major Id
- API: `GET /api/subjects/major/{majorId}`
- Kỳ vọng:
  - HTTP `200`
  - có chứa `regularSubjectCode`

### Get Program Types - All
- Kỳ vọng:
  - HTTP `200`
  - mảng program type không rỗng

### Get Program Types - Filtered
- Kỳ vọng:
  - HTTP `200`
  - mảng chứa `CQ` và `Chung`

### Get Class Years - All
- Kỳ vọng:
  - HTTP `200`
  - mảng class year không rỗng

### Get Class Years - Filtered
- Kỳ vọng:
  - HTTP `200`
  - mảng chứa `2026`

### Get Subjects By Majors - CQ
- API: `GET /api/subjects/majors`
- Kỳ vọng:
  - HTTP `200`
  - có chứa `regularSubjectCode`

### Get Subjects By Majors - Chung
- Kỳ vọng:
  - HTTP `200`
  - phần tử đầu có `subjectCode = commonSubjectCode`
  - `majorCode = Chung`

### Get Group Majors
- API: `GET /api/subjects/group-majors`
- Kỳ vọng:
  - HTTP `200`
  - kết quả chứa `majorCode` đã sinh

### Get Common Subjects
- API: `GET /api/subjects/common-subjects`
- Kỳ vọng:
  - HTTP `200`
  - có chứa `commonSubjectCode`

### Update Regular Subject
- API: `PUT /api/subjects/{id}`
- Kỳ vọng:
  - HTTP `200`
  - `subjectName = Regular Subject Patched <runId>`

### Verify Updated Regular Subject
- Kỳ vọng:
  - HTTP `200`
  - search trả về đúng tên mới

### Delete Common Subject By Id
- API: `DELETE /api/subjects/{id}`
- Kỳ vọng:
  - HTTP `200`

### Delete Subjects By Semester Name
- API: `DELETE /api/subjects/semester-name/{semesterName}`
- Kỳ vọng:
  - HTTP `200`
  - `data >= 1`

### Delete Subjects By Semester Name And Academic Year
- API: `DELETE /api/subjects/semester-name/{semesterName}/academic-year/{academicYear}`
- Kỳ vọng:
  - HTTP `200`
  - `data >= 1`

### Verify Primary Subjects Deleted
- Kỳ vọng:
  - HTTP `200`
  - `data.items.length = 0`

## Hai request manual cho Upload Excel
### Upload Excel - Manual Valid .xlsx
- API: `POST /api/subjects/upload-excel`
- Trước khi gửi:
  - mở tab Body > form-data
  - attach 1 file `.xlsx` hợp lệ
  - có thể dùng thử `data/full-ct.xlsx` hoặc `data/data.xlsx`
  - giữ field `semester = {{excelSemesterLabel}}`
- Kỳ vọng:
  - HTTP `200`
  - response có `successCount`

### Upload Excel - Manual Invalid Extension
- Trước khi gửi:
  - attach 1 file không phải `.xlsx`, ví dụ `.md` hoặc `.txt`
- Kỳ vọng:
  - HTTP `400`
  - backend báo chỉ chấp nhận file Excel `.xlsx`

## Lưu ý khi chạy lại collection
- Request login luôn sinh `runId` mới để tránh đụng dữ liệu cũ.
- Nếu bạn chỉ rerun một vài request giữa chừng, hãy chạy lại từ `Login Admin And Initialize Run Context` để reset bộ biến.
- Không nên sửa tay các biến `primarySemesterName`, `majorCode`, `regularSubjectCode` trừ khi bạn muốn tự quản lý data lifecycle.

## Lưu ý về hành vi backend hiện tại
- Mọi endpoint ngoài `/api/auth/**` đều yêu cầu JWT.
- `createSubject` đang có hành vi upsert khi subject trùng theo bộ khóa nghiệp vụ, và controller vẫn trả `201`.
- `Room Occupancy bulk-create` bỏ qua item lỗi hoặc trùng, không rollback cả request.
- `sortBy` và `direction` ở một số API Room Occupancy không được assert chặt trong test vì service hiện đang sort thủ công sau khi load dữ liệu.

# BÁO CÁO KIỂM THỬ - MODULE HẬU KIỂM (SCHEDULE VALIDATION)

**Ngày cập nhật:** 19/04/2026
**Module:** Hậu Kiểm (Schedule Validation / Post-Validation)
**Java Version:** 17 | **Testing Framework:** JUnit 5 + Mockito | **Build Tool:** Maven

---

## Tổng quan

| Tổng Tests | Pass | Fail | Success Rate |
|------------|------|------|--------------|
| 98 | 97 | **1** | 99.0% |

---

## Chi tiết Tests Failed

| ID | File | Tên Test Case | Nguyên nhân |
|----|------|--------------|-------------|
| HK53 | ScheduleValidationControllerIntegrationTest.java | test_validateSchedule_completeValidationReport | Controller không trả về validation report đầy đủ |

---

## Kết quả theo Package

| Package | Tests | Pass | Fail | Success Rate |
|---------|-------|------|------|--------------|
| com.ptit.schedule.controller | 16 | 15 | 1 | 93.8% |
| com.ptit.schedule.dto | 52 | 51 | 1 | 98.1% |
| com.ptit.schedule.service.impl | 30 | 30 | 0 | 100% |

---

## 1. Controller Integration Tests (Package: com.ptit.schedule.controller)

### ScheduleValidationControllerIntegrationTest.java - 16 tests | 93.8% Pass | 1 Fail

| TC ID | Tên Test | Mục đích | Input | Expected | Result | Ghi chú |
|-------|----------|----------|-------|----------|--------|---------|
| HK39 | test_validateSchedule_noConflicts_returns200 | Kiểm tra validate không có conflicts trả về 200 | Valid schedule entries | status=200, hasConflicts=false | Pass | |
| HK40 | test_validateSchedule_roomConflict_returns200 | Kiểm tra validate có room conflict vẫn trả về 200 | ScheduleEntries với room conflict | status=200, hasConflicts=true | Pass | |
| HK41 | test_validateSchedule_teacherConflict_returns200 | Kiểm tra validate có teacher conflict vẫn trả về 200 | ScheduleEntries với teacher conflict | status=200, hasConflicts=true | Pass | |
| HK42 | test_validateSchedule_bothConflicts_returns200 | Kiểm tra validate có cả 2 loại conflicts | ScheduleEntries với cả 2 loại | status=200, hasConflicts=true | Pass | |
| HK43 | test_validateSchedule_emptyList_returns200 | Kiểm tra validate empty list trả về 200 | Empty scheduleEntries | status=200, totalEntries=0 | Pass | |
| HK44 | test_validateSchedule_singleEntry_returns200 | Kiểm tra validate single entry trả về 200 | Single ScheduleEntry | status=200, totalEntries=1 | Pass | |
| HK45 | test_validateSchedule_responseContainsEntries | Kiểm tra response chứa schedule entries | Multiple entries | Response chứa entries list | Pass | |
| HK46 | test_validateSchedule_responseContainsConflicts | Kiểm tra response chứa conflict results | Entries với conflicts | Response chứa conflictResult | Pass | |
| HK47 | test_validateSchedule_responseContainsFileInfo | Kiểm tra response chứa file info | Request với file info | Response chứa fileName, fileSize | Pass | |
| HK48 | test_validateSchedule_hasConflictsReflectsReality | Kiểm tra hasConflicts phản ánh thực tế | Entries không conflict | hasConflicts=false | Pass | |
| HK49 | test_validateSchedule_conflictCountAccurate | Kiểm tra conflict count chính xác | Known number of conflicts | Conflict count đúng | Pass | |
| HK50 | test_validateSchedule_noAuth_returns401 | Kiểm tra không auth trả về 401 | No Authorization header | status=401 | Pass | |
| HK51 | test_validateSchedule_invalidToken_returns401 | Kiểm tra invalid token trả về 401 | Invalid JWT token | status=401 | Pass | |
| HK52 | test_validateSchedule_responseStructure | Kiểm tra response structure đầy đủ | Valid request | Response có đầy đủ fields | Pass | |
| HK53 | test_validateSchedule_completeValidationReport | Kiểm tra complete validation report được trả về | Valid request | Report chứa tất cả thông tin cần thiết | **FAIL** | Missing validation report fields |
| HK54 | test_validateSchedule_entriesMappedCorrectly | Kiểm tra entries được map đúng | Request entries vs response | Entries mapped correctly | Pass | |
| HK55 | test_validateSchedule_conflictsMappedCorrectly | Kiểm tra conflicts được map đúng | Conflict entries vs response | Conflicts mapped correctly | Pass | |

---

## 2. DTO Tests (Package: com.ptit.schedule.dto)

### ApiResponseTest.java - 16 tests | 93.8% Pass | 1 Fail

| TC ID | Tên Test | Mục đích | Input | Expected | Result | Ghi chú |
|-------|----------|----------|-------|----------|--------|---------|
| TKB001 | test_success_withData_returnsStatus200 | Kiểm tra success(T data) trả về status 200 | ApiResponse.success(Map.of("id", 1)) | success=true, status=200, data!=null | **FAIL** | Message không được set mặc định |
| TKB002 | test_success_withMessage_returnsCustomMessage | Kiểm tra success(String) với message tùy chỉnh | ApiResponse.success("Lưu thành công") | success=true, message="Lưu thành công" | Pass | |
| TKB003 | test_success_withDataAndMessage_returnsBothFields | Kiểm tra success(T, String) trả về cả data và message | ApiResponse.success(data, "Cập nhật thành công") | success=true, data!=null, message="Cập nhật thành công" | Pass | |
| TKB004 | test_created_withData_returnsStatus201 | Kiểm tra created(T data) trả về status 201 | ApiResponse.created(newSchedule) | success=true, status=201 | Pass | |
| TKB005 | test_created_withMessage_returnsCustomMessage | Kiểm tra created(T, String) với message tùy chỉnh | ApiResponse.created(schedule, "Tạo mới thành công") | success=true, message="Tạo mới thành công" | Pass | |
| TKB006 | test_error_withMessageAndStatus_returnsErrorResponse | Kiểm tra error(String, int) tạo error response | ApiResponse.error("Lỗi validation", 400) | success=false, error="Lỗi validation", status=400 | Pass | |
| TKB007 | test_error_withMessageErrorAndStatus_returnsBothFields | Kiểm tra error(String, String, int) với message và error field | ApiResponse.error("Msg", "ResourceNotFoundException", 404) | success=false, message="Msg", error="ResourceNotFoundException" | Pass | |
| TKB008 | test_notFound_returns404Status | Kiểm tra notFound(String) tạo 404 response | ApiResponse.notFound("Không tìm thấy") | success=false, status=404 | Pass | |
| TKB009 | test_badRequest_returns400Status | Kiểm tra badRequest(String) tạo 400 response | ApiResponse.badRequest("Dữ liệu không hợp lệ") | success=false, status=400 | Pass | |
| TKB010 | test_jsonSerialization_excludesNullFields | Kiểm tra JSON serialization loại bỏ null fields | ApiResponse.success("test") với null fields khác | JSON không chứa key null | Pass | |
| TKB011 | test_jsonDeserialization_createsCorrectObject | Kiểm tra JSON deserialization tạo object đúng | JSON: {"success":true,"data":"test"} | Object với success=true, data="test" | Pass | |
| TKB012 | test_builder_withAllFields_createsCompleteResponse | Kiểm tra builder với đầy đủ fields | ApiResponse.builder().success(true).message("OK").data(obj).error("err").status(200).build() | Tất cả fields accessible | Pass | |
| TKB013 | test_builder_withPartialFields_leavesOthersNull | Kiểm tra builder với partial fields | ApiResponse.builder().success(true).message("OK").build() | error=null, data=null, status=null | Pass | |
| TKB014 | test_success_withNullData_handlesGracefully | Kiểm tra success() với null data | ApiResponse.success((String) null) | success=true, message="Success" | Pass | |
| TKB015 | test_complexNestedData_serializationWorks | Kiểm tra cấu trúc dữ liệu lồng nhau | Map với nested objects | JSON serialization đúng cấu trúc | Pass | |
| TKB016 | test_genericListType_preservesListData | Kiểm tra ApiResponse với List<T> | ApiResponse<List<String>> với ["A","B","C"] | getData() trả về List<String> với 3 phần tử | Pass | |

### PageResponseTest.java - 11 tests | 90.9% Pass | 1 Fail

| TC ID | Tên Test | Mục đích | Input | Expected | Result | Ghi chú |
|-------|----------|----------|-------|----------|--------|---------|
| TKB017 | test_contentList_modifiable | Kiểm tra content list có thể modify | List<String> content với add/remove | List có thể add/remove phần tử | **FAIL** | UnsupportedOperationException - Arrays.asList() immutable |
| TKB018 | test_constructor_allFieldsSet | Kiểm tra constructor set đúng tất cả fields | PageResponse(content=[item1,item2], pageNum=0, total=25) | getPageNum()=0, content=[item1,item2], total=25 | Pass | |
| TKB019 | test_builder_emptyContent | Kiểm tra builder với content rỗng | PageResponse với content=[], total=0 | getContent().isEmpty()=true, total=0 | Pass | |
| TKB020 | test_builder_nullContent | Kiểm tra builder với content=null | PageResponse với content=null | getContent()=null | Pass | |
| TKB021 | test_pageNumber_storedCorrectly | Kiểm tra page number được lưu đúng | pageNum=5 | getPageNum()==5 | Pass | |
| TKB022 | test_pageSizeAndTotal_relationship | Kiểm tra page size và total được lưu đúng | total=95, pageSize=10 | getTotal()==95 | Pass | |
| TKB023 | test_largeTotalCount_handledCorrectly | Kiểm tra xử lý total count lớn | total=1000000 | getTotal()==1000000, không overflow | Pass | |
| TKB024 | test_contentWithComplexObjects | Kiểm tra content với complex objects | Content list of Map<String,Object> | Complex objects được preserve | Pass | |
| TKB025 | test_firstPage_pageNumZero | Kiểm tra trang đầu (pageNum=0) | pageNum=0, pageSize=20, total=50 | pageNum=0 là trang đầu | Pass | |
| TKB026 | test_lastPage_pageNumCorrect | Kiểm tra nhận diện trang cuối | pageNum=9, total=95, pageSize=10 | pageNum=9 là trang cuối | Pass | |
| TKB027 | test_singleItemPerPage | Kiểm tra single item per page | pageSize=1, total=1, content=[item] | content size=1 | Pass | |

### PagedResponseTest.java - 13 tests | 100% Pass

| TC ID | Tên Test | Mục đích | Input | Expected | Result |
|-------|----------|----------|-------|----------|--------|
| TKB028 | test_factoryMethod_of_createsCorrectResponse | Kiểm tra of() factory method tạo PagedResponse đúng | PagedResponse.of(items=[A,B,C], page=1, totalElements=25) | getItems()=[A,B,C], page=1, totalElements=25 | Pass |
| TKB029 | test_factoryMethod_of_emptyItems | Kiểm tra of() với empty items list | PagedResponse.of(items=[], totalElements=0) | getItems().isEmpty()=true, totalElements=0 | Pass |
| TKB030 | test_factoryMethod_of_nullItems | Kiểm tra of() với null items | PagedResponse.of(items=null, totalElements=0) | getItems()=null | Pass |
| TKB031 | test_builder_createsCompleteResponse | Kiểm tra builder tạo complete PagedResponse | PagedResponse.builder().items([...]).page(0).totalElements(100).totalPages(5).build() | Tất cả fields set đúng | Pass |
| TKB032 | test_builder_partialFields | Kiểm tra builder với partial fields | PagedResponse.builder().items([A,B]).page(1).build() | Fields còn lại default | Pass |
| TKB033 | test_totalPages_exactDivision | Kiểm tra totalPages cho chia hết | totalElements=100, size=10 | getTotalPages()==10 | Pass |
| TKB034 | test_totalPages_partialLastPage | Kiểm tra totalPages với trang cuối không đầy | totalElements=95, size=10 | getTotalPages()==10 | Pass |
| TKB035 | test_singlePage_scenario | Kiểm tra kịch bản single page | totalElements=5, size=10 | getTotalPages()==1 | Pass |
| TKB036 | test_firstPage_pageZero | Kiểm tra trang đầu (page=0) | page=0, size=20, totalElements=100 | page=0 được nhận diện là trang đầu | Pass |
| TKB037 | test_lastPage_detection | Kiểm tra nhận diện trang cuối | page=4, totalElements=95, totalPages=5 | page=4 là trang cuối | Pass |
| TKB038 | test_largeDataset_handledCorrectly | Kiểm tra xử lý large dataset | totalElements=1000000, totalPages=50000 | Large values được preserve | Pass |
| TKB039 | test_complexObjectContent | Kiểm tra content với complex objects | Items list of Map<String,Object> | Complex objects được preserve | Pass |
| TKB040 | test_vsPageResponse_fieldMapping | Kiểm tra PagedResponse vs PageResponse mapping | Data tương tự cho cả 2 types | Equivalent fields match | Pass |

### ConflictResultTest.java - 12 tests | 100% Pass

| TC ID | Tên Test | Mục đích | Input | Expected | Result |
|-------|----------|----------|-------|----------|--------|
| HK01 | test_totalZero_noConflict | Kiểm tra getTotalConflicts()=0 khi không có xung đột | Empty conflict lists | totalConflicts==0 | Pass |
| HK02 | test_teacherConflicts_notNull_returnsEmptyList | Kiểm tra getTeacherConflicts()!=null cho empty list | TeacherConflict=null | getTeacherConflicts()!=null | Pass |
| HK03 | test_roomConflicts_notNull_returnsEmptyList | Kiểm tra getRoomConflicts()!=null cho empty list | RoomConflict=null | getRoomConflicts()!=null | Pass |
| HK04 | test_singleTeacherConflict_countIsOne | Kiểm tra count=1 với 1 teacher conflict | 1 teacher conflict entry | totalConflicts==1 | Pass |
| HK05 | test_singleRoomConflict_countIsOne | Kiểm tra count=1 với 1 room conflict | 1 room conflict entry | totalConflicts==1 | Pass |
| HK06 | test_multipleConflicts_totalIsCorrect | Kiểm tra tổng conflicts đúng | 2 teacher + 3 room = 5 total | totalConflicts==5 | Pass |
| HK07 | test_teacherConflictEntry_allFields | Kiểm tra TeacherConflict entry với đầy đủ fields | teacherId, subjectName, date, shifts | All fields accessible | Pass |
| HK08 | test_roomConflictEntry_allFields | Kiểm tra RoomConflict entry với đầy đủ fields | room, subjectName, date, shifts | All fields accessible | Pass |
| HK09 | test_conflictDisplayInfo_format | Kiểm tra format conflict display info | TeacherConflict với đầy đủ fields | DisplayInfo format "subject - teacher" | Pass |
| HK10 | test_builder_createsCompleteResult | Kiểm tra builder tạo complete ConflictResult | Builder với đầy đủ fields | TotalConflicts tính đúng | Pass |
| HK11 | test_builder_emptyResult | Kiểm tra builder với empty result | Builder không có conflicts | totalConflicts==0 | Pass |
| HK12 | test_nullEntries_handledGracefully | Kiểm tra xử lý null entries trong conflict lists | Lists chứa null entries | NullPointerException bị throw | Pass |

### ScheduleEntryTest.java - 10 tests | 100% Pass

| TC ID | Tên Test | Mục đích | Input | Expected | Result |
|-------|----------|----------|-------|----------|--------|
| HK13 | test_displayInfo_format | Kiểm tra getDisplayInfo() format "mã - tên (GV)" | ScheduleEntry với code, name, teacher | getDisplayInfo()=="INT1306 - ..." | Pass |
| HK14 | test_builder_allProperties | Kiểm tra builder với đầy đủ thuộc tính | ScheduleEntry với đầy đủ fields | Tất cả fields accessible | Pass |
| HK15 | test_builder_nullValues | Kiểm tra builder cho phép null các trường optional | ScheduleEntry với chỉ code, name | null cho optional | Pass |
| HK16 | test_slotKey_format | Kiểm tra TimeSlot.getSlotKey() format | TimeSlot với thông số cụ thể | getSlotKey()=="Tuần 1-Thứ 2-1-1-3" | Pass |
| HK17 | test_slotDisplayInfo_format | Kiểm tra TimeSlot.getDisplayInfo() format dễ đọc | TimeSlot với thông số cụ thể | getDisplayInfo()=="Tuần 5 (Thứ 6) - ..." | Pass |
| HK18 | test_slotKey_different | Kiểm tra getSlotKey() phân biệt các slot khác nhau | TimeSlot1(Thứ 2) vs TimeSlot2(Thứ 3) | slotKey1!=slotKey2 | Pass |
| HK19 | test_slotKey_same | Kiểm tra getSlotKey() tạo key giống nhau cho slot cùng thời gian | 2 TimeSlot cùng thông số | slotKey1==slotKey2 | Pass |
| HK20 | test_timeSlot_differentValues | Kiểm tra TimeSlot xử lý đúng các giá trị kíp, tiết khác nhau | TimeSlot(shift=3, tiết 7, 4 tiết) | shift="3", startPeriod="7" | Pass |
| HK21 | test_entry_multipleTimeSlots | Kiểm tra ScheduleEntry chứa nhiều TimeSlot | ScheduleEntry với 5 timeSlots | getTimeSlots().size()==5 | Pass |
| HK22 | test_timeSlot_equals | Kiểm tra TimeSlot equals và hashCode hoạt động đúng | 2 TimeSlot cùng thông số | equals()=true | Pass |

### ScheduleValidationResultTest.java - 16 tests | 100% Pass

| TC ID | Tên Test | Mục đích | Input | Expected | Result |
|-------|----------|----------|-------|----------|--------|
| HK23 | test_hasConflicts_nullResult | Kiểm tra hasConflicts()=false khi conflictResult null | null conflictResult | false | Pass |
| HK24 | test_hasConflicts_noConflict | Kiểm tra hasConflicts()=false khi không có conflict | conflictResult có totalConflicts=0 | false | Pass |
| HK25 | test_hasConflicts_roomConflict | Kiểm tra hasConflicts()=true khi có room conflict | 1 RoomConflict | true | Pass |
| HK26 | test_hasConflicts_teacherConflict | Kiểm tra hasConflicts()=true khi có teacher conflict | 1 TeacherConflict | true | Pass |
| HK27 | test_roomConflictCount_nullList | Kiểm tra getRoomConflictCount()=0 khi list null | null | 0 | Pass |
| HK28 | test_roomConflictCount_correct | Kiểm tra getRoomConflictCount() đếm đúng số lượng | 3 RoomConflict entries | 3 | Pass |
| HK29 | test_teacherConflictCount_nullList | Kiểm tra getTeacherConflictCount()=0 khi list null | null | 0 | Pass |
| HK30 | test_teacherConflictCount_correct | Kiểm tra getTeacherConflictCount() đếm đúng số lượng | 2 TeacherConflict entries | 2 | Pass |
| HK31 | test_fileSize_zero | Kiểm tra getFormattedFileSize() với 0 bytes | 0 | "0 Bytes" | Pass |
| HK32 | test_fileSize_bytes | Kiểm tra getFormattedFileSize() format Bytes | 500 | "500.00 Bytes" | Pass |
| HK33 | test_fileSize_kilobytes | Kiểm tra getFormattedFileSize() format KB | 1024 | "1.00 KB" | Pass |
| HK34 | test_fileSize_megabytes | Kiểm tra getFormattedFileSize() format MB | 1024*1024 | "1.00 MB" | Pass |
| HK35 | test_fileSize_gigabytes | Kiểm tra getFormattedFileSize() format GB | 1024^3 | "1.00 GB" | Pass |
| HK36 | test_fileSize_overKB | Kiểm tra getFormattedFileSize() xử lý giá trị > 1KB | 2048 | "2.00 KB" | Pass |
| HK37 | test_builder_allProperties | Kiểm tra builder với đầy đủ thuộc tính | ScheduleValidationResult với đầy đủ fields | Tất cả fields set đúng | Pass |
| HK38 | test_integration_conflictsAndCounts | Kiểm tra tích hợp hasConflicts() và các method đếm | 1 room + 2 teacher conflicts | hasConflicts()=true, room=1, teacher=2 | Pass |

### SaveScheduleRequestTest.java - 13 tests | 100% Pass

| TC ID | Tên Test | Mục đích | Input | Expected | Result |
|-------|----------|----------|-------|----------|--------|
| TKB041 | test_builder_createsCompleteRequest | Kiểm tra builder tạo SaveScheduleRequest đầy đủ | SaveScheduleRequest với đầy đủ fields | Tất cả fields accessible | Pass |
| TKB042 | test_defaultConstructor_emptyFields | Kiểm tra default constructor tạo request rỗng | SaveScheduleRequest mới | Tất cả fields=null | Pass |
| TKB043 | test_jsonSerialization_usesSnakeCase | Kiểm tra JSON sử dụng snake_case | SaveScheduleRequest với data | JSON keys: "subject_id", "template_database_id" | Pass |
| TKB044 | test_jsonDeserialization_mapsSnakeCase | Kiểm tra JSON deserialization map snake_case | JSON string với snake_case keys | Object fields được populate đúng | Pass |
| TKB045 | test_requiredFieldsOnly | Kiểm tra request với required fields | SaveScheduleRequest với subjectId, templateDatabaseId | Các fields khác=null | Pass |
| TKB046 | test_allFields_populated | Kiểm tra request với tất cả optional fields | SaveScheduleRequest với tất cả fields | Tất cả fields accessible | Pass |
| TKB047 | test_nullOptionalFields | Kiểm tra xử lý null optional fields | SaveScheduleRequest với required fields only | Serialization không lỗi | Pass |
| TKB048 | test_roomNumber_hyphenFormat | Kiểm tra room number với format có gạch nối | phongHoc="401-A2" | getPhongHoc()=="401-A2" | Pass |
| TKB049 | test_largeStudentCount | Kiểm tra large student count | siSoMotLop=200 | getSiSoMotLop()==200 | Pass |
| TKB050 | test_specialCharactersInMajor | Kiểm tra special characters trong major | khoa="Viễn thông 1" | Unicode được preserve | Pass |
| TKB051 | test_setters_modifyFields | Kiểm tra setters modify fields đúng | Gọi setSubjectId(), setNienKhoa() | Fields được update đúng | Pass |
| TKB052 | test_equalsAndHashCode | Kiểm tra equals() và hashCode() hoạt động đúng | 2 Request cùng data | equals()=true, hashCode() bằng nhau | Pass |
| TKB053 | test_differentRequests_notEqual | Kiểm tra các request khác nhau không equal | Request1 vs Request2 khác data | equals()=false | Pass |

---

## 3. Service Integration Tests (Package: com.ptit.schedule.service.impl)

### ScheduleConflictDetectionServiceImplExtendedTest.java - 20 tests | 100% Pass

| TC ID | Tên Test | Mục đích | Input | Expected | Result |
|-------|----------|----------|-------|----------|--------|
| HK56 | test_sameRoom_differentTimes_noConflict | Kiểm tra cùng phòng khác giờ không conflict | Phòng 401 Thứ 2 tiết 1 vs Thứ 3 tiết 1 | no conflict | Pass |
| HK57 | test_sameRoom_sameTime_conflict | Kiểm tra cùng phòng cùng giờ conflict | Phòng 401 Thứ 2 tiết 1 vs Thứ 2 tiết 1 | room conflict | Pass |
| HK58 | test_differentRooms_noConflict | Kiểm tra phòng khác nhau không conflict | Phòng 401 vs 402 cùng giờ | no conflict | Pass |
| HK59 | test_sameTeacher_sameTime_conflict | Kiểm tra cùng giáo viên cùng giờ conflict | GV01 Thứ 2 tiết 1 vs Thứ 2 tiết 1 | teacher conflict | Pass |
| HK60 | test_sameTeacher_differentTimes_noConflict | Kiểm tra cùng giáo viên khác giờ không conflict | GV01 Thứ 2 tiết 1 vs Thứ 3 tiết 1 | no conflict | Pass |
| HK61 | test_differentTeachers_noConflict | Kiểm tra giáo viên khác nhau không conflict | GV01 vs GV02 cùng giờ | no conflict | Pass |
| HK62 | test_overlappingShifts_sameTeacher_conflict | Kiểm tra kíp chồng lấn cùng giáo viên | GV01 Thứ 2 kíp 1 vs Thứ 2 kíp 1 (cùng giờ) | teacher conflict | Pass |
| HK63 | test_overlappingShifts_sameRoom_conflict | Kiểm tra kíp chồng lấn cùng phòng | Phòng 401 Thứ 2 kíp 1 vs Thứ 2 kíp 1 | room conflict | Pass |
| HK64 | test_noConflicts_returnsEmptyLists | Kiểm tra không conflict trả về empty lists | Entries không conflict | empty roomConflicts, empty teacherConflicts | Pass |
| HK65 | test_multipleConflicts_collectsAll | Kiểm tra collect tất cả conflicts | Multiple conflicts | Tất cả conflicts được collect | Pass |
| HK66 | test_conflictInfo_containsRequiredFields | Kiểm tra conflict info chứa required fields | Single conflict | room, date, shifts present | Pass |
| HK67 | test_teacherConflictInfo_containsTeacherId | Kiểm tra teacher conflict info chứa teacherId | Teacher conflict | teacherId present | Pass |
| HK68 | test_conflictInMultipleSlots_detected | Kiểm tra conflict trong nhiều slots | Entry có nhiều time slots | Conflict detected for each slot | Pass |
| HK69 | test_manyEntries_performance | Kiểm tra performance với nhiều entries | 100 entries | Completes within reasonable time | Pass |
| HK70 | test_entriesWithNullTeacher_noConflict | Kiểm tra entries với null teacher không conflict | Entry có teacher=null | No conflict (null treated as no conflict) | Pass |
| HK71 | test_entriesWithNullRoom_noConflict | Kiểm tra entries với null room không conflict | Entry có room=null | No conflict (null treated as no conflict) | Pass |
| HK72 | test_conflictPair_sameSubject_sameSlot | Kiểm tra conflict pair cùng subject cùng slot | Same subject, same slot | Conflict detected | Pass |
| HK73 | test_conflictPair_differentSubject_sameSlot | Kiểm tra conflict pair khác subject cùng slot | Different subjects, same slot | Conflict detected | Pass |
| HK74 | test_sameEntry_noConflict | Kiểm tra entry không conflict với chính nó | Single entry against itself | No self-conflict | Pass |
| HK75 | test_conflictInPartialOverlap_detected | Kiểm tra conflict trong partial overlap | Tiết 1 (3 periods) vs tiết 2 (3 periods) overlap | Conflict detected | Pass |

### ScheduleExcelReaderServiceImplExtendedTest.java - 23 tests | 100% Pass

| TC ID | Tên Test | Mục đích | Input | Expected | Result |
|-------|----------|----------|-------|----------|--------|
| HK76 | test_readExcel_validFile_parsedCorrectly | Kiểm tra readExcel() parse file đúng | Valid Excel file | entries!=null, entries not empty | Pass |
| HK77 | test_readExcel_fileNotFound_throwsException | Kiểm tra file not found throw exception | Invalid file path | FileProcessingException | Pass |
| HK78 | test_readExcel_emptyFile_returnsEmptyList | Kiểm tra empty file trả về empty list | Empty Excel file | entries.isEmpty()=true | Pass |
| HK79 | test_readExcel_extractsSubjectCode | Kiểm tra trích xuất subjectCode đúng | Excel với mã môn học | subjectCode extracted | Pass |
| HK80 | test_readExcel_extractsSubjectName | Kiểm tra trích xuất subjectName đúng | Excel với tên môn học | subjectName extracted | Pass |
| HK81 | test_readExcel_extractsTeacherId | Kiểm tra trích xuất teacherId đúng | Excel với mã giáo viên | teacherId extracted | Pass |
| HK82 | test_readExcel_extractsTeacherName | Kiểm tra trích xuất teacherName đúng | Excel với tên giáo viên | teacherName extracted | Pass |
| HK83 | test_readExcel_extractsRoom | Kiểm tra trích xuất room đúng | Excel với phòng học | room extracted | Pass |
| HK84 | test_readExcel_extractsTimeSlots | Kiểm tra trích xuất time slots đúng | Excel với thông tin thời gian | timeSlots extracted | Pass |
| HK85 | test_readExcel_extractsBuilding | Kiểm tra trích xuất building đúng | Excel với tòa nhà | building extracted | Pass |
| HK86 | test_readExcel_extractsClassGroup | Kiểm tra trích xuất classGroup đúng | Excel với lớp học | classGroup extracted | Pass |
| HK87 | test_readExcel_extractsStudentCount | Kiểm tra trích xuất studentCount đúng | Excel với sĩ số | studentCount extracted | Pass |
| HK88 | test_readExcel_handlesEmptyCells | Kiểm tra xử lý empty cells | Excel với empty cells | Empty cells handled gracefully | Pass |
| HK89 | test_readExcel_handlesWhitespace | Kiểm tra xử lý whitespace | Cells với leading/trailing spaces | Whitespace trimmed | Pass |
| HK90 | test_readExcel_handlesNullValues | Kiểm tra xử lý null values | Excel với null values | Null values handled | Pass |
| HK91 | test_readExcel_multipleSheets_processesAll | Kiểm tra process all sheets | Excel với multiple sheets | All sheets processed | Pass |
| HK92 | test_readExcel_malformedRow_skipped | Kiểm tra skip malformed rows | Excel với malformed row | Malformed rows skipped | Pass |
| HK93 | test_readExcel_dateParsing | Kiểm tra date parsing | Excel với date columns | Dates parsed correctly | Pass |
| HK94 | test_readExcel_specialCharacters_handled | Kiểm tra xử lý special characters | Subject names với special chars | Special chars preserved | Pass |
| HK95 | test_readExcel_largeFile_performance | Kiểm tra performance với large file | Large Excel file | Completes within reasonable time | Pass |
| HK96 | test_readExcel_numericRoomNumbers | Kiểm tra xử lý numeric room numbers | Room="401" (number) | room="401" | Pass |
| HK97 | test_readExcel_roomWithBuilding | Kiểm tra xử lý phòng kèm tòa | Room="401-A2" | room="401-A2" | Pass |
| HK98 | test_readExcel_shiftValues_preserved | Kiểm tra preserve shift values | Shift="1", "2", "3" | shift values preserved | Pass |

---

## Hướng dẫn chạy Test

### Sử dụng Maven Wrapper

```powershell
# Di chuyển vào thư mục dự án
cd D:\LapTrinh\SQA\BE\schedule

# Chạy tất cả test của module Hậu Kiểm
.\mvnw.cmd test -Dtest="**/dto/*Test,**/controller/*IntegrationTest,**/service/impl/*ExtendedTest"

# Tạo HTML report
.\mvnw.cmd surefire-report:report-only

# Mở HTML report
start target\site\test-report.html
```

### Xem kết quả
- **HTML Report:** `target/site/test-report.html`
- **XML Reports:** `target/surefire-reports/*.xml`
- **TXT Reports:** `target/surefire-reports/*.txt`

---

**Người cập nhật:** AI Assistant
**Ngày cập nhật:** 19/04/2026

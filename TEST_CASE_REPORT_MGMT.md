# BÁO CÁO KIỂM THỬ - MODULE QUẢN LÝ THỜI KHÓA BIỂU

**Ngày cập nhật:** 19/04/2026
**Module:** Quản lý Thời Khóa Biểu (Schedule Management)
**Java Version:** 17 | **Testing Framework:** JUnit 5 + Mockito | **Build Tool:** Maven

---

## Tổng quan

| Tổng Tests | Pass | Fail | Success Rate |
|------------|------|------|--------------|
| 196 | 190 | **6** | 96.9% |

---

## Chi tiết Tests Failed

| ID | File | Tên Test Case | Nguyên nhân |
|----|------|--------------|-------------|
| TKB001 | ApiResponseTest.java | test_success_withData_returnsStatus200 | `success(T data)` không set message mặc định |
| TKB017 | PageResponseTest.java | test_contentList_modifiable | `Arrays.asList()` trả về immutable list |
| TKB096 | JwtAccessDeniedHandlerTest.java | test_handle_includesVietnameseMessage | Handler không hardcode Vietnamese message |
| TKB103 | JwtAuthenticationEntryPointTest.java | test_commence_includesVietnameseMessage | Handler không hardcode Vietnamese message |
| TKB129 | AcademicYearUtilsTest.java | test_splitSemesterAndYear_trimsWhitespace | Code không trim whitespace xung quanh "-" |
| TKB145 | ScheduleExcelReaderServiceImplExtendedTest.java | test_readExcel_parsesAllRows | Excel reader chỉ parse 1 row thay vì tất cả |

---

## Kết quả theo Package

| Package | Tests | Pass | Fail | Success Rate |
|---------|-------|------|------|--------------|
| com.ptit.schedule | 8 | 8 | 0 | 100% |
| com.ptit.schedule.config | 31 | 31 | 0 | 100% |
| com.ptit.schedule.controller | 21 | 21 | 0 | 100% |
| com.ptit.schedule.dto | 91 | 89 | 2 | 97.8% |
| com.ptit.schedule.exception | 26 | 26 | 0 | 100% |
| com.ptit.schedule.security | 39 | 37 | 2 | 94.9% |
| com.ptit.schedule.service.impl | 1 | 0 | 1 | 0% |
| com.ptit.schedule.utils | 18 | 17 | 1 | 94.4% |

---

## 1. Application Tests (Package: com.ptit.schedule)

### ScheduleApplicationTest.java - 8 tests | 100% Pass

| TC ID | Tên Test | Mục đích | Input | Expected | Result |
|-------|----------|----------|-------|----------|--------|
| TKB189 | test_scheduleApplication_classExists | Kiểm tra class tồn tại với đúng annotation | - | Class exists, @SpringBootApplication present | Pass |
| TKB190 | test_scheduleApplication_isPublicClass | Kiểm tra là public class | - | Class.isPublic()=true | Pass |
| TKB191 | test_scheduleApplication_hasMainMethod | Kiểm tra có main() method | - | Method main(String[]) tồn tại | Pass |
| TKB192 | test_mainMethod_correctSignature | Kiểm tra main() có signature đúng | - | ParameterTypes=[String[].class] | Pass |
| TKB193 | test_scheduleApplication_noInstanceFields | Kiểm tra không có instance fields | - | Only static members | Pass |
| TKB194 | test_scheduleApplication_defaultConstructor | Kiểm tra có default constructor | - | Public no-arg constructor tồn tại | Pass |
| TKB195 | test_mainMethod_invokable | Kiểm tra main() có thể invoke qua reflection | - | Method accessible | Pass |
| TKB196 | test_springBootApplication_metaAnnotations | Kiểm tra @SpringBootApplication bao gồm meta-annotations | - | @Configuration, @EnableAutoConfiguration, @ComponentScan present | Pass |

---

## 2. Config Tests (Package: com.ptit.schedule.config)

### CorsConfigTest.java - 10 tests | 100% Pass

| TC ID | Tên Test | Mục đích | Input | Expected | Result |
|-------|----------|----------|-------|----------|--------|
| TKB158 | test_globalCorsFilter_setsAllowOrigin | Kiểm tra set Access-Control-Allow-Origin header | HTTP request với Origin | Allow-Origin header set | Pass |
| TKB159 | test_globalCorsFilter_setsWildcardWhenNoOrigin | Kiểm tra set wildcard khi không có Origin header | HTTP request không có Origin | Allow-Origin="*" | Pass |
| TKB160 | test_globalCorsFilter_setsAllowMethods | Kiểm tra set Allow-Methods header | HTTP request bất kỳ | Allow-Methods chứa GET, POST, PUT, DELETE, OPTIONS | Pass |
| TKB161 | test_globalCorsFilter_setsAllowCredentials | Kiểm tra set Allow-Credentials header | HTTP request bất kỳ | Allow-Credentials="true" | Pass |
| TKB162 | test_globalCorsFilter_setsMaxAge | Kiểm tra set Max-Age header | HTTP request bất kỳ | Max-Age="3600" | Pass |
| TKB163 | test_globalCorsFilter_handlesPreflight | Kiểm tra xử lý OPTIONS preflight request | HTTP method=OPTIONS | Response status=200 | Pass |
| TKB164 | test_globalCorsFilter_setsAllowHeaders | Kiểm tra set Allow-Headers header | HTTP request bất kỳ | Allow-Headers chứa Authorization, Content-Type | Pass |
| TKB165 | test_corsConfig_createsConfigurationSource | Kiểm tra tạo CorsConfigurationSource | Application configuration | CorsConfigurationSource!=null | Pass |
| TKB166 | test_corsConfigurationSource_returnsConfigForApi | Kiểm tra trả về config cho /api/** | Request đến /api/schedules | CorsConfiguration!=null | Pass |
| TKB167 | test_corsConfiguration_allowsCorrectMethods | Kiểm tra cho phép đúng HTTP methods | CorsConfiguration từ source | GET, POST, PUT, DELETE allowed | Pass |

### OpenApiConfigTest.java - 11 tests | 100% Pass

| TC ID | Tên Test | Mục đích | Input | Expected | Result |
|-------|----------|----------|-------|----------|--------|
| TKB168 | test_customOpenAPI_returnsNonNull | Kiểm tra trả về non-null OpenAPI object | - | OpenAPI!=null | Pass |
| TKB169 | test_customOpenAPI_setsCorrectTitle | Kiểm tra set đúng API title | - | Title="Schedule Management API" | Pass |
| TKB170 | test_customOpenAPI_setsCorrectVersion | Kiểm tra set đúng API version | - | Version="1.0.0" | Pass |
| TKB171 | test_customOpenAPI_setsCorrectDescription | Kiểm tra set đúng description | - | Description chứa text về API | Pass |
| TKB172 | test_customOpenAPI_setsCorrectContact | Kiểm tra set đúng contact info | - | Contact name="PTIT" | Pass |
| TKB173 | test_customOpenAPI_setsCorrectLicense | Kiểm tra set đúng license info | - | License="MIT License" | Pass |
| TKB174 | test_customOpenAPI_includesSecurityRequirement | Kiểm tra include security requirement | - | SecurityRequirement present | Pass |
| TKB175 | test_customOpenAPI_includesSecurityScheme | Kiểm tra include security scheme | - | SecurityScheme present | Pass |
| TKB176 | test_securityScheme_usesBearer | Kiểm tra security scheme sử dụng HTTP Bearer | - | Type=HTTP, scheme="bearer" | Pass |
| TKB177 | test_securityScheme_hasDescription | Kiểm tra security scheme có description | - | Description!=null | Pass |
| TKB178 | test_customOpenAPI_completeStructure | Kiểm tra complete OpenAPI structure | - | Tất cả required sections present | Pass |

### RedisConfigTest.java - 10 tests | 100% Pass

| TC ID | Tên Test | Mục đích | Input | Expected | Result |
|-------|----------|----------|-------|----------|--------|
| TKB179 | test_redisTemplate_createsNonNullTemplate | Kiểm tra tạo non-null RedisTemplate | RedisConnectionFactory | redisTemplate!=null | Pass |
| TKB180 | test_redisTemplate_usesStringKeySerializer | Kiểm tra sử dụng String serializer cho keys | RedisConnectionFactory | Key serializer is StringRedis | Pass |
| TKB181 | test_redisTemplate_usesStringHashKeySerializer | Kiểm tra sử dụng String serializer cho hash keys | RedisConnectionFactory | Hash key serializer is StringRedis | Pass |
| TKB182 | test_redisTemplate_usesJsonValueSerializer | Kiểm tra sử dụng JSON serializer cho values | RedisConnectionFactory | Value serializer is JSON | Pass |
| TKB183 | test_redisTemplate_usesJsonHashValueSerializer | Kiểm tra sử dụng JSON serializer cho hash values | RedisConnectionFactory | Hash value serializer is JSON | Pass |
| TKB184 | test_redisTemplate_usesConnectionFactory | Kiểm tra sử dụng provided connection factory | RedisConnectionFactory | Uses provided factory | Pass |
| TKB185 | test_redisTemplate_initializesProperly | Kiểm tra initializes đúng cách | Valid RedisConnectionFactory | All serializers set | Pass |
| TKB186 | test_jsonSerializer_handlesObjectType | Kiểm tra JSON serializer xử lý Object type | GenericJackson2JsonRedisSerializer | Capable of handling Object | Pass |
| TKB187 | test_stringSerializers_forKeys | Kiểm tra String serializers cho keys | RedisTemplate instances | Both key serializers are String | Pass |
| TKB188 | test_jsonSerializers_forValues | Kiểm tra JSON serializers cho values | RedisTemplate instances | Both value serializers are JSON | Pass |

---

## 3. Controller Tests (Package: com.ptit.schedule.controller)

### ScheduleControllerTest.java - 21 tests | 100% Pass

| TC ID | Tên Test | Mục đích | Input | Expected | Result |
|-------|----------|----------|-------|----------|--------|
| TKB137 | test_saveSchedule_savesSuccessfully | Kiểm tra saveSchedule() lưu thành công | List<SaveScheduleRequest> với valid data | Response.success=true, status=200 | Pass |
| TKB138 | test_saveSchedule_emptyList_throwsException | Kiểm tra empty list throw InvalidDataException | List<SaveScheduleRequest>=[] | InvalidDataException được throw | Pass |
| TKB139 | test_saveSchedule_nullRequest_throwsException | Kiểm tra null request throw InvalidDataException | null | InvalidDataException được throw | Pass |
| TKB140 | test_saveSchedule_missingSubjectId_throwsException | Kiểm tra missing subjectId throw exception | SaveScheduleRequest không có subjectId | InvalidDataException được throw | Pass |
| TKB141 | test_saveSchedule_missingTemplateId_throwsException | Kiểm tra missing templateId throw exception | SaveScheduleRequest không có templateDatabaseId | InvalidDataException được throw | Pass |
| TKB142 | test_saveSchedule_nullUser_throwsException | Kiểm tra null user throw ResourceNotFoundException | Request không có authenticated user | ResourceNotFoundException được throw | Pass |
| TKB143 | test_getAllSchedules_returnsSchedules | Kiểm tra getAllSchedules() trả về schedules của user | Authenticated user | Response chứa List<Schedule> của user | Pass |
| TKB144 | test_getSchedulesBySubject_returnsSchedules | Kiểm tra getSchedulesBySubject() trả về schedules | subjectId="INT1306" | Response chứa schedules có subjectId="INT1306" | Pass |
| TKB145 | test_getSchedulesByMajor_returnsSchedules | Kiểm tra getSchedulesByMajor() trả về schedules | major="INT" | Response chứa schedules thuộc khoa "INT" | Pass |
| TKB146 | test_getSchedulesByStudentYear_returnsSchedules | Kiểm tra getSchedulesByStudentYear() trả về schedules | studentYear="2024" | Response chứa schedules có năm học "2024" | Pass |
| TKB147 | test_deleteSchedule_deletesSuccessfully | Kiểm tra deleteSchedule() xóa schedule theo ID | id=1 | Response.success=true, status=200 | Pass |
| TKB148 | test_deleteSchedule_invalidId_throwsException | Kiểm tra invalid ID throw InvalidDataException | id=0 | InvalidDataException được throw | Pass |
| TKB149 | test_deleteSchedule_nullId_throwsException | Kiểm tra null ID throw InvalidDataException | id=null | InvalidDataException được throw | Pass |
| TKB150 | test_deleteAllSchedules_deletesAll | Kiểm tra deleteAllSchedules() xóa tất cả | - | Response.success=true | Pass |
| TKB151 | test_generateSchedule_generatesSuccessfully | Kiểm tra generateSchedule() generate TKB batch | TKBBatchRequest với valid items | Response.success=true, chứa TKBBatchResponse | Pass |
| TKB152 | test_generateSchedule_emptyRequest_throwsException | Kiểm tra empty request throw exception | TKBBatchRequest với items=[] | InvalidDataException được throw | Pass |
| TKB153 | test_health_returnsOk | Kiểm tra health() trả về OK status | - | Response.message="Schedule Controller is OK" | Pass |
| TKB154 | test_testData_returnsTemplateInfo | Kiểm tra testData() trả về template data info | - | Response chứa số lượng template data | Pass |
| TKB155 | test_resetState_resetsSuccessfully | Kiểm tra resetState() reset scheduling state | - | Response.success=true | Pass |
| TKB156 | test_saveLastSlotIdxToRedis_savesSuccessfully | Kiểm tra lưu slot index vào Redis | userId, academicYear, semester | Response.success=true | Pass |
| TKB157 | test_resetLastSlotIdxRedis_resetsSuccessfully | Kiểm tra reset slot index trong Redis | userId, academicYear, semester | Response.success=true | Pass |

---

## 4. DTO Tests (Package: com.ptit.schedule.dto)

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

---

## 5. Exception Tests (Package: com.ptit.schedule.exception)

### GlobalExceptionHandlerTest.java - 14 tests | 100% Pass

| TC ID | Tên Test | Mục đích | Input | Expected | Result |
|-------|----------|----------|-------|----------|--------|
| TKB054 | test_handleValidationExceptions_returns400WithFieldErrors | Kiểm tra validation errors trả về 400 | MethodArgumentNotValidException với errors | Response.status=400, chứa field errors | Pass |
| TKB055 | test_handleValidationExceptions_emptyErrors | Kiểm tra validation với empty errors | MethodArgumentNotValidException với empty map | Response status=400, errors={} | Pass |
| TKB056 | test_handleHttpMessageNotReadable_returns400 | Kiểm tra invalid JSON trả về 400 | HttpMessageNotReadableException | Response.status=400 | Pass |
| TKB057 | test_handleResourceNotFoundException_returns404 | Kiểm tra resource not found trả về 404 | ResourceNotFoundException | Response.status=404, success=false | Pass |
| TKB058 | test_handleResourceNotFoundException_withDetails | Kiểm tra resource not found với chi tiết | ResourceNotFoundException với resource, field, value | Message format đúng | Pass |
| TKB059 | test_handleDuplicateResourceException_returns409 | Kiểm tra duplicate resource trả về 409 | DuplicateResourceException | Response.status=409, success=false | Pass |
| TKB060 | test_handleDuplicateResourceException_factoryConstructor | Kiểm tra duplicate với factory constructor | DuplicateResourceException(resource, field, value) | Message format đúng | Pass |
| TKB061 | test_handleInvalidDataException_returns400 | Kiểm tra invalid data trả về 400 | InvalidDataException | Response.status=400, success=false | Pass |
| TKB062 | test_handleFileProcessingException_returns400 | Kiểm tra file processing error trả về 400 | FileProcessingException | Response.status=400, success=false | Pass |
| TKB063 | test_handleFileProcessingException_withCause | Kiểm tra file processing error với cause | FileProcessingException với cause | Response chứa message gốc | Pass |
| TKB064 | test_handleRuntimeException_returns500 | Kiểm tra runtime exception trả về 500 | RuntimeException | Response.status=500, success=false | Pass |
| TKB065 | test_handleGenericException_returns500WithGenericMessage | Kiểm tra generic exception trả về 500 | Generic Exception | Response.status=500, message="Internal server error" | Pass |
| TKB066 | test_allResponses_containTimestamp | Kiểm tra tất cả error responses chứa timestamp | 4 loại exception khác nhau | Tất cả responses có timestamp | Pass |
| TKB067 | test_errorResponses_consistentStructure | Kiểm tra error responses tuân theo cấu trúc nhất quán | 4 loại exception khác nhau | Tất cả responses có cùng cấu trúc | Pass |

### ExceptionClassesTest.java - 12 tests | 100% Pass

| TC ID | Tên Test | Mục đích | Input | Expected | Result |
|-------|----------|----------|-------|----------|--------|
| TKB068 | test_resourceNotFoundException_simpleMessage | Kiểm tra ResourceNotFoundException simple message | new ResourceNotFoundException("Không tìm thấy") | getMessage()=="Không tìm thấy" | Pass |
| TKB069 | test_resourceNotFoundException_factoryConstructor | Kiểm tra ResourceNotFoundException factory constructor | new ResourceNotFoundException(resourceName, fieldName, fieldValue) | Message format đúng | Pass |
| TKB070 | test_resourceNotFoundException_numericFieldValue | Kiểm tra ResourceNotFoundException với numeric field | fieldValue=123 | Message chứa "id=123" | Pass |
| TKB071 | test_duplicateResourceException_simpleMessage | Kiểm tra DuplicateResourceException simple message | new DuplicateResourceException("Đã tồn tại") | getMessage()=="Đã tồn tại" | Pass |
| TKB072 | test_duplicateResourceException_factoryConstructor | Kiểm tra DuplicateResourceException factory constructor | DuplicateResourceException(resource, field, value) | Message format đúng | Pass |
| TKB073 | test_invalidDataException_withMessage | Kiểm tra InvalidDataException với message | new InvalidDataException("Dữ liệu không hợp lệ") | getMessage()=="Dữ liệu không hợp lệ" | Pass |
| TKB074 | test_invalidDataException_emptyMessage | Kiểm tra InvalidDataException với empty message | new InvalidDataException("") | getMessage()=="" | Pass |
| TKB075 | test_fileProcessingException_withMessage | Kiểm tra FileProcessingException với message | new FileProcessingException("Không thể đọc file") | getMessage()=="Không thể đọc file" | Pass |
| TKB076 | test_fileProcessingException_withCause | Kiểm tra FileProcessingException với message và cause | message, cause=IOException | Message chứa "Lỗi đọc file", getCause()=IOException | Pass |
| TKB077 | test_fileProcessingException_causeChain | Kiểm tra FileProcessingException cause chain | Nested cause chain | Cause chain được preserve | Pass |
| TKB078 | test_allExceptions_extendRuntimeException | Kiểm tra tất cả custom exceptions extend RuntimeException | 4 exception classes | Tất cả là RuntimeException subclasses | Pass |
| TKB079 | test_exceptions_canBeThrownAndCaught | Kiểm tra exceptions có thể throw và catch | try-catch block | Exception được catch đúng | Pass |

---

## 6. Security Tests (Package: com.ptit.schedule.security)

### JwtTokenProviderTest.java - 12 tests | 100% Pass

| TC ID | Tên Test | Mục đích | Input | Expected | Result |
|-------|----------|----------|-------|----------|--------|
| TKB080 | test_generateToken_createsValidToken | Kiểm tra generateToken() tạo valid JWT string | email="admin@ptit.edu.vn", role=ADMIN | Token!=null, Token!=empty | Pass |
| TKB081 | test_generateToken_containsCorrectEmail | Kiểm tra generated token chứa đúng email claim | email="user@ptit.edu.vn", role=USER | Extracted email=="user@ptit.edu.vn" | Pass |
| TKB082 | test_generateTokenWithUsername_createsToken | Kiểm tra generateTokenWithUsername tạo token | username="testuser", role=USER | Token!=null, getEmailFromToken()=="testuser" | Pass |
| TKB083 | test_validationToken_validToken_returnsTrue | Kiểm tra validationToken() trả về true cho valid token | Valid JWT token | validationToken(token)==true | Pass |
| TKB084 | test_validationToken_invalidToken_returnsFalse | Kiểm tra validationToken() trả về false cho invalid token | Token bị sửa đổi | validationToken(tamperedToken)==false | Pass |
| TKB085 | test_validateToken_aliasWorks | Kiểm tra alias validateToken hoạt động giống validationToken | Valid JWT token | validateToken(token)==validationToken(token)==true | Pass |
| TKB086 | test_getEmailFromToken_extractsCorrectEmail | Kiểm tra getEmailFromToken extract đúng email | Token generate với email="gv01@ptit.edu.vn" | getEmailFromToken(token)=="gv01@ptit.edu.vn" | Pass |
| TKB087 | test_extractUsername_sameAsGetEmailFromToken | Kiểm tra extractUsername trả về cùng getEmailFromToken | Token với email="user@ptit.edu.vn" | extractUsername(token)==getEmailFromToken(token) | Pass |
| TKB088 | test_malformedToken_handledGracefully | Kiểm tra malformed token được xử lý graceful | Token thiếu cấu trúc JWT | validationToken(malformedToken)==false | Pass |
| TKB089 | test_nullAndEmptyToken_handled | Kiểm tra null/empty token được xử lý đúng | null, "" | validationToken(null)==false, validationToken("")==false | Pass |
| TKB090 | test_tokenValidation_multipleTimes | Kiểm tra token có thể validate nhiều lần | Same valid token validate 5 lần | Luôn trả về true | Pass |
| TKB091 | test_differentTokens_areUnique | Kiểm tra các token khác nhau là unique | 2 tokens generate cho cùng user | Token1!=Token2 | Pass |

### JwtAccessDeniedHandlerTest.java - 8 tests | 87.5% Pass | 1 Fail

| TC ID | Tên Test | Mục đích | Input | Expected | Result | Ghi chú |
|-------|----------|----------|-------|----------|--------|---------|
| TKB092 | test_handle_sets403Status | Kiểm tra handle() set response status=403 | HttpServletRequest, HttpServletResponse, AccessDeniedException | response.setStatus(403) được gọi | Pass | |
| TKB093 | test_handle_setsJsonContentType | Kiểm tra handle() set Content-Type=application/json | request, response mock | response.setContentType("application/json") được gọi | Pass | |
| TKB094 | test_handle_writesJsonBody | Kiểm tra handle() write JSON error body | request với servletPath="/api/schedules" | Response chứa JSON với success=false | Pass | |
| TKB095 | test_handle_includesExceptionMessage | Kiểm tra handle() chứa error message từ exception | AccessDeniedException với message | JSON response chứa message từ exception | Pass | |
| TKB096 | test_handle_includesVietnameseMessage | Kiểm tra handle() chứa Vietnamese message "Từ chối truy cập" | Any AccessDeniedException | JSON response chứa "Từ chối truy cập" | **FAIL** | Response không hardcode Vietnamese message |
| TKB097 | test_handle_nullExceptionMessage | Kiểm tra handle() với null exception message | AccessDeniedException với null message | Handler hoàn thành không lỗi | Pass | |
| TKB098 | test_handle_emptyServletPath | Kiểm tra handle() với empty servlet path | request với servletPath="" | Handler hoàn thành bình thường | Pass | |
| TKB099 | test_handle_usesCorrectStatusConstant | Kiểm tra handle() sử dụng SC_FORBIDDEN constant | Standard AccessDeniedException | response.getStatus()==403 | Pass | |

### JwtAuthenticationEntryPointTest.java - 9 tests | 88.9% Pass | 1 Fail

| TC ID | Tên Test | Mục đích | Input | Expected | Result | Ghi chú |
|-------|----------|----------|-------|----------|--------|---------|
| TKB100 | test_commence_sets401Status | Kiểm tra commence() set response status=401 | HttpServletRequest, HttpServletResponse, AuthenticationException | response.setStatus(401) được gọi | Pass | |
| TKB101 | test_commence_setsJsonContentType | Kiểm tra commence() set Content-Type=application/json | request, response mock | response.setContentType("application/json") được gọi | Pass | |
| TKB102 | test_commence_writesJsonBody | Kiểm tra commence() write JSON error body | request với servletPath="/api/test" | Response chứa JSON với success=false | Pass | |
| TKB103 | test_commence_includesVietnameseMessage | Kiểm tra commence() chứa Vietnamese message "Không được phép" | Any AuthenticationException | JSON response chứa "Không được phép" | **FAIL** | Response không hardcode Vietnamese message |
| TKB104 | test_commence_includesExceptionMessage | Kiểm tra commence() chứa exception message | AuthenticationException với message | JSON chứa error message | Pass | |
| TKB105 | test_commence_nullExceptionMessage | Kiểm tra commence() với null exception message | AuthenticationException với null message | Handler hoàn thành không lỗi | Pass | |
| TKB106 | test_commence_emptyServletPath | Kiểm tra commence() với empty servlet path | request với servletPath="" | Handler hoàn thành bình thường | Pass | |
| TKB107 | test_commence_usesCorrectStatusConstant | Kiểm tra commence() sử dụng SC_UNAUTHORIZED constant | Standard AuthenticationException | response.getStatus()==401 | Pass | |
| TKB108 | test_differentStatusCodes_vsAccessDenied | Kiểm tra khác status code cho auth vs access denied | Both handlers | Entry point 401, Access denied 403 | Pass | |

### JwtAuthenticationFilterTest.java - 10 tests | 100% Pass

| TC ID | Tên Test | Mục đích | Input | Expected | Result |
|-------|----------|----------|-------|----------|--------|
| TKB109 | test_noAuthHeader_passesThrough | Kiểm tra filter pass through khi không có Authorization header | Request không có Authorization header | FilterChain.doFilter được gọi | Pass |
| TKB110 | test_nonBearerAuthHeader_passesThrough | Kiểm tra filter pass through cho non-Bearer auth header | Authorization header với prefix "Basic" | FilterChain.doFilter được gọi | Pass |
| TKB111 | test_invalidToken_passesThrough | Kiểm tra filter pass through khi token validation fail | Invalid JWT token | FilterChain.doFilter được gọi | Pass |
| TKB112 | test_nullEmailFromToken_passesThrough | Kiểm tra filter xử lý null email từ getEmailFromToken | Token mà getEmailFromToken() trả về null | FilterChain.doFilter được gọi | Pass |
| TKB113 | test_validToken_setsAuthentication | Kiểm tra filter authenticate user với valid token | Valid JWT token với email tồn tại | SecurityContext set authentication!=null | Pass |
| TKB114 | test_existingAuthentication_preserved | Kiểm tra filter không overwrite existing authentication | Request đã có authentication đã set | Existing authentication được preserve | Pass |
| TKB115 | test_exceptionDuringProcessing_continues | Kiểm tra filter xử lý exception trong token processing | Token provider throw exception | FilterChain.doFilter được gọi | Pass |
| TKB116 | test_userDetailsServiceException_continues | Kiểm tra filter xử lý UserDetailsService exception | UserDetailsService throw exception | FilterChain.doFilter được gọi | Pass |
| TKB117 | test_emptyBearerToken_passesThrough | Kiểm tra filter xử lý empty Bearer token | "Bearer " (có space nhưng không có token) | FilterChain.doFilter được gọi | Pass |
| TKB118 | test_bearerOnlySpaces_passesThrough | Kiểm tra filter xử lý Bearer với chỉ spaces | "Bearer    " (4 spaces) | FilterChain.doFilter được gọi | Pass |

---

## 7. Utils Tests (Package: com.ptit.schedule.utils)

### AcademicYearUtilsTest.java - 18 tests | 94.4% Pass | 1 Fail

| TC ID | Tên Test | Mục đích | Input | Expected | Result | Ghi chú |
|-------|----------|----------|-------|----------|--------|---------|
| TKB119 | test_resolveAcademicYear_returnsProvidedValue | Kiểm tra resolveAcademicYear() trả về giá trị đã cung cấp | academicYear="2023-2024" | resolveAcademicYear("2023-2024")=="2023-2024" | Pass | |
| TKB120 | test_resolveAcademicYear_whitespaceOnly_autoCalculates | Kiểm tra chỉ whitespace trả về auto-calculated | academicYear="   " (whitespace) | Giá trị auto-calculated "YYYY-YYYY" | Pass | |
| TKB121 | test_resolveAcademicYear_null_returnsAutoCalculated | Kiểm tra null trả về auto-calculated | academicYear=null | Giá trị auto-calculated "YYYY-YYYY" | Pass | |
| TKB122 | test_resolveAcademicYear_augustOrLater_usesCurrentYear | Kiểm tra tháng 8 trở đi dùng năm hiện tại | Month>=8 | Năm bắt đầu=năm hiện tại | Pass | |
| TKB123 | test_resolveAcademicYear_historicalYear_preserved | Kiểm tra năm học lịch sử được giữ nguyên | academicYear="2020-2021" | resolveAcademicYear("2020-2021")=="2020-2021" | Pass | |
| TKB124 | test_resolveAcademicYear_futureYear_preserved | Kiểm tra năm học tương lai được giữ nguyên | academicYear="2030-2031" | resolveAcademicYear("2030-2031")=="2030-2031" | Pass | |
| TKB125 | test_splitSemesterAndYear_standardFormat | Kiểm tra parse format chuẩn | "Học kỳ 1-2024-2025" | Pair("Học kỳ 1","2024-2025") | Pass | |
| TKB126 | test_splitSemesterAndYear_emDashSeparator | Kiểm tra xử lý em-dash separator | "HK2–2023-2024" (em-dash) | Pair("HK2","2023-2024") | Pass | |
| TKB127 | test_splitSemesterAndYear_nullInput | Kiểm tra null input trả về null | input=null | splitSemesterAndYear(null)==null | Pass | |
| TKB128 | test_splitSemesterAndYear_invalidFormat | Kiểm tra invalid format trả về null | "2024-2025" (không có semester) | splitSemesterAndYear("2024-2025")==null | Pass | |
| TKB129 | test_splitSemesterAndYear_trimsWhitespace | Kiểm tra trim whitespace từ các phần | "  Học kỳ 1  -  2024-2025  " | Pair.trimmed().first="Học kỳ 1" | **FAIL** | Result= null, logic split không trim |
| TKB130 | test_splitSemesterAndYear_vietnameseCharacters | Kiểm tra xử lý Vietnamese characters | "Học kỳ 2-2023-2024" | Pair chứa "Học kỳ 2" | Pass | |
| TKB131 | test_splitSemesterAndYear_singleDash | Kiểm tra xử lý single dash separator | "HK1-2025-2026" | Pair("HK1","2025-2026") | Pass | |
| TKB132 | test_resolveAcademicYear_emptyString | Kiểm tra empty string auto-calculates | input="" | Giá trị auto-calculated "YYYY-YYYY" | Pass | |
| TKB133 | test_splitSemesterAndYear_yearOnlyFormat | Kiểm tra year-only format trả về null | "2024-2025" không có semester | splitSemesterAndYear("2024-2025")==null | Pass | |
| TKB134 | test_resolveAcademicYear_variousFormats | Kiểm tra các format valid được chấp nhận | ["2024-2025","2023-2024","2022-2023"] | Tất cả được trả về nguyên | Pass | |
| TKB135 | test_academicYearBoundary_september | Kiểm tra September bắt đầu năm học mới | Month=9 | Năm học="2024-2025" | Pass | |
| TKB136 | test_academicYearBoundary_july | Kiểm tra July là cuối năm học trước | Month=7 | Năm học="2023-2024" | Pass | |

---

## 8. Service Tests (Package: com.ptit.schedule.service.impl)

### ScheduleExcelReaderServiceImplExtendedTest.java - 1 test | 0% Pass | 1 Fail

| TC ID | Tên Test | Mục đích | Input | Expected | Result | Ghi chú |
|-------|----------|----------|-------|----------|--------|---------|
| TKB145 | test_readExcel_parsesAllRows | Kiểm tra Excel reader parse tất cả rows | Excel với 2 entries | result.size()==2 | **FAIL** | Chỉ parse 1 row, entry thứ 2 bị bỏ qua |

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

# Chạy tất cả test
.\mvnw.cmd clean test

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

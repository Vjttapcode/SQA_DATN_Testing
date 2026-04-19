# -*- coding: utf-8 -*-
"""Rewrite 14_System-Test CSV: white/gray-box phrasing -> black-box (user-visible)."""
import csv
from pathlib import Path

INP = Path(__file__).parent / "14_System-Test - Module lập lịch.csv"
OUT = INP  # overwrite in place

# Per test id: (col_muc_dich_idx3, col_ky_thuat_idx4, col_du_lieu_idx5, col_steps_idx6, col_expected_idx7)
# Columns: 0=id, 1=chuc nang, 2=muc dich, 3=ky thuat, 4=du lieu, 5=steps, 6=expected, 7=actual, 8=status, 9=note
# Wait - header: Mã, Chức năng, Mục đích, Kỹ thuật, Dữ liệu, Bước, Expected, Actual, Status, Note
# So indices 0-9

REWRITE: dict[str, tuple[str, str, str, str, str]] = {
    "LL-01": (
        "Danh sách năm học hiển thị đúng theo dữ liệu hệ thống",
        "Functional Testing",
        "Hệ thống đã có danh sách năm học",
        "Mở dropdown Chọn năm học",
        "Chỉ hiển thị các năm học có trong hệ thống (không rỗng bất thường)",
    ),
    "LL-03": (
        "Danh sách học kỳ chỉ thuộc năm học đang chọn",
        "Equivalence Partitioning",
        "Đã chọn năm học 2025-2026",
        "Mở dropdown Chọn học kỳ",
        "Không hiển thị học kỳ của năm học khác",
    ),
    "LL-05": (
        "Sau khi chọn học kỳ, danh sách Loại hệ đào tạo được làm mới",
        "Functional Testing",
        "Đã chọn năm học và học kỳ hợp lệ",
        "1. Mở dropdown học kỳ\n2. Chọn một học kỳ trong danh sách",
        "Dropdown Loại hệ đào tạo có thể chọn được và hiển thị các lựa chọn phù hợp (sau khi hệ thống tải xong)",
    ),
    "LL-06": (
        "Sau khi chọn học kỳ, có thể chọn khóa khi đã chọn hệ không phải Chung",
        "Functional Testing",
        "Đã chọn năm học và học kỳ hợp lệ",
        "1. Mở dropdown học kỳ\n2. Chọn một học kỳ\n3. Chọn Loại hệ đào tạo (ví dụ Chính quy)",
        "Dropdown Khóa được kích hoạt và sau khi chọn hệ có thể tải danh sách khóa (nếu có dữ liệu)",
    ),
    "LL-07": (
        "Hiển thị danh sách Loại hệ đào tạo và tùy chọn Chung",
        "Functional Testing",
        "Đã chọn học kỳ hợp lệ",
        "Mở dropdown Loại hệ đào tạo",
        "Danh sách hiển thị đúng với dữ liệu thực tế của học kỳ đã chọn và có mục Chung",
    ),
    "LL-08": (
        "Sau khi chọn Loại hệ đào tạo, danh sách khóa cập nhật",
        "Functional Testing",
        "Đã chọn học kỳ và năm học hợp lệ",
        "1. Mở dropdown Loại hệ đào tạo\n2. Chọn một hệ có trong danh sách",
        "Dropdown Khóa hiển thị danh sách tương ứng hoặc rỗng nếu không có khóa",
    ),
    "LL-10": (
        "Chọn hệ Chính quy (hoặc hệ thường) thì danh sách khóa hiển thị",
        "Functional Testing",
        "Đã chọn đủ năm học, học kỳ và Loại hệ đào tạo (không phải Chung)",
        "1. Chọn Loại hệ đào tạo Chính quy\n2. Quan sát dropdown Khóa",
        "Dropdown Khóa có dữ liệu hoặc trống đúng với tình huống, không lỗi giao diện",
    ),
    "LL-11": (
        "Dropdown khóa bị vô hiệu khi chọn hệ Chung",
        "Decision Table Testing",
        "Loại hệ đào tạo = Chung",
        "1. Chọn Chung ở Loại hệ đào tạo",
        "Dropdown Khóa không chọn được (disabled)",
    ),
    "LL-13": (
        "Sau khi chọn khóa, danh sách nhóm ngành hiển thị",
        "Functional Testing",
        "Đã chọn đủ năm học, học kỳ, hệ và khóa",
        "1. Chọn một khóa trong dropdown Khóa",
        "Dropdown Chọn ngành hiển thị các nhóm ngành tương ứng",
    ),
    "LL-17": (
        "Với hệ Chung, tải được danh sách môn học chung không cần chọn khóa và ngành",
        "Use Case Testing",
        "Loại hệ đào tạo = Chung, đã chọn năm học và học kỳ",
        "1. Bấm Tải môn học",
        "Bảng môn hiển thị danh sách môn hoặc thông báo rõ nếu không có dữ liệu",
    ),
    "LL-18": (
        "Tải môn theo nhóm ngành đã chọn",
        "Functional Testing",
        "Nhóm ngành ví dụ AT-CN-KH",
        "1. Chọn ngành trong dropdown\n2. Bấm Tải môn học",
        "Bảng môn hiển thị đúng các môn thuộc nhóm ngành đã chọn",
    ),
    "LL-19": (
        "Nhóm ngành dạng E-* vẫn tải môn bình thường",
        "Boundary Value Analysis",
        "Nhóm ngành có mã bắt đầu bằng E-",
        "1. Chọn nhóm ngành E-*\n2. Bấm Tải môn học",
        "Hệ thống tải được dữ liệu hoặc báo không có môn một cách nhất quán",
    ),
    "LL-23": (
        "Xóa hết ô Sĩ số/lớp rồi rời ô, giá trị về 0 hoặc mặc định an toàn",
        "Boundary Value Analysis",
        "Ô Sĩ số một lớp đang có số",
        "1. Xóa toàn bộ nội dung ô\n2. Tab hoặc click ra ngoài",
        "Ô hiển thị 0 hoặc giá trị mặc định, không lỗi",
    ),
    "LL-25": (
        "Có thể tick Gộp ngành khi cùng mã môn và nhiều ngành",
        "Decision Table Testing",
        "Bảng có ít nhất hai dòng cùng mã môn khác ngành",
        "1. Tìm môn có nhiều ngành\n2. Quan sát cột Gộp ngành",
        "Checkbox Gộp ngành có thể bật (không bị vô hiệu)",
    ),
    "LL-29": (
        "Tick Gộp ngành thì mở vùng cấu hình kết hợp ngành",
        "Functional Testing",
        "Đã tick Gộp ngành",
        "1. Tick ô Gộp ngành",
        "Hiển thị vùng mở rộng với Ngành 1 cố định và các lựa chọn ngành kết hợp",
    ),
    "LL-30": (
        "Chọn Ngành 2 và Ngành 3 cập nhật sĩ số tổng và ẩn dòng trùng hợp lý",
        "Functional Testing",
        "Đã bật Gộp ngành và có ngành phụ",
        "1. Chọn Ngành 2 và Ngành 3 trong vùng kết hợp",
        "Sĩ số tổng khớp tổng các ngành đã chọn, các dòng ngành con được ẩn đúng",
    ),
    "LL-31": (
        "Thêm một tổ hợp ngành khác",
        "Functional Testing",
        "Đang ở chế độ Gộp ngành",
        "1. Bấm Thêm kết hợp",
        "Xuất hiện thêm một khối cấu hình tổ hợp",
    ),
    "LL-32": (
        "Xóa một tổ hợp ngành",
        "Functional Testing",
        "Có ít nhất một tổ hợp",
        "1. Bấm Xóa trên một tổ hợp",
        "Tổ hợp bị xóa; nếu xóa hết thì tắt gộp ngành",
    ),
    "LL-33": (
        "Đổi Sĩ số một lớp trong tổ hợp ảnh hưởng khi Sinh TKB",
        "Boundary Value Analysis",
        "Giá trị 0 hoặc số lớn",
        "1. Sửa ô Sĩ số một lớp trong tổ hợp\n2. Sinh TKB",
        "Giá trị lưu đúng và số lớp sinh ra phản ánh thay đổi",
    ),
    "LL-34": (
        "Bỏ tick Gộp ngành khôi phục các dòng ngành đã ẩn",
        "State Transition Testing",
        "Đang gộp ngành và có dòng bị ẩn",
        "1. Bỏ tick Gộp ngành",
        "Các dòng ngành hiển thị lại đầy đủ",
    ),
    "LL-35": (
        "Tick Đăng ký chung khi cùng mã môn khác khóa",
        "Condition Testing",
        "Hai dòng cùng mã môn học khác khóa (hệ Chung)",
        "1. Tìm cặp dòng phù hợp\n2. Tick Đăng ký chung",
        "Hai dòng gộp thành một, sĩ số cộng lại",
    ),
    "LL-38": (
        "Bỏ tick Đăng ký chung khôi phục hai dòng và sĩ số",
        "State Transition Testing",
        "Đang gộp đăng ký chung",
        "1. Bỏ tick Đăng ký chung",
        "Hiển thị lại hai dòng như trước khi gộp",
    ),
    "LL-40": (
        "Không có dòng môn nào thì không thể Sinh TKB",
        "Boundary Value Analysis",
        "Bảng môn không còn dòng (đã xóa hết)",
        "1. Xóa hết các dòng môn\n2. Quan sát vùng Sinh TKB",
        "Không hiển thị vùng Sinh hoặc nút Sinh bị ẩn/vô hiệu",
    ),
    "LL-41": (
        "Sinh TKB từ bảng môn hợp lệ",
        "Functional Testing",
        "Bảng môn có dữ liệu hợp lệ",
        "1. Bấm Sinh Thời khóa biểu",
        "Có kết quả lịch hoặc thông báo rõ các môn không sinh được",
    ),
    "LL-44": (
        "Trong lúc Gán phòng hoặc Lưu, không bấm trùng Sinh",
        "State Transition Testing",
        "Đang thực hiện Gán phòng hoặc Lưu",
        "1. Trong lúc xử lý thử bấm Sinh TKB",
        "Nút Sinh vô hiệu hoặc không gây lỗi",
    ),
    "LL-45": (
        "Gán phòng cho kết quả đã sinh",
        "Functional Testing",
        "Đã có bảng kết quả sau Sinh",
        "1. Bấm Gán phòng",
        "Cột phòng trên bảng kết quả được điền đầy đủ theo khả năng hệ thống",
    ),
    "LL-46": (
        "Khi không đủ phòng, hiển thị cảnh báo cho người dùng",
        "Functional Testing",
        "Kết quả sinh có môn khó xếp phòng",
        "1. Bấm Gán phòng",
        "Hiển thị hộp thoại hoặc toast cảnh báo danh sách môn chưa có phòng phù hợp",
    ),
    "LL-47": (
        "Lưu thời khóa biểu thành công khi dữ liệu đủ",
        "Functional Testing",
        "Bảng kết quả đã có phòng và đủ thông tin",
        "1. Bấm Lưu thời khóa biểu",
        "Thông báo lưu thành công hoặc lỗi rõ ràng trên giao diện",
    ),
    "LL-48": (
        "Sau khi lưu, dữ liệu phòng được phản ánh nhất quán (nếu có)",
        "Functional Testing",
        "Đã lưu TKB thành công",
        "1. Lưu xong kiểm tra màn hình liên quan (ví dụ phòng học) nếu có",
        "Không crash; trạng thái phòng cập nhật đúng kỳ vọng nghiệp vụ",
    ),
    "LL-49": (
        "Sau khi lưu thành công, phiên làm việc ghi nhận bản sao kết quả (nếu có tính năng)",
        "Functional Testing",
        "Vừa lưu TKB thành công",
        "1. Tải lại trang hoặc kiểm tra khu vực kết quả đã lưu (nếu có)",
        "Người dùng vẫn xem lại được kết quả vừa lưu hoặc thông báo phù hợp",
    ),
    "LL-50": (
        "Xóa toàn bộ kết quả đã lưu và giải phóng phòng (nếu có chức năng)",
        "Functional Testing",
        "Có nhiều bản kết quả đã lưu",
        "1. Dùng chức năng xóa tất cả (nếu có trên giao diện)",
        "Dữ liệu được xóa theo xác nhận và phòng trở lại trạng thái phù hợp",
    ),
    "LL-51": (
        "Khi tải khóa thất bại, giao diện không crash",
        "Robustness Testing",
        "Môi trường mạng lỗi hoặc máy chủ không phản hồi khi tải khóa",
        "1. Chọn hệ đào tạo khi mạng lỗi\n2. Quan sát dropdown Khóa",
        "Trang không crash, dropdown khóa trống hoặc giữ trạng thái an toàn",
    ),
    "LL-53": (
        "Trong lúc tải môn, nút Tải môn báo trạng thái chờ",
        "State Transition Testing",
        "Đang gửi yêu cầu tải môn",
        "1. Bấm Tải môn học\n2. Quan sát ngay sau đó",
        "Nút hiển thị Đang tải hoặc tương đương và không bấm lặp gây lỗi",
    ),
    "LL-54": (
        "Trong lúc Sinh TKB, không bấm Lưu/Gán trùng",
        "State Transition Testing",
        "Đang Sinh TKB",
        "1. Bấm Sinh\n2. Trong lúc chờ thử bấm Lưu và Gán phòng",
        "Các nút Lưu và Gán vô hiệu hoặc không gây lỗi",
    ),
    "LL-56": (
        "Số lớp sinh ra khớp quy tắc sĩ số và sĩ số một lớp",
        "Equivalence Partitioning",
        "Chính quy, sĩ số 143, sĩ số một lớp 100",
        "1. Đặt đúng dữ liệu bảng môn\n2. Sinh TKB",
        "Số lớp hiển thị đúng (ví dụ 2 lớp cho ví dụ trên)",
    ),
    "LL-57": (
        "Cột ngành trên kết quả hiển thị đúng khi gộp nhiều ngành",
        "Functional Testing",
        "Đã gộp 2-3 ngành và Sinh TKB",
        "1. Sinh TKB\n2. Xem cột Ngành trên bảng kết quả",
        "Ngành hiển thị dạng gộp (ví dụ A-B-C) đúng với cấu hình",
    ),
    "LL-58": (
        "Dữ liệu lưu trên trình duyệt hỏng không làm crash trang",
        "Error Guessing",
        "Dữ liệu lưu cục bộ của trang Lập lịch bị sửa tay không hợp lệ",
        "1. Sửa tay dữ liệu lưu cục bộ (nếu tester có quyền)\n2. F5 tải lại trang",
        "Trang mở được với trạng thái mặc định an toàn",
    ),
    "LL-59": (
        "Sĩ số tổng trong tổ hợp ngành bằng tổng các ngành đã chọn",
        "Business Rule Testing",
        "Chọn đúng 2 ngành trong tổ hợp",
        "1. Chọn đủ ngành trong tổ hợp\n2. Xem ô sĩ số tổng",
        "Sĩ số tổng bằng tổng sĩ số từng ngành đã chọn",
    ),
    "LL-61": (
        "Gán phòng thành công hiển thị thông báo ngắn gọn",
        "Functional Testing",
        "Dữ liệu đủ để gán đủ phòng",
        "1. Bấm Gán phòng",
        "Toast hoặc thông báo thành công rõ ràng",
    ),
    "LL-62": (
        "Import file Excel hợp lệ kèm chọn học kỳ",
        "Functional Testing + File Validation",
        "File .xlsx đúng mẫu, học kỳ có trong hệ thống",
        "1. Chọn học kỳ\n2. Chọn file\n3. Xác nhận import",
        "Thông báo import thành công",
    ),
    "LL-63": (
        "Chưa chọn học kỳ thì không import được",
        "Boundary Value Analysis / Negative Testing",
        "File hợp lệ, chưa chọn học kỳ",
        "1. Chọn file\n2. Bấm xác nhận không chọn học kỳ",
        "Thông báo yêu cầu chọn học kỳ, không hoàn tất import",
    ),
    "LL-66": (
        "Tải file mẫu import từ giao diện",
        "Functional Testing",
        "Có nút tải file mẫu trên modal",
        "1. Mở modal Upload\n2. Bấm tải file mẫu",
        "Trình duyệt tải được file mẫu",
    ),
    "LL-68": (
        "Trong lúc đang import, không bấm trùng upload",
        "State Transition Testing",
        "Đang thực hiện import",
        "1. Bắt đầu import\n2. Thử bấm lại upload",
        "Nút upload vô hiệu hoặc không gửi trùng",
    ),
    "LL-69": (
        "Khi máy chủ lỗi, import báo lỗi rõ",
        "Error Guessing",
        "Máy chủ trả lỗi khi import",
        "1. Thực hiện import khi backend lỗi",
        "Toast hoặc thông báo lỗi, không crash",
    ),
    "LL-74": (
        "Khi không có học kỳ trong hệ thống, trang không crash",
        "Robustness Testing",
        "Hệ thống trả về danh sách học kỳ rỗng",
        "1. Mở trang Lập lịch",
        "Dropdown năm học/học kỳ trống hoặc rõ ràng, không crash",
    ),
    "LL-75": (
        "Lỗi mạng khi tải danh mục học kỳ không làm vỡ trang",
        "Robustness Testing",
        "Không có mạng hoặc máy chủ không phản hồi lúc vào trang",
        "1. Mở trang Lập lịch khi lỗi mạng",
        "Trang không crash, có thể hiển thị trống hoặc thông báo",
    ),
    "LL-76": (
        "Khi danh sách hệ đào tạo rỗng, vẫn có lựa chọn mặc định",
        "Negative Testing",
        "Hệ thống không trả thêm hệ đào tạo động",
        "1. Chọn năm và học kỳ\n2. Mở Loại hệ đào tạo",
        "Vẫn thấy các lựa chọn mặc định (ví dụ Chính quy, Đặc thù, Chung)",
    ),
    "LL-77": (
        "Dữ liệu hệ đào tạo bất thường không crash trang",
        "Robustness Testing",
        "Phản hồi máy chủ không đúng định dạng mong đợi",
        "1. Chọn năm và học kỳ\n2. Mở Loại hệ đào tạo",
        "Không crash, dropdown an toàn",
    ),
    "LL-78": (
        "Không có khóa thì không tải môn và nút Tải môn vô hiệu",
        "Negative Testing",
        "Danh sách khóa rỗng sau khi chọn hệ",
        "1. Chọn đủ bước đến khi khóa rỗng",
        "Nút Tải môn không dùng được",
    ),
    "LL-79": (
        "Modal import: lỗi tải học kỳ có thông báo và không import nhầm",
        "Robustness Testing",
        "Lỗi khi tải danh sách học kỳ trong modal",
        "1. Mở modal Upload\n2. Chọn file và xác nhận",
        "Thông báo lỗi rõ, không import khi thiếu học kỳ hợp lệ",
    ),
    "LL-80": (
        "Chưa chọn file mà bấm xác nhận không gây lỗi",
        "Negative Testing",
        "Chưa chọn file trên máy",
        "1. Mở modal\n2. Chọn học kỳ (nếu cần)\n3. Bấm xác nhận",
        "Không crash, không báo sai; modal giữ nguyên hoặc nhắc chọn file",
    ),
    "LL-82": (
        "File .XLSX (chữ hoa) vẫn được chấp nhận nếu đúng nội dung",
        "Functional Testing + File Validation",
        "Tên file DATA.XLSX",
        "1. Chọn file\n2. Chọn học kỳ\n3. Xác nhận",
        "Xử lý giống file .xlsx thường hoặc báo lỗi rõ từ hệ thống",
    ),
    "LL-83": (
        "File vượt dung lượng cho phép bị từ chối",
        "Boundary Value Analysis",
        "File lớn hơn giới hạn quy định trên giao diện",
        "1. Chọn file quá lớn\n2. Xác nhận",
        "Thông báo file quá lớn, không import",
    ),
    "LL-84": (
        "File rỗng không làm crash và không nên ghi nhận thành công",
        "Robustness Testing",
        "File 0 byte",
        "1. Chọn file rỗng\n2. Xác nhận",
        "Báo lỗi hoặc từ chối, UI ổn định",
    ),
    "LL-85": (
        "Kéo thả nhiều file chỉ xử lý file đầu tiên",
        "Negative Testing",
        "Hai file Excel hợp lệ",
        "1. Kéo thả hai file vào vùng drop\n2. Xác nhận",
        "Chỉ tên file đầu tiên được chọn xử lý",
    ),
    "LL-86": (
        "Kéo thả không có file không crash modal",
        "Robustness Testing",
        "Thao tác kéo thả rỗng",
        "1. Kéo vào vùng drop nhưng không thả file",
        "Không crash, không chọn file",
    ),
    "LL-87": (
        "File mẫu không tồn tại: báo lỗi tải",
        "Negative Testing",
        "Liên kết file mẫu lỗi 404 (môi trường test)",
        "1. Bấm tải file mẫu",
        "Thông báo không tải được, không crash",
    ),
    "LL-88": (
        "Import bị từ chối từ máy chủ hiển thị thông báo",
        "Negative Testing",
        "Máy chủ từ chối import",
        "1. Chọn file và học kỳ\n2. Xác nhận",
        "Toast hoặc thông báo lỗi theo nội dung hệ thống",
    ),
    "LL-89": (
        "Đóng modal trong lúc upload không làm treo giao diện",
        "State Transition Testing",
        "Đang upload",
        "1. Bắt đầu import\n2. Đóng modal",
        "Sau khi xử lý xong trạng thái nút và trang bình thường",
    ),
    "LL-90": (
        "Bấm xác nhận hai lần nhanh không gây lỗi nghiêm trọng",
        "Concurrency Testing",
        "File và học kỳ hợp lệ",
        "1. Double-click hoặc bấm xác nhận hai lần liên tiếp",
        "Một lần xử lý hoặc hệ thống ổn định, không crash",
    ),
    "LL-91": (
        "Import quá lâu không phản hồi có thông báo timeout (nếu có)",
        "Negative Testing",
        "Môi trường giả lập chờ quá lâu",
        "1. Import và chờ",
        "Thông báo lỗi timeout hoặc hủy an toàn",
    ),
    "LL-92": (
        "Ô sĩ số một lớp không nhận chữ lẫn trong số",
        "Negative Testing",
        "Dán chuỗi có chữ và số",
        "1. Dán vào ô\n2. Rời ô",
        "Chỉ còn chữ số hợp lệ hoặc giữ giá trị an toàn",
    ),
    "LL-93": (
        "Không nhập được số âm vào ô sĩ số một lớp",
        "Negative Testing",
        "Gõ -5",
        "1. Focus ô\n2. Gõ -5\n3. Rời ô",
        "Không lưu số âm hoặc hiển thị hợp lệ",
    ),
    "LL-94": (
        "Khoảng trắng trong ô số được xử lý an toàn",
        "Negative Testing",
        "Chỉ nhập khoảng trắng",
        "1. Xóa ô\n2. Nhập space\n3. Rời ô",
        "Về 0 hoặc mặc định, không crash",
    ),
    "LL-95": (
        "Sĩ số một lớp cực lớn được kiểm soát hoặc cảnh báo",
        "Stress Testing",
        "Nhập sĩ số một lớp rất lớn",
        "1. Nhập số lớn\n2. Sinh TKB",
        "Giới hạn hợp lý hoặc thông báo không hợp lệ",
    ),
    "LL-96": (
        "Sĩ số một lớp bằng 0 không nên sinh lịch sai",
        "Boundary Value Analysis",
        "Để trống ô rồi về 0",
        "1. Xóa ô sĩ số một lớp\n2. Rời ô\n3. Sinh TKB",
        "Chặn sinh hoặc báo lỗi rõ, không hiển thị kết quả sai",
    ),
    "LL-97": (
        "Sĩ số một lớp trong tổ hợp bằng 0 không sinh lịch sai",
        "Boundary Value Analysis",
        "Gộp ngành, sĩ số một lớp = 0",
        "1. Nhập 0\n2. Sinh TKB",
        "Chặn hoặc báo lỗi, không kết quả sai",
    ),
    "LL-98": (
        "Nhập số thập phân vào sĩ số một lớp trong tổ hợp",
        "Negative Testing",
        "Nhập 10.5",
        "1. Bật gộp ngành\n2. Nhập 10.5 vào sĩ số một lớp",
        "Hệ thống làm tròn hoặc từ chối nhất quán với quy tắc hiển thị",
    ),
    "LL-99": (
        "Môn chỉ một ngành không tick được Gộp ngành",
        "Negative Testing",
        "Môn chỉ có một dòng ngành",
        "1. Quan sát checkbox Gộp ngành",
        "Checkbox vô hiệu và có gợi ý",
    ),
    "LL-100": (
        "Chỉ chọn một ngành trong tổ hợp thì không tạo lịch từ tổ hợp đó",
        "Negative Testing",
        "Chỉ có Ngành 1, không chọn Ngành 2 và 3",
        "1. Bật gộp\n2. Sinh TKB",
        "Không sinh dòng từ tổ hợp chưa đủ ngành hoặc báo rõ",
    ),
    "LL-102": (
        "Dữ liệu không hợp lệ khi Lưu hiển thị thông báo lỗi đúng",
        "Negative Testing",
        "Tình huống lưu bị máy chủ từ chối (4xx)",
        "1. Bấm Lưu\n2. Đọc thông báo",
        "Thông báo lỗi rõ ràng cho người dùng",
    ),
    "LL-103": (
        "Lỗi phụ sau khi lưu không làm vỡ giao diện",
        "Robustness Testing",
        "Lưu chính thành công nhưng bước phụ lỗi",
        "1. Bấm Lưu\n2. Quan sát",
        "Thông báo thành công chính vẫn rõ, không crash",
    ),
    "LL-104": (
        "Hủy xác nhận xóa thì không mất dữ liệu",
        "Negative Testing",
        "Có chức năng xóa một kết quả đã lưu",
        "1. Bấm xóa\n2. Chọn Hủy",
        "Danh sách kết quả không đổi",
    ),
    "LL-105": (
        "Xóa một kết quả đã lưu cập nhật đúng màn hình",
        "Negative Testing",
        "Đang xem một kết quả đã lưu",
        "1. Xóa\n2. Quan sát bảng",
        "Bảng phản ánh đúng sau xóa hoặc thông báo phù hợp",
    ),
    "LL-106": (
        "Hủy xóa tất cả không đổi dữ liệu",
        "Negative Testing",
        "Có chức năng xóa tất cả",
        "1. Bấm xóa tất cả\n2. Hủy",
        "Dữ liệu giữ nguyên",
    ),
    "LL-107": (
        "Lỗi khi xóa tất cả có thông báo",
        "Error Guessing",
        "Môi trường gây lỗi khi xóa và giải phóng phòng",
        "1. Xác nhận xóa tất cả",
        "Toast lỗi, không crash",
    ),
    "LL-108": (
        "Đổi năm học hoặc học kỳ sau khi đã Sinh: kết quả trên màn không mất ngoài ý muốn",
        "State Transition Testing",
        "Đã có bảng kết quả Sinh",
        "1. Sinh xong\n2. Đổi năm học hoặc học kỳ\n3. Quan sát bảng kết quả",
        "Kết quả đã sinh vẫn hiển thị như trước khi đổi bộ lọc (theo thiết kế)",
    ),
    "LL-109": (
        "Sinh TKB nhiều lần liên tiếp không crash",
        "Stress Testing",
        "Dữ liệu hợp lệ",
        "1. Sinh 4-5 lần liên tiếp",
        "Mỗi lần cập nhật kết quả, không crash",
    ),
    "LL-110": (
        "Không có môn hoặc đang Gán phòng thì không bấm Sinh trùng",
        "Negative Testing",
        "Bảng trống hoặc đang gán phòng",
        "1. Quan sát nút Sinh",
        "Nút ẩn hoặc vô hiệu",
    ),
    "LL-111": (
        "Người dùng không phải admin vẫn dùng được Lập lịch nếu được cấp quyền",
        "Role-Based Access Testing",
        "Tài khoản vai trò người dùng thường",
        "1. Đăng nhập\n2. Vào Lập lịch\n3. Tải môn và Sinh",
        "Thực hiện được theo quyền được giao",
    ),
    "LL-112": (
        "Thao tác gộp ngành nhanh liên tục không crash",
        "Concurrency Testing",
        "Bật/tắt gộp, thêm xóa tổ hợp nhanh",
        "1. Tick/bỏ tick và thao tác combo nhanh",
        "Giao diện ổn định, trạng thái khớp checkbox",
    ),
    "LL-114": (
        "Sidebar thu gọn vẫn thấy icon và gợi ý tên mục",
        "Usability Testing",
        "Sidebar ở chế độ thu gọn",
        "1. Thu gọn sidebar\n2. Di chuột lên mục Lập lịch",
        "Hiện icon và tooltip hoặc nhãn khi hover",
    ),
    "LL-116": (
        "Nút Upload lịch mẫu rõ trạng thái khi đang xử lý",
        "GUI Testing",
        "Trước và trong khi upload",
        "1. Quan sát nút Upload\n2. Trong lúc upload",
        "Nút hiển thị đang xử lý và khó bấm trùng",
    ),
    "LL-125": (
        "Nút Sinh TKB đổi trạng thái khi chưa có môn hoặc đang xử lý",
        "GUI Testing",
        "Chưa có dòng môn hoặc đang Sinh",
        "1. Quan sát nút Sinh\n2. Trong lúc Sinh",
        "Nút vô hiệu hoặc có biểu tượng chờ",
    ),
    "LL-129": (
        "Khu vực cảnh báo môn không sinh được có tiêu đề và nút đóng",
        "GUI Testing",
        "Sinh TKB có môn thất bại",
        "1. Sinh để có danh sách cảnh báo\n2. Quan sát panel",
        "Panel màu cảnh báo, có nút đóng",
    ),
    "LL-130": (
        "Điều hướng bằng phím Tab qua các ô điều khiển",
        "Accessibility (keyboard)",
        "Trang Lập lịch",
        "1. Nhấn Tab lần lượt từ đầu trang",
        "Focus di chuyển hợp lý, không kẹt",
    ),
    "LL-133": (
        "Bảng kết quả đủ các cột theo thiết kế màn hình",
        "GUI Testing",
        "Đã có kết quả sau Sinh",
        "1. Cuộn ngang dọc bảng kết quả",
        "Đủ cột theo giao diện quy định",
    ),
    "LL-134": (
        "Sidebar thu gọn: hover mục Lập lịch hiện nhãn",
        "Usability Testing",
        "Sidebar đã thu gọn",
        "1. Thu sidebar\n2. Hover Lập lịch",
        "Hiển thị nhãn Lập lịch (tooltip hoặc tương đương)",
    ),
}


def integration_to_functional(tech: str) -> str:
    t = tech.strip()
    if t in ("Integration", "Integration Testing"):
        return "Functional Testing"
    return t


# (cụm gray/white-box, bản black-box) — dùng cho sanitize và đảo để đánh dấu ô đã sửa khi xuất Excel
BLACKBOX_REPLACEMENTS: list[tuple[str, str]] = [
    (
        "GET /semesters gọi cùng lúc với trang nên lỗi mạng không gọi được semesters thì cũng crash trang",
        "Lỗi mạng khi tải danh sách học kỳ khiến cả trang bị crash (không đạt kỳ vọng ổn định).",
    ),
    (
        "Chưa đến api do save-batch bị lỗi",
        "Lưu theo lô thất bại nên phần phòng chưa cập nhật.",
    ),
    (
        "Save-batch chưa trả được về lỗi 400",
        "Thông báo lỗi chưa phản ánh đúng lỗi nghiệp vụ (mong đợi 400).",
    ),
    (
        "API được gọi đúng param và trả về kết quả",
        "Dữ liệu trên màn hình cập nhật đúng theo thao tác",
    ),
    ("Đã get đúng param và gọi được API", "Dropdown Khóa hiển thị danh sách phù hợp"),
    ("API được gọi và trả về dữ liệu", "Bảng môn hiển thị danh sách dữ liệu"),
    ("Request gửi mã ngành không tách split", "Danh sách môn khớp nhóm ngành đã chọn"),
    (
        "Một số học kỳ API chưa có dữ liệu nhưng đang hiển thị có danh sách",
        "Một số học kỳ chưa có dữ liệu nhưng giao diện vẫn hiển thị có danh sách",
    ),
    ("Hiển thị chưa đúng dữ liệu API trả", "Hiển thị chưa khớp với dữ liệu thực tế"),
    ("Chưa gọi được API", "Thao tác lưu chưa hoàn tất, dữ liệu chưa cập nhật"),
    ("1. Chọn khóa khi API lỗi.", "1. Chọn khóa khi tải danh sách thất bại."),
    (
        "Đã hiển thị toast thành công, api trả về dữ liệu 200",
        "Đã hiển thị toast thành công; hệ thống ghi nhận xử lý thành công",
    ),
    ("api trả về dữ liệu 200", "hệ thống ghi nhận xử lý thành công"),
    ("Chỉ xử lý được 1 request", "Chỉ một lần import được xử lý (không nhân đôi)"),
    ("khi backend lỗi", "khi máy chủ xử lý lỗi"),
    ("Hệ thống trả về danh sách học kỳ rỗng", "Không có học kỳ trong hệ thống (danh sách rỗng)"),
    ("Hệ thống không trả thêm hệ đào tạo động", "Không có thêm lựa chọn hệ đào tạo động ngoài mặc định"),
    ("Load semesters", "Tải danh mục học kỳ"),
    ("Load program types", "Tải loại hình đào tạo"),
    ("Load class years", "Tải danh sách khóa"),
    (" không gọi API", " không hoàn tất thao tác"),
    ("không gọi API", "không hoàn tất thao tác"),
    (
        "Liên kết file mẫu lỗi 404 (môi trường test)",
        "Liên kết tải file mẫu không khả dụng (môi trường test)",
    ),
    (
        "Lỗi khi template file 404 vẫn download được file",
        "File mẫu không tồn tại nhưng vẫn tải được — chưa đúng kỳ vọng",
    ),
    (
        "Sai luồng thực thi, khi gửi một file rỗng lên vẫn ghi nhận file cả backend cả ui",
        "Sai luồng: gửi file rỗng vẫn được ghi nhận ở cả máy chủ và giao diện",
    ),
    ("Lỗi 500", "Hiển thị lỗi máy chủ tổng quát"),
    (
        "Thông báo lỗi chưa phản ánh đúng lỗi nghiệp vụ (mong đợi 400).",
        "Thông báo chưa phản ánh rõ lỗi dữ liệu đầu vào so với lỗi máy chủ.",
    ),
]


def sanitize_blackbox(text: str) -> str:
    """Thay cụm white/gray-box (API, HTTP, tên endpoint) bằng mô tả quan sát được trên UI."""
    if not text:
        return text
    s = text
    for a, b in BLACKBOX_REPLACEMENTS:
        s = s.replace(a, b)
    return s


def invert_sanitize_blackbox(text: str) -> str:
    """Đảo BLACKBOX_REPLACEMENTS (chuỗi mới -> cũ); thứ tự ưu tiên chuỗi dài trước."""
    if not text:
        return text
    s = text
    for old_gray, new_bb in sorted(BLACKBOX_REPLACEMENTS, key=lambda p: len(p[1]), reverse=True):
        s = s.replace(new_bb, old_gray)
    return s


def blackbox_sanitize_ll_row(row: list[str]) -> None:
    """Áp dụng sanitize cho các cột mô tả (bỏ qua mã, kỹ thuật, trạng thái)."""
    idx_text = (1, 2, 4, 5, 6, 7, 9)
    for j in idx_text:
        if j < len(row) and row[j]:
            row[j] = sanitize_blackbox(row[j])


def main() -> None:
    rows: list[list[str]] = []
    with INP.open("r", encoding="utf-8-sig", newline="") as f:
        reader = csv.reader(f)
        for row in reader:
            rows.append(row)

    out_rows: list[list[str]] = []
    for i, row in enumerate(rows):
        if i == 0 and len(row) > 0 and "QLND" in (row[0] or ""):
            row = list(row)
            row[0] = (row[0] or "").replace("Module QLND", "Module Lập lịch (TKB)")
            out_rows.append(row)
            continue
        if len(row) >= 7 and row[0] and row[0].startswith("LL-"):
            tid = row[0].strip()
            if tid in REWRITE:
                md, kt, dl, st, ex = REWRITE[tid]
                row = list(row)
                row[2] = md
                row[3] = integration_to_functional(kt)
                row[4] = dl
                row[5] = st
                row[6] = ex
            else:
                row = list(row)
                row[3] = integration_to_functional(row[3])
                # Light cleanup on common patterns in muc dich / du lieu
                if row[2]:
                    row[2] = (
                        row[2]
                        .replace("load từ API học kỳ", "theo dữ liệu hệ thống")
                        .replace("load từ API  /subjects/program-types", "theo dữ liệu hệ thống")
                    )
                if row[4] and "systemType" in row[4]:
                    row[4] = row[4].replace("systemType", "Loại hệ").replace("Chung", "Chung")
                if row[4] and "assigningRooms" in row[4]:
                    row[4] = "Đang hoặc vừa bấm Gán phòng"
                if row[4] and "loading true" in row[4].lower():
                    row[4] = "Đang gửi yêu cầu tải môn"
                if row[4] and "loading generate" in row[4].lower():
                    row[4] = "Đang Sinh TKB"
                if row[4] and "Import đang chạy" in row[4]:
                    row[4] = "Đang thực hiện import file"
                if row[4] and "importing" in row[4].lower() and "true" in row[4].lower():
                    row[4] = "Đang upload file trong modal"
                if row[4] and "importing=false hoặc true" in row[4]:
                    row[4] = "Trước và trong khi upload file"
                if row[4] and "batchRows" in row[4].lower():
                    row[4] = row[4].replace("batchRows = []", "Không còn dòng môn").replace(
                        "batchRows đang loading", "Đang Sinh TKB hoặc chưa có môn"
                    )
                if row[6] and "NotificationModal" in row[6]:
                    row[6] = row[6].replace(
                        "NotificationModal warning, toast dismiss",
                        "Hiển thị hộp thoại hoặc toast cảnh báo, có thể đóng",
                    )
                if row[6] and "toast.success" in row[6]:
                    row[6] = row[6].replace("toast.success một dòng", "Thông báo thành công ngắn gọn")
                if row[6] and "API" in row[6] and tid not in REWRITE:
                    row[6] = (
                        row[6]
                        .replace("API được gọi đúng param và trả về kết quả", "Dữ liệu trên màn hình cập nhật đúng")
                        .replace("không gọi API", "không hoàn tất thao tác")
                    )
                if row[5] and "Xem network" in row[5]:
                    row[5] = row[5].replace("2. Xem network.", "2. Quan sát dropdown Khóa có dữ liệu hoặc thông báo.")
            blackbox_sanitize_ll_row(row)
            out_rows.append(row)
        else:
            out_rows.append(row)

    with OUT.open("w", encoding="utf-8-sig", newline="") as f:
        w = csv.writer(f, lineterminator="\n")
        w.writerows(out_rows)

    print("OK rows", len(out_rows))


if __name__ == "__main__":
    main()

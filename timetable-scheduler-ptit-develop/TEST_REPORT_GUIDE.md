# Hướng dẫn tạo Maven HTML Test Report

## Tổng quan
Project này đã được cấu hình với JaCoCo để tạo báo cáo coverage. Thêm vào đó, chúng ta có thể tạo HTML test reports bằng Maven Surefire Report Plugin.

## Cách tạo HTML Test Report

### 1. Chạy tests và tạo báo cáo

```bash
# Chạy tất cả tests và generate báo cáo
mvn clean test surefire-report:report

# Hoặc
mvn clean verify
```

Sau khi chạy, báo cáo sẽ được tạo tại:
```
target/site/surefire-report.html
```

### 2. Xem báo cáo coverage (JaCoCo)

Báo cáo coverage được tạo tự động khi chạy `verify`:
```
target/site/jacoco/index.html
```

### 3. Tạo toàn bộ Maven Site

```bash
# Tạo toàn bộ site với tất cả báo cáo
mvn clean site
```

Site sẽ được tạo tại:
```
target/site/index.html
```

## Cấu hình plugins trong pom.xml

Project đã có cấu hình sau:

### JaCoCo Plugin
- Tích hợp sẵn với `spring-boot-maven-plugin`
- Tạo báo cáo tại `target/site/jacoco/`
- Tự động chạy khi gọi `mvn verify`

### Surefire Plugin (mặc định)
- Được sử dụng bởi Spring Boot
- Tạo báo cáo test tại `target/surefire-reports/`

## Quick Commands

```bash
# Chỉ chạy tests
mvn test

# Chạy tests và xem kết quả nhanh
mvn -q test

# Chạy tests với coverage
mvn clean verify

# Mở báo cáo HTML (trên Windows)
start target/site/surefire-report.html

# Hoặc dùng PowerShell
Invoke-Item target\site\surefire-report.html
```

## Tùy chỉnh báo cáo

Nếu muốn tùy chỉnh báo cáo, có thể thêm plugin vào pom.xml:

```xml
<reporting>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-report-plugin</artifactId>
            <version>3.5.2</version>
        </plugin>
    </plugins>
</reporting>
```

Sau đó chạy:
```bash
mvn site
```

@echo off
echo Compiling CsvToXlsxConverter...
cd /d "%~dp0"
cd ..

REM Compile with Maven
mvn clean compile -DskipTests

if %ERRORLEVEL% neq 0 (
    echo Compilation failed!
    pause
    exit /b 1
)

echo.
echo Running converter...
java -cp "target/classes;target/dependency/*" com.ptit.schedule.util.CsvToXlsxConverter "Unit Test - Lập lịch.csv" "Unit Test - Lập lịch.xlsx"

if %ERRORLEVEL% neq 0 (
    echo Conversion failed!
) else (
    echo.
    echo Conversion completed successfully!
    echo Output file: Unit Test - Lập lịch.xlsx
)

pause

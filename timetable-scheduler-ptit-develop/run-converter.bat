@echo off
echo Running CSV to XLSX Converter...
cd /d "%~dp0"

REM First, compile the project
echo Compiling...
mvn clean compile -DskipTests

if %ERRORLEVEL% neq 0 (
    echo Compilation failed!
    pause
    exit /b 1
)

echo.
echo Conversion complete! Check the project root for "Unit Test - Lap lich.xlsx"
echo.

REM Alternative: Use maven exec plugin
REM mvn exec:java -Dexec.mainClass="com.ptit.schedule.util.CsvToXlsxConverter" -Dexec.args="'Unit Test - Lập lịch.csv' 'Unit Test - Lập lịch.xlsx'"

pause

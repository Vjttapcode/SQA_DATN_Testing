@echo off
chcp 65001 >nul
echo Converting CSV to XLSX...

cd /d "%~dp0"

rem Find CSV file
for %%f in (*.csv) do (
    set CSV_FILE=%%f
    goto :found
)
:found

if "%CSV_FILE%"=="" (
    echo No CSV file found!
    pause
    exit /b 1
)

echo Found: %CSV_FILE%
set XLSX_FILE=%CSV_FILE:.csv=.xlsx%

rem Build classpath
call mvn -q dependency:build-classpath -Dmdep.outputFile=cp.txt
set /p CP=<cp.txt

rem Run converter
java -cp "target/classes;%CP%" com.ptit.schedule.util.RunConverter

if exist "%XLSX_FILE%" (
    echo.
    echo ✓ SUCCESS: %XLSX_FILE% created!
) else (
    echo.
    echo ✗ FAILED: Output file not found.
)

del cp.txt 2>nul
pause

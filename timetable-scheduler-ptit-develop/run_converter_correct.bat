cd /d "e:\MAU\New folder\timetable-scheduler-ptit-develop"

rem Build classpath from Maven dependencies
mvn -q dependency:build-classpath -Dmdep.outputFile=cp.txt

rem Build classpath string
set CP=
for /f "usebackq delims=" %%i in ("cp.txt") do set CP=%%i

rem Run converter
echo Running converter...
java -cp "target/classes;%CP%" com.ptit.schedule.util.RunConverter

rem Check result
if exist "Unit Test - L?p l?ch.xlsx" (
    echo.
    echo SUCCESS: XLSX file created!
) else (
    echo.
    echo FAILED: Output file not found.
)

pause

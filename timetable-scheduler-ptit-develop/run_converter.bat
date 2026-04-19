@echo off
chcp 65001 >nul
echo Finding CSV file...
for %%f in (*.csv) do (
    echo Found: %%f
    set CSV_FILE=%%f
    goto :found
)
:found
echo Converting %CSV_FILE% ...

set MAVEN_OPTS=-Dfile.encoding=UTF-8

rem Build classpath from Maven dependencies
mvn dependency:build-classpath -Dmdep.outputFile=classpath.txt -q
set /p CP=<classpath.txt
set CLASSPATH=target/classes;%CP%

java -cp "%CLASSPATH%" com.ptit.schedule.util.RunConverter
echo Done.
pause

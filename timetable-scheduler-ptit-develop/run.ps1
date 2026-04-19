cd "e:\MAU\New folder\timetable-scheduler-ptit-develop"
mvn -q dependency:build-classpath -Dmdep.outputFile=cp.txt
$cp = Get-Content cp.txt
java -cp "target/classes;$cp" com.ptit.schedule.util.RunConverter

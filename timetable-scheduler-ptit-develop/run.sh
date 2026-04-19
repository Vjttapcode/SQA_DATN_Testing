cd /d "e:\MAU\New folder\timetable-scheduler-ptit-develop"
call mvn -q dependency:build-classpath -Dmdep.pathFile=cp.txt
java -cp "target/classes;$(cat cp.txt | tr '\n' ';')" com.ptit.schedule.util.RunConverter
del cp.txt

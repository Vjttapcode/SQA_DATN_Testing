# Get Maven dependencies classpath
mvn dependency:build-classpath -Dmdep.createDependencyClasspath=false -Dmdep.outputFile=cp.txt -q
$cp = Get-Content cp.txt -Raw

# Build full classpath
$fullCp = "target/classes;" + $cp

Write-Host "Converting CSV to XLSX..." -ForegroundColor Cyan
Write-Host "Classpath length: $($fullCp.Length) chars" -ForegroundColor Yellow

java -cp $fullCp com.ptit.schedule.util.RunConverter

Remove-Item cp.txt -Force -ErrorAction SilentlyContinue

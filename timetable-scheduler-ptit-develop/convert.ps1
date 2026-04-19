# Build classpath using Maven
mvn -q dependency:build-classpath -Dmdep.outputFile=cp.txt
$classpath = "target/classes;" + (Get-Content cp.txt -Raw)

# Run converter
Write-Host "Running CSV to XLSX converter..." -ForegroundColor Cyan
java -cp $classpath com.ptit.schedule.util.RunConverter

# Cleanup
Remove-Item cp.txt -Force -ErrorAction SilentlyContinue

if (Test-Path "Unit Test - L?p l?ch.xlsx") {
    Write-Host "`n✓ Conversion successful! XLSX file created." -ForegroundColor Green
} else {
    Write-Host "`n✗ Conversion failed. Check errors above." -ForegroundColor Red
}

pause

# CSV to XLSX Converter Script

param(
    [string]$CsvFile = "Unit Test - Lập lịch.csv",
    [string]$XlsxFile = "Unit Test - Lập lịch.xlsx"
)

Write-Host "Converting CSV to XLSX..." -ForegroundColor Cyan
Write-Host "Input: $CsvFile"
Write-Host "Output: $XlsxFile"

# Check if ImportExcel module is available
if (Get-Module -ListAvailable -Name ImportExcel) {
    Write-Host "ImportExcel module found, using it..." -ForegroundColor Green

    $data = Import-Csv -Path $CsvFile -Delimiter ','

    # Get all properties (columns)
    $properties = $data[0].PSObject.Properties.Name

    # Create Excel file
    $excelPackage = $data | Export-Excel -Path $XlsxFile -WorksheetName "Test Cases" -AutoSize -AutoFilter -TableName "TestCases" -PassThru

    # Format header row
    $worksheet = $excelPackage.Workbook.Worksheets[1]
    $headerRange = $worksheet.Cells[1,1,1,$properties.Count]
    $headerRange.Style.Font.Bold = $true
    $headerRange.Style.Fill.PatternType = 'Solid'
    $headerRange.Style.Fill.BackgroundColor.SetColor([System.Drawing.Color]::LightGray)
    $headerRange.Style.HorizontalAlignment = 'Center'

    $excelPackage.Save()
    $excelPackage.Dispose()

    Write-Host "`nConversion completed successfully!" -ForegroundColor Green
    Write-Host "File: $XlsxFile"
}
else {
    Write-Host "ImportExcel module not found." -ForegroundColor Yellow
    Write-Host "Installing ImportExcel module..." -ForegroundColor Yellow

    try {
        Install-Module -Name ImportExcel -Scope CurrentUser -Force -AllowClobber
        Write-Host "Module installed. Please run this script again." -ForegroundColor Green
    }
    catch {
        Write-Host "Failed to install module: $_" -ForegroundColor Red
        Write-Host "`nAlternative: Creating HTML file that Excel can open" -ForegroundColor Yellow

        # Fallback: create HTML table
        $htmlFile = $XlsxFile.Replace(".xlsx", ".html")
        $lines = Get-Content $CsvFile

        $html = @"
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Test Cases</title>
    <style>
        table { border-collapse: collapse; width: 100%; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; font-weight: bold; }
        tr:nth-child(even) { background-color: #f9f9f9; }
    </style>
</head>
<body>
    <h2>Unit Test - Lập lịch</h2>
    <table>
"@

        for ($i = 0; $i -lt $lines.Count; $i++) {
            $line = $lines[$i]
            $cells = $line -split ','

            if ($i -eq 0) {
                $html += "      <tr>`n"
                foreach ($cell in $cells) {
                    $html += "        <th>$($cell.Trim())</th>`n"
                }
                $html += "      </tr>`n"
            }
            else {
                $html += "      <tr>`n"
                foreach ($cell in $cells) {
                    $html += "        <td>$($cell.Trim())</td>`n"
                }
                $html += "      </tr>`n"
            }
        }

        $html += @"
    </table>
</body>
</html>
"@

        $html | Out-File -FilePath $htmlFile -Encoding UTF8
        Write-Host "`nHTML file created: $htmlFile" -ForegroundColor Green
        Write-Host "Open this file in Excel (File -> Open -> select the HTML file)" -ForegroundColor Yellow
    }
}

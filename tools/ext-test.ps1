$ErrorActionPreference = "SilentlyContinue"
$txtDir = "C:\Users\JHOEL\Desktop\inventario-api\target\surefire-reports"
$files = Get-ChildItem -Path $txtDir -Filter "*.txt"
foreach ($f in $files) {
    $content = Get-Content -LiteralPath $f.FullName
    $markers = $content | Where-Object { $_ -match "Caused by|UnexpectedRollbackException|rollback-only|invalid input syntax|NOT NULL|jsonb|SQLException|ConstraintViolation|DataIntegrity|JdbcSQL|too long|UnexpectedRollback|marked as rollback|unique constraint|violates" }
    if (-not $markers) { continue }
    $caption = "===== " + $f.Name + " ====="
    Write-Host $caption
    Write-Host ("".PadRight($caption.Length, "="))
    for ($i = 0; $i -lt $content.Count; $i++) {
        $line = $content[$i]
        if ($line -match "Caused by|UnexpectedRollback|rollback-only|invalid input syntax|NOT NULL|jsonb|SQLException|ConstraintViolation|DataIntegrity|JdbcSQL|too long|marked as rollback|unique constraint|violates|JdbcSQLIntegrity") {
            Write-Host ("C$i> " + $line.Trim())
            for ($j = $i + 1; $j -lt [Math]::Min($i + 4, $content.Count); $j++) {
                $t = $content[$j].Trim()
                if ($t.Length -gt 0) { Write-Host ("    + " + $t) }
            }
        }
    }
    Write-Host ""
}
Write-Host "EXTRACTOR_FIN"

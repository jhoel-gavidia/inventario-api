$ErrorActionPreference = "Stop"
$reportRoot = "C:\Users\JHOEL\Desktop\inventario-api\target\surefire-reports"

Get-ChildItem -Path $reportRoot -Filter "*.txt" | ForEach-Object {
    $name = $_.Name
    $lines = Get-Content $_.FullName
    $hasMarks = $lines | Where-Object {
        $_ -match "Caused by|UnexpectedRollbackException|rollback-only|jsonb|invalid input syntax|NOT NULL|not-null|too long|DataIntegrityViolation|ConstraintViolation|JdbcSQL|SQLState"
    }
    if (-not $hasMarks) { return }

    Write-Host "============================"
    Write-Host ("FILE: " + $name)
    Write-Host "============================"
    $cap = 0
    for ($i = 0; $i -lt $lines.Count; $i++) {
        $t = $lines[$i].Trim()
        if ($t -match "Caused by|UnexpectedRollbackException|rollback-only|jsonb|invalid input syntax|NOT NULL|too long|DataIntegrityViolation|JdbcSQL|SQLState:$|marked as rollback|EntityManager cannot") {
            if ($cap -ge 12) { Write-Host "... (cap reached)"; break }
            $cap++
            $j = $i
            $window = 7
            Write-Host ("-- L" + $i + ": " + $t)
            for ($k = 1; $k -le $window; $k++) {
                if (($i + $k) -lt $lines.Count -and $lines[$i + $k].Trim().Length -gt 0) {
                    Write-Host ("     +L" + ($i + $k) + ": " + $lines[$i + $k].Trim())
                }
            }
        }
    }
    Write-Host ""
}
Write-Host "== done =="

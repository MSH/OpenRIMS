 powershell -command "((Get-Content -path .\%1 -Raw) -replace 'NO_AUTO_CREATE_USER','') | Set-Content -Path .\fixed_dump.sql"
@echo off
echo Checking DynamoDB Local tables using cURL...
echo.
curl -X POST http://localhost:8000 ^
-H "Content-Type: application/x-amz-json-1.0" ^
-H "X-Amz-Target: DynamoDB_20120810.ListTables" ^
-d "{}"
echo.
echo.
echo If you see table names above, DynamoDB Local is working!
pause
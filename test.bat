@echo off
echo Running Banking Transaction System Tests...
echo.

REM Check if Maven is available
where mvn >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo Maven not found in PATH. Please install Maven or use your IDE to run tests.
    echo.
    echo Alternative: Open the project in IntelliJ IDEA and run tests from there
    pause
    exit /b 1
)

echo Compiling and running tests...
mvn clean test

if %ERRORLEVEL% NEQ 0 (
    echo Tests failed!
) else (
    echo All tests passed successfully!
)

pause
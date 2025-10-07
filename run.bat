@echo off
echo Building and running Banking Transaction System...
echo.

REM Check if Maven is available
where mvn >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo Maven not found in PATH. Please install Maven or use your IDE to run the project.
    echo.
    echo Alternative: Open the project in IntelliJ IDEA and run Main.java
    pause
    exit /b 1
)

echo Compiling project...
mvn clean compile

if %ERRORLEVEL% NEQ 0 (
    echo Compilation failed!
    pause
    exit /b 1
)

echo.
echo Running Banking Transaction System...
echo.
mvn exec:java -Dexec.mainClass="org.example.Main"

pause
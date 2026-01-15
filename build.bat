@echo off
echo Building Wiggly JAR Game Launcher...
echo.

REM Check if Maven is installed
where mvn >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Maven is not installed or not in PATH
    echo Please install Maven from https://maven.apache.org/download.cgi
    pause
    exit /b 1
)

REM Check if Java is installed
where java >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java JDK 17 or higher
    pause
    exit /b 1
)

echo Maven and Java found!
echo.

REM Build the project
echo Running Maven build...
call mvn clean package

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ======================================
    echo BUILD SUCCESSFUL!
    echo ======================================
    echo.
    echo The executable JAR has been created at:
    echo target\wiggly-launcher.jar
    echo.
    echo To run the application:
    echo   java -jar target\wiggly-launcher.jar
    echo.
    echo Or simply double-click the JAR file in Windows Explorer
    echo.
) else (
    echo.
    echo ======================================
    echo BUILD FAILED!
    echo ======================================
    echo Please check the error messages above
    echo.
)

pause

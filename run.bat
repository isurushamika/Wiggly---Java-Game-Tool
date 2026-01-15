@echo off
echo Starting Wiggly JAR Game Launcher...
echo.

REM Check if the JAR file exists
if not exist "target\wiggly-launcher.jar" (
    echo ERROR: wiggly-launcher.jar not found!
    echo Please build the project first by running build.bat
    echo.
    pause
    exit /b 1
)

REM Check if Java is installed
where java >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java Runtime Environment (JRE) 17 or higher
    pause
    exit /b 1
)

REM Run the application
java -jar target\wiggly-launcher.jar

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo The application encountered an error.
    pause
)

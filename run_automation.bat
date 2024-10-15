@echo off

:: Start Appium in a new terminal window
echo Starting Appium in a new terminal window...
start cmd /k "gradlew.bat startAppium"
if %ERRORLEVEL% NEQ 0 (
    echo Failed to start Appium. Exiting.
    exit /b 1
)

:: Start the emulator in a new terminal window
echo Starting Emulator in a new terminal window...
start cmd /k "gradlew.bat startEmulator"
if %ERRORLEVEL% NEQ 0 (
    echo Failed to start the emulator. Exiting.
    exit /b 1
)

:: Wait for 30 seconds
echo Waiting for 30 seconds to allow services to start...
timeout /t 30

:: Run the automation script in a new terminal window
echo Running the automation script in a new terminal window...
start cmd /k "gradlew.bat runAutomationScript"
if %ERRORLEVEL% NEQ 0 (
    echo Automation tests failed. Exiting.
    exit /b 1
)

echo Automation complete.
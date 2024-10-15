@echo off
REM Load variables from local.env
for /F "tokens=1,2 delims==" %%i in (local.env) do (
    set "key=%%i"
    set "value=%%j"
    setlocal enabledelayedexpansion
    set "key=!key: =!"
    set "value=!value: =!"
    endlocal & set "%key%=%value%"
)

REM Check if device_UID is loaded correctly
if "%device_UID%"=="" (
    echo Failed to load device_UID from local.env. Exiting.
    exit /b 1
)

REM Start the emulator and save its PID
start /B gradlew startEmulator > logs\emulator.log 2>&1
set "EMULATOR_PID=%!"

REM Check if emulator started correctly
if "%EMULATOR_PID%"=="" (
    echo Failed to start emulator. Exiting.
    exit /b 1
)

REM Start Appium and save its PID
start /B gradlew startAppium > logs\appium.log 2>&1
set "APPIUM_PID=%!"

REM Check if Appium started correctly
if "%APPIUM_PID%"=="" (
    echo Failed to start Appium. Exiting.
    exit /b 1
)

REM Function to clean up processes (Emulator and Appium) when script finishes
:cleanup
echo Stopping Emulator (%device_UID%) and Appium (PID: %APPIUM_PID%)...

REM Check and stop the emulator using adb
adb devices | findstr "%device_UID%" > nul
if not errorlevel 1 (
    adb -s %device_UID% emu kill
    echo Emulator (%device_UID%) stopped via adb.
) else (
    echo Emulator was already stopped.
)

REM Check and stop Appium if it's still running
tasklist /FI "PID eq %APPIUM_PID%" | findstr "gradlew" > nul
if not errorlevel 1 (
    taskkill /PID %APPIUM_PID% /F
    echo Appium (PID: %APPIUM_PID%) has been stopped.
) else (
    echo Appium (PID: %APPIUM_PID%) was already stopped.
)

exit /b 0

REM Wait for Emulator and Appium to fully start before running tests
timeout /t 10 > nul

REM Run automation tests and log the output
gradlew run | tee logs\java.log
if %errorlevel% neq 0 (
    echo Automation tests failed. Exiting.
    call :cleanup
    exit /b 1
)

REM Stop Gradle Daemon if it is running
gradlew --stop

REM Call cleanup when script ends
call :cleanup

@echo off

:: Start Appium in a new terminal window
echo Starting Appium in a new terminal window...
start cmd /k "gradlew.bat startAppium"

:: Start the emulator in a new terminal window
echo Starting Emulator in a new terminal window...
start cmd /k "gradlew.bat startEmulator"

:: Wait for the emulator to boot completely
echo Waiting for the emulator to boot...
:boot_check_loop
for /F "tokens=*" %%A in ('adb shell getprop sys.boot_completed') do (
    set boot_completed=%%A
)
if "%boot_completed%"=="1" (
    echo Emulator boot completed.
) else (
    echo Emulator still booting... Waiting 5 seconds.
    timeout /t 5 > nul
    goto boot_check_loop
)

:: Run the automation script in a new terminal window
echo Running the automation script in a new terminal window...
start cmd /k "gradlew.bat runAutomationScript"

echo Automation complete.
exit /b 0
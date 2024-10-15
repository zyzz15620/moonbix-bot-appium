#!/bin/bash

# Manually load variables from local.env without xargs
if [ -f "./local.env" ]; then
    while IFS='=' read -r key value; do
        if [[ $key && $value ]]; then
            # Remove any surrounding quotes from the value, just in case
            value="${value%\"}"
            value="${value#\"}"
            export "$key=$value"
        fi
    done < "./local.env"
else
    echo "local.env file not found. Exiting."
    exit 1
fi

# Start the emulator and save its PID
./gradlew startEmulator > logs/emulator.log 2>&1 &
EMULATOR_PID=$!
if [ -z "$EMULATOR_PID" ]; then
    echo "Failed to start emulator. Exiting."
    exit 1
fi

# Start Appium and save its PID
./gradlew startAppium > logs/appium.log 2>&1 &
APPIUM_PID=$!
if [ -z "$APPIUM_PID" ]; then
    echo "Failed to start Appium. Exiting."
    exit 1
fi

# Function to clean up processes (Emulator and Appium) when the script finishes
cleanup() {
    echo "Stopping Emulator ($device_UID) and Appium (PID: $APPIUM_PID)..."

    # Check and stop Emulator using adb
    if adb devices | grep "$device_UID" > /dev/null; then
        adb -s "$device_UID" emu kill  # Stops the emulator gracefully
        echo "Emulator stopped via adb."
    else
        echo "Emulator was already stopped."
    fi

    # Check and stop Appium if it's still running
    if kill -0 "$APPIUM_PID" 2>/dev/null; then
        pkill -TERM -P "$APPIUM_PID"
        echo "Appium (PID: $APPIUM_PID) has been stopped."
    else
        echo "Appium (PID: $APPIUM_PID) was already stopped."
    fi
}

# Catch Ctrl+C (SIGINT) to stop the processes gracefully
trap cleanup SIGINT

# Wait for Emulator and Appium to fully start before running tests
echo "Waiting for Emulator and Appium to start..."
sleep 10  # This can be improved with actual health checks for readiness

# Run automation tests and log the output, show logs in terminal as well
./gradlew run | tee logs/java.log
if [ $? -ne 0 ]; then
    echo "Automation tests failed. Exiting."
    cleanup
    exit 1
fi

# Stop Gradle Daemon if it is running
./gradlew --stop

# Call the cleanup function when the script ends
cleanup

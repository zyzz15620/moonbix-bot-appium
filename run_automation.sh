#!/bin/bash

# Start the emulator
echo "Starting the emulator..."
./gradlew startEmulator > logs/emulator.log 2>&1 &
EMULATOR_PID=$!
sleep 5  # Give the emulator time to start
if ! kill -0 "$EMULATOR_PID" 2>/dev/null; then
    echo "Failed to start the emulator. Exiting."
    exit 1
fi

# Start Appium
echo "Starting Appium..."
./gradlew startAppium > logs/appium.log 2>&1 &
APPIUM_PID=$!
sleep 5  # Give Appium time to start
if ! kill -0 "$APPIUM_PID" 2>/dev/null; then
    echo "Failed to start Appium. Exiting."
    exit 1
fi

# Wait for both services to be ready
echo "Waiting for Emulator and Appium to fully start..."
sleep 10  # Adjust the sleep duration as needed

# Run automation script
echo "Running automation tests..."
./gradlew runAutomationScript | tee logs/test.log
if [ $? -ne 0 ]; then
    echo "Automation tests failed. Exiting."
    exit 1
fi

# No automatic shutdown of processes
echo "Automation complete. You can stop the emulator and Appium manually."

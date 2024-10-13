#!/bin/bash

./gradlew startEmulator > logs/emulator.log 2>&1 &
EMULATOR_PID=$!

./gradlew startAppium > logs/appium.log 2>&1 &
APPIUM_PID=$!

cleanup() {
    echo "Stopping Emulator (PID: $EMULATOR_PID) and Appium (PID: $APPIUM_PID)..."
    kill $EMULATOR_PID
    kill $APPIUM_PID
}

# Bắt tín hiệu Control + C (SIGINT) để dừng các tiến trình
trap cleanup SIGINT

# Chạy automation script và ghi log vào file, đồng thời hiển thị log trên terminal
./gradlew run | tee logs/java.log

# Dừng Appium và Emulator sau khi script kết thúc
cleanup

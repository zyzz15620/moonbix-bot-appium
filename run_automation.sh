#!/bin/bash

# Khởi động emulator và lưu PID
./gradlew startEmulator > logs/emulator.log 2>&1 &
EMULATOR_PID=$!

# Khởi động Appium và lưu PID
./gradlew startAppium > logs/appium.log 2>&1 &
APPIUM_PID=$!

# Hàm dọn dẹp để tắt Appium và Emulator khi script kết thúc
cleanup() {
    echo "Stopping Emulator (PID: $EMULATOR_PID) and Appium (PID: $APPIUM_PID)..."

    # Kiểm tra và dừng Emulator nếu tiến trình còn tồn tại
    if kill -0 $EMULATOR_PID 2>/dev/null; then
        kill $EMULATOR_PID
        echo "Emulator (PID: $EMULATOR_PID) đã được tắt."
    else
        echo "Emulator (PID: $EMULATOR_PID) đã dừng trước đó."
    fi

    # Kiểm tra và dừng Appium nếu tiến trình còn tồn tại
    if kill -0 $APPIUM_PID 2>/dev/null; then
        kill $APPIUM_PID
        echo "Appium (PID: $APPIUM_PID) đã được tắt."
    else
        echo "Appium (PID: $APPIUM_PID) đã dừng trước đó."
    fi
}

# Bắt tín hiệu Control + C (SIGINT) để dừng các tiến trình
trap cleanup SIGINT

# Chạy automation script và ghi log vào file, đồng thời hiển thị log trên terminal
./gradlew run | tee logs/java.log

# Gọi hàm cleanup khi script kết thúc
cleanup

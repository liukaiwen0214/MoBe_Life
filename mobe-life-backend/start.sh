#!/bin/bash

APP_NAME="mobe-life-backend-0.0.1-SNAPSHOT.jar"
APP_PATH="target/$APP_NAME"
PID_FILE="app.pid"
LOG_DIR="logs"
LOG_FILE="$LOG_DIR/start.log"

mkdir -p "$LOG_DIR"

if [ -f "$PID_FILE" ]; then
  PID=$(cat "$PID_FILE")
  if ps -p "$PID" > /dev/null 2>&1; then
    echo "Application is already running, PID: $PID"
    exit 1
  else
    rm -f "$PID_FILE"
  fi
fi

nohup java -jar "$APP_PATH" > "$LOG_FILE" 2>&1 &
echo $! > "$PID_FILE"

sleep 2

PID=$(cat "$PID_FILE")
if ps -p "$PID" > /dev/null 2>&1; then
  echo "Application started successfully, PID: $PID"
  echo "Log file: $LOG_FILE"
else
  echo "Application failed to start"
  exit 1
fi
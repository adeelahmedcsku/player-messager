@echo off
REM Script to run the single-process version
REM Both players run in the same JVM process

echo Building project...
call mvn clean package -q

if %ERRORLEVEL% NEQ 0 (
    echo Build failed!
    exit /b 1
)

echo.
echo Running single-process version...
echo.

java -cp target\player-messaging-1.0.0.jar com.player.messaging.SingleProcessMain
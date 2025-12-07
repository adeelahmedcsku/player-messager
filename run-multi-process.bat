@echo off
REM Script to run the multi-process version
REM Each player runs in a separate JVM process with different PIDs

echo Building project...
call mvn clean package -q

if %ERRORLEVEL% NEQ 0 (
    echo Build failed!
    exit /b 1
)

echo.
echo Starting multi-process version...
echo.

REM Start responder in background
echo Starting Responder process...
start /B "Responder" java -cp target\player-messaging-1.0.0.jar com.player.messaging.MultiProcessMain responder > responder.log 2>&1

REM Wait for responder to start
timeout /t 2 /nobreak > nul

REM Start initiator in foreground
echo.
echo Starting Initiator process...
java -cp target\player-messaging-1.0.0.jar com.player.messaging.MultiProcessMain initiator

REM Kill responder after initiator completes
echo.
echo Stopping Responder process...
taskkill /FI "WindowTitle eq Responder*" /T /F > nul 2>&1

echo.
echo Both processes terminated
echo.
echo Responder log:
type responder.log
del responder.log
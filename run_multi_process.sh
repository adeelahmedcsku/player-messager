#!/bin/bash

# Script to run the multi-process version
# Each player runs in a separate JVM process with different PIDs

echo "Building project..."
mvn clean package -q

if [ $? -ne 0 ]; then
    echo "Build failed!"
    exit 1
fi

echo ""
echo "Starting multi-process version..."
echo ""

# Start responder in background
echo "Starting Responder process..."
java -cp target/player-messaging-1.0.0.jar com.player.messaging.MultiProcessMain responder > responder.log 2>&1 &
RESPONDER_PID=$!
echo "Responder PID: $RESPONDER_PID"

# Wait for responder to start
sleep 2

# Start initiator in foreground
echo ""
echo "Starting Initiator process..."
java -cp target/player-messaging-1.0.0.jar com.player.messaging.MultiProcessMain initiator

# Kill responder after initiator completes
echo ""
echo "Stopping Responder process..."
kill $RESPONDER_PID 2>/dev/null
wait $RESPONDER_PID 2>/dev/null

echo ""
echo "Both processes terminated"
echo ""
echo "Responder log:"
cat responder.log
rm -f responder.log
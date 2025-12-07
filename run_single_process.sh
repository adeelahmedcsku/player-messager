#!/bin/bash

# Script to run the single-process version
# Both players run in the same JVM process

echo "Building project..."
mvn clean package -q

if [ $? -ne 0 ]; then
    echo "Build failed!"
    exit 1
fi

echo ""
echo "Running single-process version..."
echo ""

java -cp target/player-messaging-1.0.0.jar com.player.messaging.SingleProcessMain
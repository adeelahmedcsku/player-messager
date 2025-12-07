# Player Messaging System

A clean, well-designed Java application demonstrating inter-player messaging with both single-process and multi-process implementations.

## Design Overview

### Architecture

The system follows a layered architecture with clear separation of concerns:

1. **Message Layer**: Immutable data transfer objects
2. **Transport Layer**: Abstract communication mechanism
3. **Player Layer**: Business logic for message handling
4. **Application Layer**: Orchestration and lifecycle management

### Key Design Principles

- **Dependency Inversion**: Players depend on `MessageTransport` abstraction, not concrete implementations
- **Single Responsibility**: Each class has one clear purpose
- **Open/Closed**: Easy to add new transport mechanisms without modifying existing code
- **Strategy Pattern**: Transport mechanism can be swapped at runtime

### Class Responsibilities

#### Message
- Encapsulates message content and counter
- Provides immutable data transfer between players

#### MessageHandler (Interface)
- Defines contract for receiving messages
- Enables loose coupling between transport and player logic

#### MessageTransport (Interface)
- Abstracts communication mechanism
- Supports both in-process and inter-process implementations
- Manages lifecycle (start/stop)

#### Player
- Sends messages with incrementing counter
- Receives messages and generates responses
- Concatenates received content with its own counter
- Delegates actual transport to MessageTransport implementation

#### InProcessTransport
- Implements transport for single-process communication
- Uses BlockingQueue for thread-safe message passing
- Asynchronous delivery via dedicated thread

#### SocketTransport
- Implements transport for multi-process communication
- Uses TCP sockets for inter-process messaging
- Handles both server (listener) and client (connector) roles
- Serializes/deserializes messages using ObjectStreams

#### SingleProcessMain
- Orchestrates single-process execution
- Creates two players with in-process transport
- Monitors stop condition (10 messages sent/received by initiator)
- Gracefully shuts down system

#### MultiProcessMain
- Orchestrates multi-process execution
- Supports both initiator and responder roles
- Uses socket transport for communication
- Each instance runs in separate JVM with different PID

## Building and Running
### Single Process Mode
```bash
./run-single-process.sh
```

### Multi Process Mode
```bash
./run-multi-process.sh
```
**On Windows:**
```batch
run-single-process.bat
```

**On Windows:**
```batch
run-multi-process.bat
```
### Manual Build
```bash
mvn clean package
```


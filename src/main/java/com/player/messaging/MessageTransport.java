package com.player.messaging;

/**
 * Abstraction for message transport mechanism.
 *
 * Responsibilities:
 * - Define contract for sending messages
 * - Define contract for receiving messages via handler
 * - Enable different transport implementations (in-process, inter-process)
 * - Manage lifecycle (start/stop)
 */
public interface MessageTransport {
    /**
     * Set the handler for incoming messages.
     */
    void setMessageHandler(MessageHandler handler);

    /**
     * Send a message.
     */
    void send(Message message);

    /**
     * Start the transport (e.g., open connections, start listening).
     */
    void start();

    /**
     * Stop the transport and cleanup resources.
     */
    void stop();
}
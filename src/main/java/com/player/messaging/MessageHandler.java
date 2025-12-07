package com.player.messaging;

/**
 * Interface for handling incoming messages.
 *
 * Responsibilities:
 * - Define contract for message reception
 * - Enable decoupling between message transport and message processing
 */
public interface MessageHandler {
    /**
     * Called when a message is received.
     *
     * @param message the received message
     */
    void onMessageReceived(Message message);
}
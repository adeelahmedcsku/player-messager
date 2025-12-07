package com.player.messaging;

import java.io.Serializable;

/**
 * Represents a message exchanged between players.
 *
 * Responsibilities:
 * - Encapsulate message content
 * - Track message counter
 * - Provide immutable data transfer object for player communication
 */
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String content;
    private final int counter;

    public Message(String content, int counter) {
        this.content = content;
        this.counter = counter;
    }

    public String getContent() {
        return content;
    }

    public int getCounter() {
        return counter;
    }

    @Override
    public String toString() {
        return "Message{content='" + content + "', counter=" + counter + "}";
    }
}
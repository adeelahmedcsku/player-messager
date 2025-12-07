package com.player.messaging;

/**
 * Represents a player in the messaging system.
 *
 * Responsibilities:
 * - Send messages to other players
 * - Receive and process incoming messages
 * - Maintain message counter for sent messages
 * - Generate response messages by concatenating received content with counter
 */
public class Player implements MessageHandler {
    private final String name;
    private final MessageTransport transport;
    private int messageCounter = 0;

    public Player(String name, MessageTransport transport) {
        this.name = name;
        this.transport = transport;
        this.transport.setMessageHandler(this);
    }

    public String getName() {
        return name;
    }

    public void sendMessage(String content) {
        messageCounter++;
        Message message = new Message(content, messageCounter);
        System.out.println("[" + name + "] Sending: " + message);
        transport.send(message);
    }

    @Override
    public void onMessageReceived(Message message) {
        System.out.println("[" + name + "] Received: " + message);

        // Respond with concatenated content + counter
        String responseContent = message.getContent() + messageCounter;
        sendMessage(responseContent);
    }

    public void start() {
        transport.start();
    }

    public void stop() {
        transport.stop();
    }
}
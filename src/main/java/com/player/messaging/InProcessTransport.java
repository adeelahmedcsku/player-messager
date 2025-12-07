package com.player.messaging;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * In-process message transport using blocking queues.
 *
 * Responsibilities:
 * - Deliver messages within the same JVM process
 * - Use separate threads for asynchronous message delivery
 * - Manage message queue and delivery thread lifecycle
 */
public class InProcessTransport implements MessageTransport {
    private final BlockingQueue<Message> messageQueue = new LinkedBlockingQueue<>();
    private MessageHandler handler;
    private Thread deliveryThread;
    private volatile boolean running = false;

    @Override
    public void setMessageHandler(MessageHandler handler) {
        this.handler = handler;
    }

    @Override
    public void send(Message message) {
        try {
            messageQueue.put(message);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to send message", e);
        }
    }

    @Override
    public void start() {
        running = true;
        deliveryThread = new Thread(() -> {
            while (running) {
                try {
                    Message message = messageQueue.take();
                    if (handler != null) {
                        handler.onMessageReceived(message);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        deliveryThread.start();
    }

    @Override
    public void stop() {
        running = false;
        if (deliveryThread != null) {
            deliveryThread.interrupt();
            try {
                deliveryThread.join(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
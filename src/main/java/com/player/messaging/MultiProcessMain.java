package com.player.messaging;

/**
 * Main class for multi-process execution (Requirement 7).
 *
 * Responsibilities:
 * - Create a player that communicates via sockets
 * - Support both initiator and responder roles
 * - Determine role from command-line arguments
 * - Monitor for stop condition (for initiator)
 * - Gracefully shutdown when done
 */
public class MultiProcessMain {
    private static final int MAX_MESSAGES = 10;
    private static final int PORT = 9999;

    public static void main(String[] args) throws InterruptedException {
        if (args.length < 1) {
            System.err.println("Usage: java MultiProcessMain <initiator|responder>");
            System.exit(1);
        }

        String role = args[0];
        boolean isInitiator = role.equalsIgnoreCase("initiator");

        System.out.println("=== Multi Process Mode ===");
        System.out.println("Role: " + role);
        System.out.println("PID: " + getProcessId());

        MessageTransport transport;
        CountingPlayer player;

        if (isInitiator) {
            transport = new SocketTransport(PORT, "localhost", false);
            player = new CountingPlayer("Initiator", transport, true);
            player.start();

            Thread.sleep(1000);
            player.sendMessage("Hello");

            while (!player.shouldStop()) {
                Thread.sleep(100);
            }

            System.out.println("\n=== Stop condition reached ===");
            System.out.println("Initiator sent: " + player.getSentCount() + " messages");
            System.out.println("Initiator received: " + player.getReceivedCount() + " messages");

        } else {
            transport = new SocketTransport(PORT, true);
            player = new CountingPlayer("Responder", transport, false);
            player.start();

            System.out.println("Responder waiting for messages...");
            System.out.println("Press Ctrl+C to stop");

            Thread.sleep(Long.MAX_VALUE);
        }

        player.stop();
        System.out.println("=== Graceful shutdown complete ===");
    }

    private static String getProcessId() {
        try {
            String jvmName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
            return jvmName.split("@")[0];
        } catch (Exception e) {
            return "unknown";
        }
    }

    /**
     * Extended Player that tracks message counts and determines stop condition.
     */
    static class CountingPlayer extends Player {
        private final boolean isInitiator;
        private int sentCount = 0;
        private int receivedCount = 0;

        public CountingPlayer(String name, MessageTransport transport, boolean isInitiator) {
            super(name, transport);
            this.isInitiator = isInitiator;
        }

        @Override
        public void sendMessage(String content) {
            sentCount++;
            super.sendMessage(content);
        }

        @Override
        public void onMessageReceived(Message message) {
            receivedCount++;
            System.out.println("[" + getName() + "] Received: " + message);

            // Only respond if we haven't reached the stop condition
            if (!shouldStop()) {
                String responseContent = message.getContent() + (sentCount);
                sendMessage(responseContent);
            } else {
                System.out.println("[" + getName() + "] Stop condition met, not responding");
            }
        }

        public boolean shouldStop() {
            return isInitiator && sentCount >= MAX_MESSAGES && receivedCount >= MAX_MESSAGES;
        }

        public int getSentCount() {
            return sentCount;
        }

        public int getReceivedCount() {
            return receivedCount;
        }
    }
}
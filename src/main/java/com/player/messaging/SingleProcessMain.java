package com.player.messaging;

/**
 * Main class for single-process execution (Requirement 5).
 *
 * Responsibilities:
 * - Create two players in the same JVM process
 * - Set up in-process communication between players
 * - Initiate message exchange
 * - Monitor for stop condition (10 messages sent and received by initiator)
 * - Gracefully shutdown the system
 */
public class SingleProcessMain {
    private static final int MAX_MESSAGES = 10;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Single Process Mode ===");
        System.out.println("PID: " + getProcessId());

        MessageTransport transport1 = new InProcessTransport();
        MessageTransport transport2 = new InProcessTransport();

        CountingPlayer player1 = new CountingPlayer("Player1", transport1, true);
        CountingPlayer player2 = new CountingPlayer("Player2", transport2, false);

        ((InProcessTransport) transport1).setMessageHandler(player2);
        ((InProcessTransport) transport2).setMessageHandler(player1);

        player1.start();
        player2.start();

        player1.sendMessage("Hello");

        while (!player1.shouldStop()) {
            Thread.sleep(100);
        }

        System.out.println("\n=== Stop condition reached ===");
        System.out.println("Player1 sent: " + player1.getSentCount() + " messages");
        System.out.println("Player1 received: " + player1.getReceivedCount() + " messages");

        player1.stop();
        player2.stop();

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
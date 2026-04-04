package com.example.worker.consumer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MessageConsumerTest {

    private MessageConsumer messageConsumer;

    @BeforeEach
    void setUp() {
        messageConsumer = new MessageConsumer();
    }

    @Test
    void testReceiveMessage_Success() {
        // Arrange
        String testMessage = "Hello SQS Message";

        // Act & Assert
        assertDoesNotThrow(() -> {
            messageConsumer.receiveMessage(testMessage);
        });
    }

    @Test
    void testReceiveMessage_NullMessage() {
        // Note: The current implementation might throw NullPointerException if it tries to log null.
        // But the message content logic is just logging and a sleep.

        // Act & Assert
        assertDoesNotThrow(() -> {
            messageConsumer.receiveMessage(null);
        });
    }

    @Test
    void testReceiveMessage_EmptyMessage() {
        // Arrange
        String emptyMessage = "";

        // Act & Assert
        assertDoesNotThrow(() -> {
            messageConsumer.receiveMessage(emptyMessage);
        });
    }
}

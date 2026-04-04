package com.example.worker.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import io.awspring.cloud.sqs.annotation.SqsListener;

@Component
@ConditionalOnProperty(name = "aws.sqs.consumer.enabled", havingValue = "true", matchIfMissing = true)
public class MessageConsumer {

    private static final Logger logger = LoggerFactory.getLogger(MessageConsumer.class);

    /**
     * Listens to SQS queue and processes incoming messages
     * This method is automatically triggered when a message arrives in the queue
     */
    @SqsListener("${aws.sqs.queue-name:worker-queue}")
    public void receiveMessage(String message) {
        logger.info("Received message from SQS: {}", message);

        try {
            // Implement actual business logic here
            processMessage(message);

            logger.info("Message processed successfully: {}", message);
        } catch (Exception e) {
            logger.error("Error processing message: {}", message, e);
            // Message will be returned to queue for retry based on SQS configuration
            throw e;
        }
    }

    /**
     * Process the received message
     *
     * @param message The message content
     */
    private void processMessage(String message) {
        // Simulate processing
        logger.info("Processing message...");

        // Implement actual business logic here
        // Examples: Save to database, call external API, transform data, etc.

        // Simulate some work
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Processing interrupted", e);
        }

        logger.info("Message processing completed");
    }
}

package com.example.worker.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.awspring.cloud.sqs.operations.SqsTemplate;

@Component
public class MessageProducer {

    private static final Logger logger = LoggerFactory.getLogger(MessageProducer.class);

    private final SqsTemplate sqsTemplate;

    @Value("${aws.sqs.queue-name:worker-queue}")
    private String queueName;

    public MessageProducer(SqsTemplate sqsTemplate) {
        this.sqsTemplate = sqsTemplate;
    }

    /**
     * Send a message to SQS queue
     *
     * @param message The message to send
     */
    public void sendMessage(String message) {
        try {
            logger.info("Sending message to SQS: {}", message);
            sqsTemplate.send(queueName, message);
            logger.info("Message sent successfully to queue: {}", queueName);
        } catch (Exception e) {
            logger.error("Error sending message to SQS", e);
            throw new RuntimeException("Failed to send message", e);
        }
    }
}

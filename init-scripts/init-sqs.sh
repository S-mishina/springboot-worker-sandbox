#!/bin/bash

# Wait for LocalStack to be ready
echo "Waiting for LocalStack to be ready..."
sleep 5

# Create SQS queue
echo "Creating SQS queue: worker-queue"
awslocal sqs create-queue --queue-name worker-queue

echo "SQS queue created successfully"

# List queues to verify
echo "Available queues:"
awslocal sqs list-queues

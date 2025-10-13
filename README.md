# Spring Boot Worker Application with AWS SQS

A message queue-based worker application using Spring Boot and AWS SQS (with LocalStack for local development).

## Overview

This application is a worker process that consumes messages from AWS SQS queue using Spring Cloud AWS. It demonstrates a production-ready pattern for building scalable, event-driven applications.

## Features

- **Message Queue Consumer**: Processes messages from AWS SQS queue
- **LocalStack Integration**: Local AWS SQS mock for development
- **Automatic Message Processing**: Event-driven architecture
- **Error Handling**: Automatic retry on failure
- **Scalable**: Multiple consumers can run in parallel
- **Spring Cloud AWS**: Native Spring integration with AWS services

## Architecture

```
[Message Producer] → [AWS SQS Queue] → [Worker Consumer] → [Business Logic]
                         ↓
                   [LocalStack]
                   (Local Development)
```

## Project Structure

```
springboot-worker-sandbox/
├── build.gradle                                    # Gradle build configuration
├── docker-compose.yml                              # LocalStack configuration
├── init-scripts/
│   └── init-sqs.sh                                 # SQS queue initialization script
├── src/
│   └── main/
│       ├── java/
│       │   └── com/example/worker/
│       │       ├── WorkerApplication.java          # Main application class
│       │       ├── config/
│       │       │   └── AwsSqsConfig.java          # AWS SQS configuration
│       │       ├── consumer/
│       │       │   └── MessageConsumer.java       # SQS message consumer
│       │       └── producer/
│       │           └── MessageProducer.java       # SQS message producer (for testing)
│       └── resources/
│           └── application.properties              # Application configuration
└── README.md
```

## Requirements

- Java 17 or higher
- Docker & Docker Compose (for LocalStack)
- Gradle (Gradle Wrapper included)

## Setup

### 1. Start LocalStack

Start LocalStack with Docker Compose:

```bash
docker-compose up -d
```

This will:

- Start LocalStack on port 4566
- Automatically create the `worker-queue` SQS queue
- Set up the local AWS environment

Verify LocalStack is running:

```bash
docker ps
```

### 2. Build the Application

```bash
./gradlew build
```

## Running the Application

### Start the Worker Application

```bash
./gradlew bootRun
```

The application will:

- Connect to LocalStack SQS
- Start listening for messages on the `worker-queue`
- Process messages as they arrive

### Send Test Messages

You can send messages to the queue using AWS CLI (with LocalStack):

```bash
# Send a single message
AWS_ACCESS_KEY_ID=test AWS_SECRET_ACCESS_KEY=test aws --endpoint-url=http://localhost:4566 sqs send-message \
  --queue-url http://sqs.ap-northeast-1.localhost.localstack.cloud:4566/000000000000/worker-queue \
  --message-body "Hello from SQS!"

# Send multiple messages
AWS_ACCESS_KEY_ID=test AWS_SECRET_ACCESS_KEY=test aws --endpoint-url=http://localhost:4566 sqs send-message \
  --queue-url http://sqs.ap-northeast-1.localhost.localstack.cloud:4566/000000000000/worker-queue \
  --message-body "Test message 1"

AWS_ACCESS_KEY_ID=test AWS_SECRET_ACCESS_KEY=test aws --endpoint-url=http://localhost:4566 sqs send-message \
  --queue-url http://sqs.ap-northeast-1.localhost.localstack.cloud:4566/000000000000/worker-queue \
  --message-body "Test message 2"
```

Or use the awslocal wrapper (if installed):

```bash
awslocal sqs send-message \
  --queue-url http://sqs.ap-northeast-1.localhost.localstack.cloud:4566/000000000000/worker-queue \
  --message-body "Hello from LocalStack!"
```

## Example Output

When messages are received, you'll see logs like:

```
2025-10-14 00:00:00 - c.e.w.consumer.MessageConsumer - Received message from SQS: Hello from SQS!
2025-10-14 00:00:00 - c.e.w.consumer.MessageConsumer - Processing message...
2025-10-14 00:00:01 - c.e.w.consumer.MessageConsumer - Message processing completed
2025-10-14 00:00:01 - c.e.w.consumer.MessageConsumer - Message processed successfully: Hello from SQS!
```

## Configuration

### LocalStack Configuration

Edit `docker-compose.yml` to customize LocalStack settings:

```yaml
environment:
  - SERVICES=sqs        # AWS services to enable
  - DEBUG=1             # Enable debug logging
  - AWS_DEFAULT_REGION=ap-northeast-1
```

### Application Configuration

Edit `src/main/resources/application.properties`:

```properties
# Change queue name
aws.sqs.queue-name=your-queue-name

# Change AWS region
aws.region=us-west-2

# For production, use real AWS (remove endpoint override)
# aws.sqs.endpoint=http://localhost:4566  # Comment this out for production
```

### Customization

#### Modify Message Processing Logic

Edit the `processMessage()` method in `MessageConsumer.java`:

```java
private void processMessage(String message) {
    logger.info("Processing message...");

    // Implement your business logic here
    // Examples:
    // - Save to database
    // - Call external API
    // - Transform and forward data
    // - Send notifications

    logger.info("Message processing completed");
}
```

#### Change Queue Name

Update `application.properties`:

```properties
aws.sqs.queue-name=my-custom-queue
```

And update the initialization script `init-scripts/init-sqs.sh`:

```bash
awslocal sqs create-queue --queue-name my-custom-queue
```

## Scaling

Run multiple instances of the worker to process messages in parallel:

```bash
# Terminal 1
./gradlew bootRun

# Terminal 2
./gradlew bootRun --args='--server.port=8081'

# Terminal 3
./gradlew bootRun --args='--server.port=8082'
```

Messages will be distributed across all running workers.

## Production Deployment

For production with real AWS SQS:

1. Remove or comment out the LocalStack endpoint in `application.properties`:

   ```properties
   # aws.sqs.endpoint=http://localhost:4566
   ```

2. Configure proper AWS credentials:

   ```properties
   aws.access-key-id=${AWS_ACCESS_KEY_ID}
   aws.secret-access-key=${AWS_SECRET_ACCESS_KEY}
   aws.region=ap-northeast-1
   ```

3. Create the SQS queue in AWS:

   ```bash
   aws sqs create-queue --queue-name worker-queue
   ```

4. Deploy the application (e.g., to ECS, EKS, EC2, etc.)

## Troubleshooting

### LocalStack not starting

```bash
# Check Docker logs
docker logs localstack-sqs

# Restart LocalStack
docker-compose down
docker-compose up -d
```

### Queue not found

```bash
# List available queues
awslocal sqs list-queues

# Recreate the queue
awslocal sqs create-queue --queue-name worker-queue
```

### Messages not being consumed

1. Check if the worker is running
2. Verify queue name matches in `application.properties`
3. Check LocalStack logs: `docker logs localstack-sqs`
4. Verify SQS endpoint configuration

## Cleanup

Stop and remove LocalStack:

```bash
docker-compose down
```

Remove LocalStack volumes (optional):

```bash
docker-compose down -v
```

## License

This is a sample project.

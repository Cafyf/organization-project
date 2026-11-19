# Organization Project – Docker Setup and Execution Guide

## 1. Build Docker Images

### Build Producer Image
```bash
docker build -t organization-producer:1.0 ./organization-producer
```

### Build Consumer Image
```bash
docker build -t organization-consumer:1.0 ./organization-consumer
```

### Verify Docker Images
```bash
docker images
```

## 2. Start Containers Using Docker Compose

```bash
docker compose up --build -d
```

### Check Container Status
```bash
docker compose ps
```

## 3. Create Kafka Topics

### Create Organization Topic
```bash
docker exec -it organization-kafka /opt/kafka/bin/kafka-topics.sh --create --topic organization-topic --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
```

### Create Dead Letter Topic (DLT)
```bash
docker exec -it organization-kafka /opt/kafka/bin/kafka-topics.sh --create --topic organization-topic.DLT --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
```

### Verify Kafka Topics
```bash
docker exec -it organization-kafka /opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --list
```

## 4. Monitor Application Logs

### Producer Logs
```bash
docker compose logs -f organization-producer
```

### Consumer Logs
```bash
docker compose logs -f organization-consumer
```

### Kafka Logs
```bash
docker compose logs -f kafka
```

### All Container Logs
```bash
docker compose logs -f
```

## 5. Application Endpoints

| Application | URL |
|---|---|
| Producer | http://localhost:8081 |
| Consumer | http://localhost:8082 |

## 6. Stop Containers

```bash
docker compose down
```

## 7. Stop Containers and Remove Volumes

```bash
docker compose down -v
```

**Note:** The `-v` option removes Docker volumes and may delete persisted Kafka data.
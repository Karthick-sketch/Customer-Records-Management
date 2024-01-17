# Customer-Records-Management

### Kafka

Start the Zookeeper in a new terminal

```shell
bin/zookeeper-server-start.sh config/zookeeper.properties
```

Start the Kafka broker in another terminal

```shell
bin/kafka-server-start.sh config/server.properties
```

### Customer Records Management application

Start the Spring Boot application in a new terminal

```shell
./gradlew bootRun
```

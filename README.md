# Customer-Records-Management

## Requirements

- **Java** version 17
- **MySQL** version 8
- **Apache Kafka** latest version

## Installation

- [JDK 17 installation](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
- [MySQL 8 installation](https://dev.mysql.com/doc/refman/8.0/en/installing.html)
- [Apache kafka](https://kafka.apache.org/quickstart)

## Steps to run the application

### MySQL

Ensure the `MySQL` server is running

```shell
sudo service mysql status
```

Create the `customer_records_management` database in your `MySQL` locally

```shell
CREATE DATABASE customer_records_management;
```

### Apache Kafka

Start the `Zookeeper` in a new terminal

```shell
bin/zookeeper-server-start.sh config/zookeeper.properties
```

Start the `Kafka broker` in another terminal

```shell
bin/kafka-server-start.sh config/server.properties
```

### Customer-Records-Management application

Start the Spring Boot application in a new terminal

```shell
./gradlew bootRun
```
